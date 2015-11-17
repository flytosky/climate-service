import os, hashlib, shutil
from datetime import datetime, timedelta
import md5
import urllib2
import json
#Added by Chris
import requests, time, json

from flask import jsonify, request, url_for, make_response
from werkzeug import secure_filename

from svc import app
from svc.src.twoDimMap import call_twoDimMap
from svc.src.twoDimSlice3D import call_twoDimSlice3D
from svc.src.timeSeries2D import call_timeSeries2D
from svc.src.twoDimZonalMean import call_twoDimZonalMean
from svc.src.threeDimZonalMean import call_threeDimZonalMean
from svc.src.threeDimVerticalProfile import call_threeDimVerticalProfile
from svc.src.scatterPlot2V import call_scatterPlot2V
from svc.src.conditionalSampling import call_conditionalSampling
from svc.src.conditionalSampling2Var import call_conditionalSampling2Var
#from svc.src.collocation import call_collocation
from svc.src.time_bounds import getTimeBounds
from svc.src.regridAndDownload import call_regridAndDownload
from svc.src.correlationMap import call_correlationMap

from flask import current_app
from functools import update_wrapper

#Added by Chris
#BASE_URL = 'http://einstein.sv.cmu.edu:9000/addServiceExecutionLog?userId={0}&serviceId={1}&purpose={2}&serviceConfiguration={3}&datasetLog={4}&executionStartTime={5}&executionEndTime={6}&parameters={7}'
#BASE_URL = 'http://einstein.sv.cmu.edu:9008/addServiceExecutionLog/{0}/{1}/{2}/{3}/{4}/{5}/{6}'
#BASE_POST_URL = 'http://einstein.sv.cmu.edu:9008/addServiceExecutionLogUsingPost'
BASE_POST_URL_WEI = 'http://einstein.sv.cmu.edu:9034/serviceExecutionLog/addServiceExecutionLog'
BASE_POST_URL_LOCAL = 'http://localhost:9034/serviceExecutionLog/addServiceExecutionLog'
BASE_POST_URL_NEW = 'http://einstein.sv.cmu.edu:9035/serviceExecutionLog/addServiceExecutionLog'

#PARAMETER_POST_URL = 'http://einstein.sv.cmu.edu:9008/addServiceParameter'
HEADERS = {'Content-Type': 'application/json'}
USE_CMU = True
### USE_CMU = False

userIdDict = {"lei": 26, 
              "admin": 1,
              "caquilinger": 2,
              "jbrodie": 3,
              "rbuchholz": 4,
              "fcannon": 5,
              "ochimborazo": 6,
              "mclavner": 7,
              "jgristey": 8,
              "nkille": 9,
              "mlinz": 10,
              "emaroon": 11,
              "gmarques": 12,
              "cmartinezvi": 13,
              "amerrifield": 14,
              "jnanteza": 15,
              "kneff": 16,
              "fpolverari": 17,
              "mroge": 18,
              "ksauter": 19,
              "htseng": 20,
              "abeatriz": 21,
              "hwei": 22,
              "kwillmot": 23,
              "dzermenodia": 24,
              "kzhang": 25}


def crossdomain(origin=None, methods=None, headers=None,
                max_age=21600, attach_to_all=True,
                automatic_options=True):
    if methods is not None:
        methods = ', '.join(sorted(x.upper() for x in methods))
    if headers is not None and not isinstance(headers, basestring):
        headers = ', '.join(x.upper() for x in headers)
    if not isinstance(origin, basestring):
        origin = ', '.join(origin)
    if isinstance(max_age, timedelta):
        max_age = max_age.total_seconds()

    def get_methods():
        if methods is not None:
            return methods

        options_resp = current_app.make_default_options_response()
        return options_resp.headers['allow']

    def decorator(f):
        def wrapped_function(*args, **kwargs):
            if automatic_options and request.method == 'OPTIONS':
                resp = current_app.make_default_options_response()
            else:
                resp = make_response(f(*args, **kwargs))
            if not attach_to_all and request.method != 'OPTIONS':
                return resp

            h = resp.headers

            h['Access-Control-Allow-Origin'] = origin
            h['Access-Control-Allow-Methods'] = get_methods()
            h['Access-Control-Max-Age'] = str(max_age)
            if headers is not None:
                h['Access-Control-Allow-Headers'] = headers
            return resp

        f.provide_automatic_options = False
        return update_wrapper(wrapped_function, f)
    return decorator


def get_host_port(cfg_file):
    myvars = {}
    myfile =  open(cfg_file)
    for line in myfile:
        name, var = line.partition("=")[::2]
        name = name.strip()
        var = var.strip('\n').strip()
        if name is not '' and var is not '':
            myvars[name] = var

    ### print myvars

    return myvars["HOSTNAME"], myvars["PORT"]


@app.route('/svc/twoDimMap', methods=["GET"])
@crossdomain(origin='*')
def displayTwoDimMap():
    """Run displayTwoDimMap"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())    
    


    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, lat1, lat2, months, scale


    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')
    #added by CMU
    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale}
    #/added by CMU


    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lon1+lon2+lat1+lat2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/twoDimMap/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/twoDimMap')
      # instantiate the app. class
      c1 = call_twoDimMap.call_twoDimMap(model, var, startT, endT, lon1, lon2, lat1, lat2, months, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayTwoDimMap()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'


      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 

      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      ### url = 'http://cmacws.jpl.nasa.gov:8090/static/twoDimMap/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/twoDimMap/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/twoDimMap/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayTwoDimMap()")
        message = str(e)




    #TODO call Wei's url
    #added by CMU
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "1"
    purpose = request.args.get('purpose')

    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
        try:
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'
    #/added by CMU  

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })

@app.route('/svc/twoDimMapPOST', methods=["POST"])
@crossdomain(origin='*')
def displayTwoDimMapPOST():
    """Run displayTwoDimMap"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, lat1, lat2, months, scale
    jsonData = request.json

    model = jsonData['model']
    var = jsonData['var']    
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    months = jsonData['months']
    scale = jsonData['scale']

    #added by Chris
    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale}
    #/added by Chris


    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lon1+lon2+lat1+lat2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/twoDimMap/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/twoDimMap')
      # instantiate the app. class
      c1 = call_twoDimMap.call_twoDimMap(model, var, startT, endT, lon1, lon2, lat1, lat2, months, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayTwoDimMap()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 

      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      ### url = 'http://cmacws.jpl.nasa.gov:8090/static/twoDimMap/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/twoDimMap/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/twoDimMap/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayTwoDimMap()")
        message = str(e)
    #TODO call Wei's url
    #added by Chris
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "1"#"13"#"00"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#   serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            print "Something went wrong"
        try:
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'
    #/added by Chris

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/timeSeries2D', methods=["GET"])
@crossdomain(origin='*')
def display_timeSeries2D():
    """Run display_timeSeries2D"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   

    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, lat1, lat2, scale

    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    scale = request.args.get('scale', '')

    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'scale':scale}

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lon1+lon2+lat1+lat2+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/timeSeries2D/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/timeSeries2D')
      # instantiate the app. class
      c1 = call_timeSeries2D.call_timeSeries2D(model, var, startT, endT, lon1, lon2, lat1, lat2, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.display_timeSeries2D()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 

      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      ### url = 'http://cmacws.jpl.nasa.gov:8090/static/timeSeries2D/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/timeSeries2D/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/timeSeries2D/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in display_timeSeries2D()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "3"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'


    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })

@app.route('/svc/twoDimSlice3D', methods=["GET"])
# @app.route('/svc/twoDimSlice3D', methods=["POST"])
@crossdomain(origin='*')
def displayTwoDimSlice3D():

    """Run displayTwoDimSlice3D"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''
    
    # get model, var, start time, end time, pressure_level, lon1, lon2, lat1, lat2, months, scale
    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    pr = request.args.get('pr', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')
    '''
    jsonData = request.json

    model = jsonData['model']
    var = jsonData['var']    
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    pr = jsonData['pr']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    months = jsonData['months']
    scale = jsonData['scale']
    '''
    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'pr':pr, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale}

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'pr: ', pr
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+pr+lon1+lon2+lat1+lat2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/twoDimSlice3D/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/twoDimSlice3D')
      # instantiate the app. class
      c1 = call_twoDimSlice3D.call_twoDimSlice3D(model, var, startT, endT, pr, lon1, lon2, lat1, lat2, months, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayTwoDimSlice3D()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/twoDimSlice3D/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/twoDimSlice3D/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayTwoDimSlice3D()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "4"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })

#@app.route('/svc/twoDimSlice3D', methods=["GET"])
@app.route('/svc/newTwoDimSlice3D', methods=["POST"])
@crossdomain(origin='*')
def displayNewTwoDimSlice3D():

    """Run displayTwoDimSlice3D"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''
    
    # get model, var, start time, end time, pressure_level, lon1, lon2, lat1, lat2, months, scale
    '''
    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    pr = request.args.get('pr', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')
    '''
    jsonData = request.json

    model = jsonData['model']
    var = jsonData['var']    
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    pr = jsonData['pr']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    months = jsonData['months']
    scale = jsonData['scale']
    purpose = jsonData['purpose']
   
    parameters_json = {'model':model, 'var':var, 'start_time':startT,
                       'end_time':endT, 'pr':pr, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale,'purpose':purpose}
    datasets_json = [{'source': model,'variable':var}]

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'pr: ', pr
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+pr+lon1+lon2+lat1+lat2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/twoDimSlice3D/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/twoDimSlice3D')
      # instantiate the app. class
      c1 = call_twoDimSlice3D.call_twoDimSlice3D(model, var, startT, endT, pr, lon1, lon2, lat1, lat2, months, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayTwoDimSlice3D()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/twoDimSlice3D/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/twoDimSlice3D/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayTwoDimSlice3D()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "14"
    #serviceExecutionLogId = "89"
    #"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'datasets': datasets_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_NEW, data=post_json_wei, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/twoDimZonalMean', methods=["GET"])
@crossdomain(origin='*')
def displayTwoDimZonalMean():
    """Run displayTwoDimZonalMean"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lat1, lat2, months, scale

    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')

    parameters_json_wei = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale}

    parameters_json = {'data source':model, 'variable name':var, 'start year-month':startT,
                       'end year-month':endT,
                       'start lat (deg)':lat1, 'end lat (deg)':lat2, 'select months':months,
                       'variable scale':scale}

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lat1+lat2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/twoDimZonalMean/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/twoDimZonalMean')
      # instantiate the app. class
      c1 = call_twoDimZonalMean.call_twoDimZonalMean(model, var, startT, endT, lat1, lat2, months, output_dir, scale)
      # call the app. function
      ### print 'before the call to c1.displayTwoDimZonalMean() ...'
      (message, imgFileName, dataFileName) = c1.displayTwoDimZonalMean()
      ### print 'after the call to c1.displayTwoDimZonalMean()'
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/twoDimZonalMean/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/twoDimZonalMean/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayTwoDimZonalMean()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "2"#"01"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            'Something went wrong with Xing\'s stuff'
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/threeDimZonalMean', methods=["GET"])
@crossdomain(origin='*')
def displayThreeDimZonalMean():
    """Run displayThreeDimZonalMean"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lat1, lat2, pres1, pres2, months, scale

    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    pres1 = request.args.get('pres1', '')
    pres2 = request.args.get('pres2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')

    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 
                       'lat1':lat1, 'lat2':lat2, 'pres1':pres1, 'pres2':pres2,
                       'months':months, 'scale':scale}

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'pres1: ', pres1
    print 'pres2: ', pres2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lat1+lat2+pres1+pres2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/threeDimZonalMean/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/threeDimZonalMean')
      # instantiate the app. class
      c1 = call_threeDimZonalMean.call_threeDimZonalMean(model, var, startT, endT, lat1, lat2, pres1, pres2, months, output_dir, scale)
      # call the app. function
      ### (message, imgFileName) = c1.displayThreeDimZonalMean()
      (message, imgFileName, dataFileName) = c1.displayThreeDimZonalMean()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/threeDimZonalMean/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/threeDimZonalMean/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayThreeDimZonalMean()")
        message = str(e)
    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "5"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'


    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/threeDimVerticalProfile', methods=["GET"])
@crossdomain(origin='*')
def displayThreeDimVerticalProfile():
    """Run displayThreeDimVerticalProfile"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, lat1, lat2, months, scale

    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    months = request.args.get('months', '')
    scale = request.args.get('scale', '')
    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'scale':scale}

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'months: ', months
    print 'scale: ', scale

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lat1+lat2+lon1+lon2+months+scale
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/threeDimVerticalProfile/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/threeDimVerticalProfile')
      # instantiate the app. class
      c1 = call_threeDimVerticalProfile.call_threeDimVerticalProfile(model, var, startT, endT, lon1, lon2, lat1, lat2, months, output_dir, scale)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayThreeDimVerticalProfile()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/threeDimVerticalProfile/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/threeDimVerticalProfile/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayThreeDimVerticalProfile()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "6"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)

    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'
    

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/scatterPlot2V', methods=["GET"])
@crossdomain(origin='*')
def displayScatterPlot2V():
    """Run displayScatterPlot2V"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model1, var1, pres1, model2, var2, pres2, start time, end time, lon1, lon2, lat1, lat2, nSample

    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    pres1 = request.args.get('pres1', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    pres2 = request.args.get('pres2', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    nSample = request.args.get('nSample', '')

    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1,
                       'model2':model2, 'var2':var2, 'pres2':pres2,
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'nSample':nSample}

    print 'model1: ', model1
    print 'var1: ', var1
    print 'pres1: ', pres1
    print 'model2: ', model2
    print 'var2: ', var2
    print 'pres2: ', pres2
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'nSample: ', nSample

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model1+var1+pres1+model2+var2+pres2+startT+endT+lat1+lat2+lon1+lon2+nSample
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/scatterPlot2V/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/scatterPlot2V')
      # instantiate the app. class
      c1 = call_scatterPlot2V.call_scatterPlot2V(model1, var1, pres1, model2, var2, pres2, startT, endT, lon1, lon2, lat1, lat2, nSample, output_dir, 0)
      # call the app. function (0 means the image created is scatter plot)
      ### (message, imgFileName) = c1.displayScatterPlot2V(0)
      (message, imgFileName, dataFileName) = c1.display()
      # chdir back
      os.chdir(current_dir)

      ind1 = message.find('No Data')
      if ind1>0:
        message1 = message[ind1:(ind1+200)]
        message1a = message1.split('\n')
        print message1a[0]
        print message1a[1]
     
      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port
      print 'imgFileName: ', imgFileName

      url = 'http://' + hostname + ':' + port + '/static/scatterPlot2V/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/scatterPlot2V/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 or message.find('No Data') >= 0:
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayScatterPlot2V()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "7"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/diffPlot2V', methods=["GET"])
@crossdomain(origin='*')
def displayDiffPlot2V():
    """Run displayDiffPlot2V"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model1, var1, pres1, model2, var2, pres2, start time, end time, lon1, lon2, lat1, lat2

    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    pres1 = request.args.get('pres1', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    pres2 = request.args.get('pres2', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')

    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1,
                       'model2':model2, 'var2':var2, 'pres2':pres2,
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2}

    print 'model1: ', model1
    print 'var1: ', var1
    print 'pres1: ', pres1
    print 'model2: ', model2
    print 'var2: ', var2
    print 'pres2: ', pres2
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model1+var1+pres1+model2+var2+pres2+startT+endT+lat1+lat2+lon1+lon2
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/diffPlot2V/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/scatterPlot2V')
      # instantiate the app. class
      c1 = call_scatterPlot2V.call_scatterPlot2V(model1, var1, pres1, model2, var2, pres2, startT, endT, lon1, lon2, lat1, lat2, 0, output_dir, 1)
      # call the app. function (1 means the image created is difference plot)
      (message, imgFileName, dataFileName) = c1.display()
      print 'imgFileName: ', imgFileName
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/diffPlot2V/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/diffPlot2V/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayDiffPlot2V()")
        message = str(e)
    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "8"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/conditionalSampling', methods=["GET"])
#@app.route('/svc/conditionalSampling', methods=["POST"])
@crossdomain(origin='*')
def displayConditionalSamp():
    """Run displayConditionalSamp"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model1, var1, start time, end time, lon1, lon2, lat1, lat2, pres1, pres2, months, model2, var2, bin_min, bin_max, bin_n, env_var_plev, displayOpt
    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    pres1 = request.args.get('pres1', '')
    pres2 = request.args.get('pres2', '')
    months = request.args.get('months', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    bin_min = request.args.get('bin_min', '')
    bin_max = request.args.get('bin_max', '')
    bin_n = request.args.get('bin_n', '')
    env_var_plev = request.args.get('env_var_plev', '')
    displayOpt = request.args.get('displayOpt', '')
    '''

    jsonData = request.json   

    model1 = jsonData['model1']
    var1 = jsonData['var1']
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    pres1 = jsonData['pres1']
    pres2 = jsonData['pres2']
    months = jsonData['months']
    model2 = jsonData['model2']
    var2 = jsonData['var2']
    bin_min = jsonData['bin_min']
    bin_max = jsonData['bin_max']
    bin_n = jsonData['bin_n']
    env_var_plev = jsonData['env_var_plev']
    displayOpt = jsonData['displayOpt']

    '''
    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1,
                       'model2':model2, 'var2':var2, 'pres2':pres2,
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'bin_min':bin_min, 'bin_max':bin_max,
                       'bin_n':bin_n, 'env_var_plev':env_var_plev,
                       'displayOpt':displayOpt}

    print 'model1: ', model1
    print 'var1: ', var1
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'pres1: ', pres1
    print 'pres2: ', pres2
    print 'months: ', months
    print 'model2: ', model2
    print 'var2: ', var2
    print 'bin_min: ', bin_min
    print 'bin_max: ', bin_max
    print 'bin_n: ', bin_n
    print 'env_var_plev: ', env_var_plev
    print 'displayOpt: ', displayOpt

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model1+var1+startT+endT+lat1+lat2+lon1+lon2+pres1+pres2+months+model2+var2+bin_min+bin_max+bin_n+env_var_plev+displayOpt
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/conditionalSampling/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/conditionalSampling')
      # instantiate the app. class

      # c1 = call_conditionalSampling.call_conditionalSampling('giss_e2-r', 'clw', '200101', '200212', '0', '360', '-30', '30', '20000', '90000', '5,6,7,8', 'giss_e2-r', 'tos', '294','305','20', '',  './', '6')

      c1 = call_conditionalSampling.call_conditionalSampling(model1, var1, startT, endT, lon1, lon2, lat1, lat2, pres1, pres2, months, model2, var2, bin_min, bin_max, bin_n, env_var_plev, output_dir, displayOpt)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayConditionalSampling()
      print 'imgFileName: ', imgFileName
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/conditionalSampling/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/conditionalSampling/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "9"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })



@app.route('/svc/co-locate', methods=["GET"])
@crossdomain(origin='*')
def displayColocation():
    """Run displayColocation"""
    executionStartTime = int(time.time())     
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''
     
    # get source, target, start_time, end_time
     
    source = request.args.get('source', '')
    target = request.args.get('target', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')

    parameters_json = {'source':source, 'target':target,
                       'startT':startT, 'endT':endT}

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = source+target+startT+endT
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/co-location/' #### + tag
      print 'output_dir: ', output_dir

      ### if not os.path.exists(output_dir):
        ### os.makedirs(output_dir)
       
      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/collocation')
      # instantiate the app. class
     
      c1 = call_collocation.call_collocation(source, target, startT, endT, output_dir)

      # call the app. function
      (message, imgFileName) = c1.display()
      print 'imgFileName: ', imgFileName
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      imgFileName = 'collocation_plot.png'
      ### url = 'http://' + hostname + ':' + port + '/static/conditionalSampling/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/co-location/' + '/' + imgFileName
      print 'url: ', url
      ### dataUrl = 'http://' + hostname + ':' + port + '/static/conditionalSampling/' + tag + '/' + dataFileName
      ### print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "Test .\'\"\\userId"
    serviceId = "displayColocation"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
    serviceConfigurationId = "Test .\'\"\\confId"
    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())
    post_json = {'userId':userId, 'serviceId':serviceId, 'purpose':purpose,
                 'serviceConfigurationId':serviceConfigurationId, 'datasetLogId':datasetLogId,
                 'executionStartTime':executionStartTime, 'executionEndTime':executionEndTime,
                 'parameters': parameters_json}
    #requests.post(BASE_URL, data=post_json)
#    print BASE_URL.format(userId, serviceId, purpose,
#                          serviceConfigurationId, datasetLogId,
#                          executionStartTime, executionEndTime,
#                          json.dumps(post_json))
    req_url = BASE_URL.format(userId, serviceId, purpose,
                          serviceConfigurationId, datasetLogId,
                          executionStartTime, executionEndTime)
    print req_url

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    }) 
   


@app.route('/svc/two_time_bounds', methods=["GET"])
@crossdomain(origin='*')
def displayTwoTimeBounds():
    """Run displayTwoTimeBounds"""
    executionStartTime = int(time.time())     
    # status and message
    success = True
    message = "ok"
   
    # get data source and variable name
    serviceType = request.args.get('serviceType', '')
    source1 = request.args.get('source1', '')
    var1 = request.args.get('var1', '')
    source2 = request.args.get('source2', '')
    var2 = request.args.get('var2', '')
    parameters_json = {'serviceType':serviceType, 'source1':source1,
                       'var1':var1, 'source2':source2, 'var2':var2}

    print 'source1: ', source1
    print 'var:1 ', var1
    print 'source2: ', source2
    print 'var2: ', var2

    retDateList1 = getTimeBounds.getTimeBounds(serviceType, source1, var1)
    print 'retDateList1: ', retDateList1

    if retDateList1[0] is not 0:
      lower1 = int(str(retDateList1[0]))
    else:
      lower1 = 0

    if retDateList1[1] is not 0:
      upper1 = int(str(retDateList1[1]))
    else:
      upper1 = 0

    retDateList2 = getTimeBounds.getTimeBounds(serviceType, source2, var2)
    print 'retDateList2: ', retDateList2

    if retDateList2[0] is not 0:
      lower2 = int(str(retDateList2[0]))
    else:
      lower2 = 0

    if retDateList2[1] is not 0:
      upper2 = int(str(retDateList2[1]))
    else:
      upper2 = 0

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    userId = "Test .\'\"\\userId"
    serviceId = "displayTwoTimeBounds"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
    serviceConfigurationId = "Test .\'\"\\confId"
    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())
#    parameters_json['purpose'] = purpose
#    parameters_json['dataUrl'] = ''#dataUrl
#    parameters_json['plotUrl'] = ''#url
#    post_json = {'userId':userId, 'serviceId':serviceId, 'purpose':purpose,
#                 'serviceConfigurationId':serviceConfigurationId, 'datasetLogId':datasetLogId,
#                 'executionStartTime':executionStartTime, 'executionEndTime':executionEndTime,
#                 'parameters': parameters_json}
#    post_json_wei = {'dataUrl': '', 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
#                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
#                     'parameters': parameters_json, 'url': ''}
#    post_json = json.dumps(post_json)
#    post_json_wei = json.dumps(post_json_wei)
#
#    try:
#        print requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#    except:
#        print 'Something went wrong with Xing\'s stuff'
#        pass
#    try:
#        print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
#        print post_json_wei
#    except:
#        print 'Something went wrong with Wei\'s stuff'
#        pass

    return jsonify({
        'success': success,
        'message': message,
        'time_bounds1': [lower1, upper1],
        'time_bounds2': [lower2, upper2]
    }) 



@app.route('/svc/time_bounds', methods=["GET"])
@crossdomain(origin='*')
def displayTimeBounds():
    """Run displayTimeBounds"""
    executionStartTime = int(time.time())
    # status and message
    success = True
    message = "ok"
   
    # get data source and variable name
    serviceType = request.args.get('serviceType', '')
    source = request.args.get('source', '')
    var = request.args.get('var', '')

    parameters_json = {'serviceType':serviceType, 'source':source, 'var':var}

    print 'source: ', source
    print 'var: ', var

    retDateList = getTimeBounds.getTimeBounds(serviceType, source, var)
    print 'retDateList: ', retDateList

    if retDateList[0] is not 0:
      lower = int(str(retDateList[0]))
    else:
      lower = 0

    if retDateList[1] is not 0:
      upper = int(str(retDateList[1]))
    else:
      upper = 0
    #TODO call Wei's url
    print 'Wei\'s URL called here'
    userId = "Test .\'\"\\userId"
    serviceId = "displayTimeBounds"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
    serviceConfigurationId = "Test .\'\"\\confId"
    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())
    post_json = {'userId':userId, 'serviceId':serviceId, 'purpose':purpose,
                 'serviceConfigurationId':serviceConfigurationId, 'datasetLogId':datasetLogId,
                 'executionStartTime':executionStartTime, 'executionEndTime':executionEndTime,
                 'parameters': parameters_json}

    return jsonify({
        'success': success,
        'message': message,
        'time_bounds': [lower, upper]
    }) 


@app.route('/svc/regridAndDownload', methods=["GET"])
@crossdomain(origin='*')
def regridAndDownload():
    """Run regridAndDownload"""

    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   

    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, dlon, lat1, lat2, dlat, plev

    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    dlon = request.args.get('dlon', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    dlat = request.args.get('dlat', '')
    plev = request.args.get('plev', '')

    #added by CMU
    parameters_json = {'model':model, 'var':var, 'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2, 'dlon':dlon,
                       'lat1':lat1, 'lat2':lat2, 'dlat':dlat,
                       'plev':plev}
    #/added by CMU

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'dlon: ', dlon
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'dlat: ', dlat
    print 'plev: ', plev

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lon1+lon2+dlon+lat1+lat2+dlat+plev
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/regridAndDownload/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/regridAndDownload')
      # instantiate the app. class
      c1 = call_regridAndDownload.call_regridAndDownload(model, var, startT, endT, lon1, lon2, dlon, lat1, lat2, dlat, plev, output_dir)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.regridAndDownload()
      print 'message:', message
      print 'imgFileName:', imgFileName
      print 'dataFileName:', dataFileName
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      ### url = 'http://cmacws.jpl.nasa.gov:8090/static/twoDimMap/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/regridAndDownload/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/regridAndDownload/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)

    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in regridAndDownload()")
        message = str(e)

    print 'Wei\'s URL called here'
    userId = "1";
    serviceId = "12";
    purpose = request.args.get('purpose')
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
    #        try:
    #            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
    #        except:
    #            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/regridAndDownloadPOST', methods=["POST"])
@crossdomain(origin='*')
def regridAndDownloadPOST():
    """Run regridAndDownloadPOST"""

    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model, var, start time, end time, lon1, lon2, dlon, lat1, lat2, dlat, plev

    '''
    model = request.args.get('model', '')
    var = request.args.get('var', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    dlon = request.args.get('dlon', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    dlat = request.args.get('dlat', '')
    plev = request.args.get('plev', '')
    '''

    jsonData = request.json

    model = jsonData['model']
    var = jsonData['var']
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    dlon = jsonData['dlon']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    dlat = jsonData['dlat']
    plev = jsonData['plev']

    print 'model: ', model
    print 'var: ', var
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'dlon: ', dlon
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'dlat: ', dlat
    print 'plev: ', plev

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model+var+startT+endT+lon1+lon2+dlon+lat1+lat2+dlat+plev
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/regridAndDownload/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/regridAndDownload')
      # instantiate the app. class
      c1 = call_regridAndDownload.call_regridAndDownload(model, var, startT, endT, lon1, lon2, dlon, lat1, lat2, dlat, plev, output_dir)
      # call the app. function
      (message, imgFileName, dataFileName) = c1.regridAndDownload()
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      

      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      ### url = 'http://cmacws.jpl.nasa.gov:8090/static/twoDimMap/' + tag + '/' + imgFileName
      url = 'http://' + hostname + ':' + port + '/static/regridAndDownload/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/regridAndDownload/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in regridAndDownload()")
        message = str(e)

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })


@app.route('/svc/correlationMap', methods=["GET"])
@crossdomain(origin='*')
def displayCorrelationMap():
    """Run displayCorrelationMap"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''
 
    # get model1, var1, pres1, model2, var2, pres2, start time, end time, lon1, lon2, lat1, lat2
 
    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    pres1 = request.args.get('pres1', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    pres2 = request.args.get('pres2', '')
    laggedTime = request.args.get('laggedTime', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    #months = request.args.get('months', '')
 
    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1,
                       'model2':model2, 'var2':var2, 'pres2':pres2,
                       'laggedTime':laggedTime,
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 
                       #'months':months
                        }
 
    print 'model1: ', model1
    print 'var1: ', var1
    print 'pres1: ', pres1
    print 'model2: ', model2
    print 'var2: ', var2
    print 'pres2: ', pres2
    print 'laggedTime: ', laggedTime
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    #print 'months: ', months
 
    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir
 
    try:
      seed_str = model1+var1+pres1+model2+var2+pres2+startT+endT+lat1+lat2+lon1+lon2
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/correlationMap/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)
 
      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/correlationMap')
      # instantiate the app. class
      c1 = call_correlationMap.call_correlationMap(model1, var1, pres1, model2, var2, pres2, 
           laggedTime, startT, endT, lon1, lon2, lat1, lat2, 
           #months, 
           output_dir)
      # call the app. function (0 means the image created is scatter plot)
      ### (message, imgFileName) = c1.displayScatterPlot2V(0)
      (message, imgFileName, dataFileName) = c1.display()
      # chdir back
      os.chdir(current_dir)
 
      ind1 = message.find('No Data')
      if ind1>0:
        message1 = message[ind1:(ind1+200)]
        message1a = message1.split('\n')
        print message1a[0]
        print message1a[1]
     
      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
 
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port
      print 'imgFileName: ', imgFileName
 
# zzzz
      url = 'http://' + hostname + ':' + port + '/static/correlationMap/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/correlationMap/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl
 
      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 or message.find('No Data') >= 0:
        success = False
        url = ''
        dataUrl = ''
 
    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir
 
        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir
 
        success = False
        ### message = str("Error caught in displayScatterPlot2V()")
        message = str(e)
 
    #TODO call Wei's url
    print 'Wei\'s URL called here'
    # userId = "1"
    serviceId = "11"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}

    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'
 
    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })



@app.route('/svc/conditionalSampling2Var', methods=["GET"])
@crossdomain(origin='*')
def displayConditionalSamp2Var():
    """Run displayConditionalSamp2Var"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400
    executionStartTime = int(time.time())

    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model1, var1, start time, end time, lon1, lon2, lat1, lat2, pres1, pres2, months, model2, var2, bin_min, bin_max, bin_n, env_var_plev, displayOpt
    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    model3 = request.args.get('model3', '')
    var3 = request.args.get('var3', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    pres1 = request.args.get('pres1', '')
    pres2 = request.args.get('pres2', '')
    pres3 = request.args.get('pres3', '')
    months = request.args.get('months', '')
    bin_min1 = request.args.get('bin_min1', '')
    bin_max1 = request.args.get('bin_max1', '')
    bin_n1 = request.args.get('bin_n1', '')
    bin_min2 = request.args.get('bin_min2', '')
    bin_max2 = request.args.get('bin_max2', '')
    bin_n2 = request.args.get('bin_n2', '')
    env_var_plev1 = request.args.get('env_var_plev1', '')
    env_var_plev2 = request.args.get('env_var_plev2', '')
    displayOpt = request.args.get('displayOpt', '')
    '''

    jsonData = request.json   

    model1 = jsonData['model1']
    var1 = jsonData['var1']
    startT = jsonData['start_time']
    endT = jsonData['end_time']
    lon1 = jsonData['lon1']
    lon2 = jsonData['lon2']
    lat1 = jsonData['lat1']
    lat2 = jsonData['lat2']
    pres1 = jsonData['pres1']
    pres2 = jsonData['pres2']
    months = jsonData['months']
    model2 = jsonData['model2']
    var2 = jsonData['var2']
    bin_min = jsonData['bin_min']
    bin_max = jsonData['bin_max']
    bin_n = jsonData['bin_n']
    env_var_plev = jsonData['env_var_plev']
    displayOpt = jsonData['displayOpt']

    '''
    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1, 'pres2':pres2,
                       'model2':model2, 'var2':var2, 
                       'model3':model3, 'var3':var3, 
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'months':months,
                       'bin_min1':bin_min1, 'bin_max1':bin_max1,
                       'bin_n1':bin_n1, 'env_var_plev1':env_var_plev1,
                       'bin_min2':bin_min2, 'bin_max2':bin_max2,
                       'bin_n2':bin_n2, 'env_var_plev2':env_var_plev2,
                       'displayOpt':displayOpt}

    print 'model1: ', model1
    print 'var1: ', var1
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'pres1: ', pres1
    print 'pres2: ', pres2
    print 'months: ', months
    print 'model2: ', model2
    print 'var2: ', var2
    print 'bin_min1: ', bin_min1
    print 'bin_max1: ', bin_max1
    print 'bin_n1: ', bin_n1
    print 'env_var_plev1: ', env_var_plev1
    print 'bin_min2: ', bin_min2
    print 'bin_max2: ', bin_max2
    print 'bin_n2: ', bin_n2
    print 'env_var_plev2: ', env_var_plev2
    print 'displayOpt: ', displayOpt

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model1+var1+startT+endT+lat1+lat2+lon1+lon2+pres1+pres2+months+model2+var2+model3+var3+bin_min1+bin_max1+bin_n1+env_var_plev1+bin_min2+bin_max2+bin_n2+env_var_plev2+displayOpt
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/conditionalSampling2Var/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/conditionalSampling2Var')
      # instantiate the app. class

      # c1 = call_conditionalSampling.call_conditionalSampling('giss_e2-r', 'clw', '200101', '200212', '0', '360', '-30', '30', '20000', '90000', '5,6,7,8', 'giss_e2-r', 'tos', '294','305','20', '',  './', '6')

      c1 = call_conditionalSampling2Var.call_conditionalSampling2Var(model1, var1, startT, endT, lon1, lon2, lat1, lat2, pres1, pres2, months, 
        model2, var2, bin_min1, bin_max1, bin_n1, env_var_plev1, 
        model3, var3, bin_min2, bin_max2, bin_n2, env_var_plev2, 
        output_dir, displayOpt)

      # call the app. function
      (message, imgFileName, dataFileName) = c1.displayConditionalSampling2Var()
      print 'imgFileName: ', imgFileName
      # chdir back
      os.chdir(current_dir)

      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]
      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port

      url = 'http://' + hostname + ':' + port + '/static/conditionalSampling2Var/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/conditionalSampling2Var/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 :
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "10"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')#"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"

    executionEndTime = int(time.time())
    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400
 
    #New parameters added here.
    #parameters_json['purpose'] = purpose
    #parameters_json['dataUrl'] = dataUrl
    #parameters_json['plotUrl'] = url
    #Xing's
#    post_json = {'userId':userId, 'serviceId':'21', 'purpose':purpose,
#                 'serviceConfigurationId':serviceConfigurationId, 'datasetLogId':datasetLogId,
#                 'executionStartTime':str(executionStartTime), 'executionEndTime':str(executionEndTime),
#                 'parameters': parameters_json,
#                 'plotUrl': url, 'dataUrl': dataUrl}
    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}
 
    post_json_wei_local = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime_local)*1000, 'executionEndTime':long(executionEndTime_local)*1000,
                     'parameters': parameters_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT}


#    post_json = json.dumps(post_json)
    post_json_wei = json.dumps(post_json_wei)
    post_json_wei_local = json.dumps(post_json_wei_local)
    
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_WEI, data=post_json_wei, headers=HEADERS).text
            print requests.post(BASE_POST_URL_LOCAL, data=post_json_wei_local, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })

@app.route('/svc/newScatterPlot2V', methods=["GET"])
@crossdomain(origin='*')
def displayNewScatterPlot2V():

    """Run displayScatterPlot2V"""
    executionStartTime_local = int(time.time())    
    executionStartTime_local = executionStartTime_local - 25200
    if executionStartTime_local < 0 :
      executionStartTime_local += 86400

    executionStartTime = int(time.time())   
    # status and message
    success = True
    message = "ok"
    url = ''
    dataUrl = ''

    # get model1, var1, pres1, model2, var2, pres2, start time, end time, lon1, lon2, lat1, lat2, nSample

    model1 = request.args.get('model1', '')
    var1 = request.args.get('var1', '')
    pres1 = request.args.get('pres1', '')
    model2 = request.args.get('model2', '')
    var2 = request.args.get('var2', '')
    pres2 = request.args.get('pres2', '')
    startT = request.args.get('start_time', '')
    endT = request.args.get('end_time', '')
    lon1 = request.args.get('lon1', '')
    lon2 = request.args.get('lon2', '')
    lat1 = request.args.get('lat1', '')
    lat2 = request.args.get('lat2', '')
    nSample = request.args.get('nSample', '')

    parameters_json = {'model1':model1, 'var1':var1, 'pres1':pres1,
                       'model2':model2, 'var2':var2, 'pres2':pres2,
                       'startT':startT,
                       'endT':endT, 'lon1':lon1, 'lon2':lon2,
                       'lat1':lat1, 'lat2':lat2, 'nSample':nSample}
    datasets_json = [{'source': model1,'variable':var1},{'source': model2,'variable':var2}]

    print 'model1: ', model1
    print 'var1: ', var1
    print 'pres1: ', pres1
    print 'model2: ', model2
    print 'var2: ', var2
    print 'pres2: ', pres2
    print 'startT: ', startT
    print 'endT: ', endT
    print 'lon1: ', lon1
    print 'lon2: ', lon2
    print 'lat1: ', lat1
    print 'lat2: ', lat2
    print 'nSample: ', nSample

    # get where the input file and output file are
    current_dir = os.getcwd()
    print 'current_dir: ', current_dir

    try:
      seed_str = model1+var1+pres1+model2+var2+pres2+startT+endT+lat1+lat2+lon1+lon2+nSample
      tag = md5.new(seed_str).hexdigest()
      output_dir = current_dir + '/svc/static/scatterPlot2V/' + tag
      print 'output_dir: ', output_dir
      if not os.path.exists(output_dir):
        os.makedirs(output_dir)

      # chdir to where the app is
      os.chdir(current_dir+'/svc/src/scatterPlot2V')
      # instantiate the app. class
      c1 = call_scatterPlot2V.call_scatterPlot2V(model1, var1, pres1, model2, var2, pres2, startT, endT, lon1, lon2, lat1, lat2, nSample, output_dir, 0)
      # call the app. function (0 means the image created is scatter plot)
      ### (message, imgFileName) = c1.displayScatterPlot2V(0)
      (message, imgFileName, dataFileName) = c1.display()
      # chdir back
      os.chdir(current_dir)

      ind1 = message.find('No Data')
      if ind1>0:
        message1 = message[ind1:(ind1+200)]
        message1a = message1.split('\n')
        print message1a[0]
        print message1a[1]
     
      hostname, port = get_host_port("host.cfg")
      userId = '1'
      if hostname == 'EC2':
        req = urllib2.Request('http://169.254.169.254/latest/meta-data/public-ipv4')
        response = urllib2.urlopen(req)
        hostname = response.read()

        req2 = urllib2.Request(' http://169.254.169.254/latest/user-data') 
        response2 = urllib2.urlopen(req2) 
        userId = json.loads(response2.read())['username'] 
      
      if userIdDict.has_key(userId):
        userId = userIdDict[userId]

      print 'userId: ', userId

      print 'hostname: ', hostname
      print 'port: ', port
      print 'imgFileName: ', imgFileName

      url = 'http://' + hostname + ':' + port + '/static/scatterPlot2V/' + tag + '/' + imgFileName
      print 'url: ', url
      dataUrl = 'http://' + hostname + ':' + port + '/static/scatterPlot2V/' + tag + '/' + dataFileName
      print 'dataUrl: ', dataUrl

      print 'message: ', message
      if len(message) == 0 or message.find('Error') >= 0 or message.find('error:') >= 0 or message.find('No Data') >= 0:
        success = False
        url = ''
        dataUrl = ''

    except ValueError, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        message = str(e)
    except Exception, e:
        # chdir to current_dir in case the dir is changed to where the app is in the try block
        os.chdir(current_dir)
        print 'change dir back to: ', current_dir

        success = False
        ### message = str("Error caught in displayScatterPlot2V()")
        message = str(e)

    #TODO call Wei's url
    print 'Wei\'s URL called here'
    #userId = "1"
    serviceId = "13"
    #serviceExecutionLogId = "89"
    purpose = request.args.get('purpose')
    #"Test .\'\"\\purpose"
#    serviceConfigurationId = "Test .\'\"\\confId"
#    datasetLogId = "Test .\'\"\\logId"
    executionEndTime = int(time.time())

    executionEndTime_local = int(time.time())
    executionEndTime_local = executionEndTime_local - 25200
    if executionEndTime_local < 0 :
      executionEndTime_local += 86400

    url_json = '?model1='+model1.upper()+'&var1='+var1+'&pres1='+pres1+'&model2='+model2.upper()+'&var2='+var2+'&pres2='+pres2+'&lon1='+lon1+'&lon2='+lon2+'&lat1='+lat1+'&lat2='+lat2+'&nSample='+nSample+'&startT='+startT+'&endT='+endT+'&Image='+url+'&data_url='+dataUrl+'&purpose='+purpose

    post_json_wei = {'dataUrl': dataUrl, 'userId': long(userId), 'serviceId':long(serviceId), 'purpose':purpose,
                     'executionStartTime':long(executionStartTime)*1000, 'executionEndTime':long(executionEndTime)*1000,
                     'parameters': parameters_json, 'datasets': datasets_json, 'url': url, 'datasetStudyStartTime': startT, 'datasetStudyEndTime': endT, 'urlLink': url_json}


    post_json_wei = json.dumps(post_json_wei)
    if USE_CMU:
#        try:
#            requests.post(BASE_POST_URL, data=post_json, headers=HEADERS).text
#        except:
#            pass
        try:
            print post_json_wei
            print requests.post(BASE_POST_URL_NEW, data=post_json_wei, headers=HEADERS).text
        except:
            print 'Something went wrong with Wei\'s stuff'

    return jsonify({
        'success': success,
        'message': message,
        'url': url,
        'dataUrl': dataUrl
    })
