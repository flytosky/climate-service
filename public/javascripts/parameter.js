$(document).ready(function() {
    $("#ruleAlert").hide();
    $('#dropdownNameDataDiv').hide();
    
    

	$('#addAClimateService').click(function() {
		console.log("beeping");
		myElement = document.getElementById("tbody");
		
		name = document.getElementById("name").value;
		purpose = document.getElementById("purpose").value;
		serviceUrl = document.getElementById("url").value;
		scenario = document.getElementById("scenario").value;
		version = document.getElementById("version").value;
		rootServiceId = document.getElementById("rootServiceId").value;
		if(name == "" || purpose == "" || serviceUrl == "" || scenario == "" || version == "" || rootServiceId == "") {
			$("#msg").show("slow");
			return;
		}
		var serviceData = {
			name: name,
			purpose: purpose,
			url: serviceUrl,
			scenario: scenario,
			version: version,
			rootServiceId: rootServiceId
		};
		
		var pageStr = myElement.innerHTML;
		var pageOutput = document.getElementById("output").innerHTML;
		console.log(pageStr);
		//add the output here!!!!!!!
		var obj = {
			dataListContent: dataListContent,
			pageOutput: pageOutput,
			pageString: pageStr,
			name: name,
			purpose: purpose,
			url: serviceUrl
		};
		
		$.ajax({
			url: "savePage",
			data: JSON.stringify(obj),
			headers: {
				'Content-Type': 'application/json'
			},
			type: "POST"
		});
		
		$.ajax({
			url: "add",
			data: JSON.stringify(serviceData),
			headers: {
				'Content-Type': 'application/json'
			},
			type: "POST"
		}).done(function(data) {
			console.log("invoked"); 
			var serviceId = JSON.parse(data);
			console.log("Get the serviceID: " + serviceId);
			
//			var paraData = JSON.stringify(parameterPackage);
			var x;
			for (x in parameterPackage) {
				parameterPackage[x].serviceId = serviceId;
			}
			
			
		    console.log(JSON.stringify(parameterPackage));
			
			$.ajax({
				url: "addAllParameters",
				data: JSON.stringify(parameterPackage),
				headers: {
					'Content-Type': 'application/json'
				},
				type: "POST"
			}).done(location.reload());
			
		});
		
		
		
		
	});
});

function createAutoClosingAlert(delay) {
    $("#ruleAlert").show();
    window.setTimeout(function() { $("#ruleAlert").hide(); }, delay);
}
	//dataList
	var dataListContent = "";
	
	
	//suffix
	var sufTrID = "_trID";
	var sufDetail = "_detail";
	
	//parameter package
	var parameterPackage = [];
	var indexInService = 0;
	var countDelete = 0;
	
	
	
	//rule engine data
	var dataSourceList = [];//initialization ??
	var modelList = [];
	var varList = [];
	var modelAndVar = [];
	var map = {};
	var jsonfiedData ="";
	var dependency_list = "";
	var onchangeText = "";

function addDataSource() {
	source = document.getElementById("dataSource").value;
	dataSourceList.push(source);
	document.getElementById("demo1").value= source ;
	createAutoClosingAlert(500);    
}

function addGroup() {
	group = document.getElementById("dataGroup").value;
	modelList.push(group);   
	document.getElementById("demo1").value= group  ; 
	createAutoClosingAlert(500);
}

function resetDataSource() {
	dataSourceList = [];
}

function uploadVarJs() {
	$("#uploadJsBtn").show();
}

function addVariable() {
	variable= document.getElementById("variable").value;
	varList.push(variable); 
	document.getElementById("demo1").value= variable  ; 
	createAutoClosingAlert(500);
}

function oneClickAdd() {
	addDataSource();
	addGroup();
	addVariable();
}


function continue1() {
	modelAndVar.push(modelList);
	modelAndVar.push(varList);
	map[dataSourceList] = modelAndVar;
	
	jsonfiedData = JSON.stringify(map);
	document.getElementById("preview").value= jsonfiedData;		
	
	dataSourceList = [];
	modelList = [];
	varList = [];
	modelAndVar = [];
	createAutoClosingAlert(500);
}


function addDataList() {
	dataListContent += "var " + dependency_list +"="+ jsonfiedData + ";";
	dependency_list = "";
	jsonfiedData ="";
}

function addDataListInput() { //deprecated
	dataListContent += "var " + dependency_list +"="+ jsonfiedData + ";";
	dependency_list = "";
	jsonfiedData ="";
}

//deprecated
function postAllData() {
	$.ajax({
		url: "ruleEngineData",
		data: jsonfiedData,
		headers: {
			'Content-Type': 'application/json'
		},
		type: "POST"
	}).done(console.log("frontDone"));
}


function resetVariable() {
	modelAndVar = [];
}

function deleteAllData() {
	map = {};
}

function isValidJson(json) {
    try {
        JSON.parse(json);
        return true;
    } catch (e) {
        return false;
    }
}

function validate() {
    var toBeValidated = document.getElementById("preview").value;
    document.getElementById("valid").value = isValidJson(toBeValidated);
    if (isValidJson(toBeValidated)){
        jsonfiedData = toBeValidated;
    } else {
        document.getElementById("preview").value= jsonfiedData;
    }
}


function addDependency() {
	dependency_list = document.getElementById("dependence1").value + document.getElementById("dependence2").value + "_list";
	var dependency1 = document.getElementById("dependence1").value + "_select";
	var dependency2 = document.getElementById("dependence2").value + "_select";	
	onchangeText +=  "put_var('" +dependency1+ "', '"+ dependency2+"', "+dependency_list+");"
	document.getElementById(dependency1).setAttribute("onChange",onchangeText);
	console.log("add dependency finished...");
}

function addDependencyInputArea() { //another button
	dependency_list = document.getElementById("dependence1").value + document.getElementById("dependence2").value + "_list";
	var dependency1 = document.getElementById("dependence1").value + "_select";
	var dependency2 = document.getElementById("dependence2").value + "_detail";	 //sufDetail
	onchangeText +=  "put_var_input('" +dependency1+ "', '"+ dependency2+"', "+dependency_list+");" // set new FUNCTION here
	document.getElementById(dependency1).setAttribute("onChange",onchangeText);
	console.log("add dependency input area finished...");
}


function addButton() {
	var outputName = document.getElementById("outputName");
	var outputUrl = document.getElementById("outputUrl");
	var str = '<button type="button" class="btn btn-success btn-lg" name="' +outputUrl.value + '"  id="' +outputName.value + '" onclick="window.location.href=\''+ outputUrl.value +'\';">'+ outputName.value+'</button>';
	$("#output").append(str);
	
}


function addRow() {
	var isUsingDropdownData = false;
    var name = document.getElementById("parameterNameDescriptive");
    var nameFunc = document.getElementById("parameterNameFunctional");
    var type = document.getElementById("parameterType");
    var values = document.getElementById("parameterValues");// from input area dropdown list
    
    var defaultValues = document.getElementById("defaultValues");
    
    var parameterBag = {};
    
    if (jQuery("input:radio[name=optionsRadios]:checked").val() == "option1") {
    	isUsingDropdownData = true;
    	
    	var rule = "*|Type|*" + type.value+ "*|Value|*" + values.value + "*|DefaultValue|*" + defaultValues.value;
        parameterBag = {"indexInService" : indexInService, "purpose" : name.value, "rule" : rule, "name" : nameFunc.value, "dataRange" : ""};
        parameterPackage.push(parameterBag);
        
    }else {
    	isUsingDropdownData = false;
    	var dataValues = document.getElementById("dropdownNameData").value; // from textarea dropdown list
    	dataValues = JSON.parse(dataValues);
    	console.log(dataValues.APIvalue);
    	
	    var descriptiveValues = null;	    
    	values = dataValues.APIvalue; // this is what the attri is called in TextArea
    	descriptiveValues = dataValues.descriptiveValues;  
    	
    	var tempValue = ""; //for compatible purpose
	    for (var item in values) {
	    	tempValue += values[item];
	    	tempValue += ",";
	    }
	    tempValue = tempValue.substring(0, tempValue.length -1);
	   
	    
	    var rule = "*|Type|*" + type.value+ "*|Value|*" + values + "*|DefaultValue|*" + defaultValues.value;
	    parameterBag = {"indexInService" : indexInService, "purpose" : name.value, "rule" : rule, "name" : nameFunc.value, "dataRange" : ""};
	    parameterPackage.push(parameterBag);
    }
    
    
    
    

    var table = document.getElementById("parameterPreview");

    var rowCount = table.rows.length;

    switch(type.value) {
        case "Input text":
            appendInput(name, nameFunc, defaultValues, indexInService);
            break;
        case "Input area":
            appendArea(name, nameFunc, indexInService);
            break;
        case "Multiple selects":
            appendSelects(name, nameFunc, values, defaultValues, indexInService);
            break;
        case "Radio button":
            appendRadioButton(name, nameFunc, values, defaultValues, indexInService);
            break;
        case "Dropdown list":
        	if (isUsingDropdownData == false) {
            	appendDropdownListText(name, nameFunc, values, descriptiveValues, defaultValues, indexInService);
    		}else {
    			appendDropdownList(name, nameFunc, values, defaultValues, indexInService);
    		}
            break;
        default:
            appendInput(name, nameFunc, defaultValues);
    }
    var paraData = JSON.stringify(parameterPackage);
    document.getElementById("previewPara").value= paraData;
    indexInService++;
}

function appendInput(name, nameFunc, defaultValues, indexInService) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';  //HERE ADD _SELECT
    str += '<td>';

    str += '<input type="text" class="form-control" id="' + nameFunc.value + sufDetail + '" placeholder="' + defaultValues.value + '">'; //changed id to t2

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendArea(name, nameFunc, indexInService) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';
    str += '<td>';

    str += '<textarea class="form-control" rows="5" id="' + text + sufDetail + '">';
    str += '</textarea>';

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendSelects(name, nameFunc, values, defaultValues, indexInService) {
    var res = name.value.split(" ");
    // generate id
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    // generate value array
    var array = values.value.split(",");

    // generate default value array
    var defaultValueArray = defaultValues.value.split(",");
    var defaultValueSet = {};
    for (i = 0; i < defaultValueArray.length; i++) {
        defaultValueSet[defaultValueArray[i].trim()] = true;
    }

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td>';
    for (i = 0; i < array.length; i++) {
        str += '<label class="checkbox-inline"><input type="checkbox" id="' + text + sufDetail + i + '"' +
        'value="' + array[i] + '"';
        if (defaultValueSet[array[i].trim()]) {
            str += ' checked';
        }
        str += '>' + array[i] + '</label>';
    }
    str += '</td>';

    str += '<td><button type="button" class="btn btn-danger"' +
               'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendRadioButton(name, nameFunc, values, defaultValues, indexInService) {
    var res = name.value.split(" ");
    // generate id
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    // generate value array
    var array = values.value.split(",");

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td>';
    for (i = 0; i < array.length; i++) {
    	// name is for grouping
        str += '<label class="radio-inline"><input type="radio" id="' + text + sufDetail + i + '"' +
        'value="' + array[i] + '" name="' + text + sufDetail + '"';
        if (array[i].trim() == defaultValues.value.trim()) {
            str += ' checked';
        }
        str += '>' + array[i] + '</label>';
    }
    str += '</td>';

    str += '<td><button type="button" class="btn btn-danger"' +
               'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendDropdownList(name, nameFunc, values, defaultValues, indexInService) {

    // generate value array
    var array = values.value.split(",");

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td><select class="form-control" id="'+ nameFunc.value +'_select">';
    for (i = 0; i < array.length; i++) {
        str += '<option ';
        str += 'value="' +array[i] + '"';
        if (array[i].trim() == defaultValues.value.trim()) {
            str += ' selected';
        }
        str += '>';
        str += array[i];
        str += '</option>';
    }
    str += '</select></td>';

    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendDropdownListText(name, nameFunc, values, descriptiveValues, defaultValues, indexInService) { // for input area
	
    // generate value array
    var array = values;
    var arrayDes = descriptiveValues;

    var str = "";
    str += '<tr id = "'+ nameFunc.value + sufTrID +'" ><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td><select class="form-control" id="'+ nameFunc.value +'_select">';
    for (i = 0; i < array.length; i++) {
        str += '<option ';
        str += 'value="' +array[i] + '"';
        if (array[i].trim() == defaultValues.value.trim()) {
            str += ' selected';
        }
        str += '>';
        str += arrayDes[i];
        str += '</option>';
    }
    str += '</select></td>';

    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this,' + indexInService + ')">delete</button></td></tr>';

    $("#tbody").append(str);
}

function deleteRow(obj, indexInService) {

    var index = obj.parentNode.parentNode.rowIndex;
    var table = document.getElementById("parameterPreview");
    table.deleteRow(index);
    parameterPackage.splice(indexInService-countDelete, 1);
    countDelete++;
    var paraData = JSON.stringify(parameterPackage);
    document.getElementById("previewPara").value= paraData;
}

function addTable() {

    var myTableDiv = document.getElementById("myDynamicTable");

    var table = document.createElement('TABLE');
    table.border='1';

    var tableBody = document.createElement('TBODY');
    table.appendChild(tableBody);

    for (var i=0; i<3; i++){
       var tr = document.createElement('TR');
       tableBody.appendChild(tr);

       for (var j=0; j<4; j++){
           var td = document.createElement('TD');
           td.width='75';
           td.appendChild(document.createTextNode("Cell " + i + "," + j));
           tr.appendChild(td);
       }
    }
    myTableDiv.appendChild(table);

}

function disableDependencyItem() {
	 if (jQuery("input:radio[name=dependencyRadio]:checked").val() == "option1"){	 
	     	$("#dependencyDetailForm").hide();
	     	$("#cacheForm").hide();
	     }else {    	
	    	 $("#dependencyDetailForm").show();
		     $("#cacheForm").show();
	     }	
}


function disableDropdownItem() {
	 if (jQuery("input:radio[name=optionsRadios]:checked").val() == "option1"){	 
     	document.getElementById("dropdownNameData").disabled = true;
     	document.getElementById("parameterValues").disabled = false;
     }else {    	
     	document.getElementById("dropdownNameData").disabled = false;
     	document.getElementById("parameterValues").disabled = true;
     }
}

function disableItem() {
	
    var type = document.getElementById("parameterType");
   
    switch(type.value) {
        case "Input text":
        	$("#dropdownNameDataDiv").hide();
            document.getElementById("parameterValues").disabled = true;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Input area":
        	$("#dropdownNameDataDiv").hide();
            document.getElementById("parameterValues").disabled = true;
            document.getElementById("defaultValues").disabled = true;
            break;
        case "Multiple selects":
        	$("#dropdownNameDataDiv").hide();
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Radio button":
        	$("#dropdownNameDataDiv").hide();
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Dropdown list":
        	$("#dropdownNameDataDiv").show();
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;         
            document.getElementById("dropdownNameData").disabled = true;
         	document.getElementById("parameterValues").disabled = false;
            break;
    }
}

function sendValues(url) {
    var body = document.getElementById("dynamicTBody");
    // getElementsByTagName will always get an array
    var temp = body.getElementsByTagName("tr");

    // declare a HashMap to store the parameter name and value
    var map = {};
    
    // receive climate service model call url
    map["climateServiceCallUrl"] = url;
    console.log("Climate Service Call url: " + url);

    var len = temp.length;
    var i = 0;
    for (i = 0; i < len; i++) {
    	// key is used for backend climate service model
    	var key = temp[i].getElementsByTagName("td")[0].getAttribute("id");

        var res = key.split(" ");
        // text is used for retrieving dynamic page element by name
        var text = res[0];
        for (j = 1; j < res.length; j++) {
            var temp = res[j].charAt(0).toUpperCase();
            temp += res[j].substring(1);
            text += temp;
        }
        
        var value = "";
        var tagName = temp[i].getElementsByTagName("td")[1].firstElementChild.tagName;
        console.log("tagName: " + tagName);
        switch(tagName) {
        	case "INPUT":
        		value = temp[i].getElementsByTagName("input")[0].value;
	    	    console.log("input test: " + value);
	    	    if (value == null || value.length == 0) {
	    	    	value = temp[i].getElementsByTagName("input")[0].getAttribute("placeholder");
	    	    	console.log("value after using placeholder: " + value);
	    	    }
	    	    break;
        	case "SELECT":
        		var selects = temp[i].getElementsByTagName("td")[1].getElementsByTagName("select")[0].getElementsByTagName("option");
        		
	        	for (var k = 0, length = selects.length; k < length; k++) {
	        	    if (selects[k].selected) {
	        	        value = selects[k].value;	        	        // 1117changeslog
	        	        break;
	        	    }
	        	}
	            break;
        	case "TEXTAREA":
        		value = temp[i].getElementsByTagName("td")[1].getElementsByTagName("textarea")[0].value;
        		break;
        	case "LABEL":
        		var type = temp[i].getElementsByTagName("td")[1].firstElementChild.firstElementChild.getAttribute("Type");
        		if (type == "radio") {
        			var radios = temp[i].getElementsByTagName("td")[1].getElementsByTagName("label");
    	        	for (var k = 0, length = radios.length; k < length; k++) {
    	        	    if (radios[k].firstElementChild.checked) {
    	        	        // do whatever you want with the checked radio
    	        	    	// value is used for passing data and innerHTML is for representing
    	        	        value = radios[k].firstElementChild.value;	        	        
    	        	        break;
    	        	    }
    	        	}
        		} else if (type == "checkbox") {
        			var selects = temp[i].getElementsByTagName("td")[1].getElementsByTagName("label");
    	        	value = "";
    	        	for (var k = 0, length = selects.length; k < length; k++) {
    	        	    if (selects[k].firstElementChild.checked) {
    	        	        // do whatever you want with the checked radio
    	        	        value = value + selects[k].firstElementChild.value + ",";	        	        
    	        	    }
    	        	}
    	        	value = value.substr(0, value.length - 1);
    	        	console.log("multiple selects: " + value);
        		} else {
        			alert("The type of html element is false!");
        		}
        }

        map[key] = value;

        console.log("map key: " + key);
        console.log("map value: " + map[key]);
    } // end of for-loop
    
    
/***********************
    alert(JSON.stringify(map));
    map["model"] = "NASA_AIRS";
    map["var"] = "ta";
    map["start_time"] = "200402";
    map["end_time"] = "200412";
    map["pr"] = "50000";
    map["lon1"] = "0";
    map["lon2"] = "360";
    map["lat1"] = "-90";
    map["lat2"] = "90";
    map["months"] = "1,2,3,4,5,6,7,8,9,10,11,12";
    map["scale"] = "0";
    map["purpose"] = "test";
************************/
    $.ajax({
        url: "climateService/serviceModels",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(map),
        dataType: "text"
    }).done(function(data) {
    	console.log("success");
    	var responseJson = JSON.parse(data);
    	
    	$('#serviceImg').attr("src", responseJson.url);
    	$('#serviceImgLink').attr("href", responseJson.url);
    	$('#comment').html(responseJson.dataUrl);
    	$('#commentLink').attr("href", responseJson.dataUrl);
    	$('#message').html(responseJson.message);
    	$('#downloadButton').attr("onclick","location.href ='" + responseJson.dataUrl + "';");
    	console.log("done!!!!!")
    	var x=document.getElementById("output").getElementsByTagName("button");
    	
    	for (var i = 0; i < x.length; i ++) {
    		var key = responseJson[x[i].getAttribute("name")];
    		var id = x[i].innerHTML;
    		console.log("key    " + key + "    id    " +id);
    		$('#'+id).attr("onclick","location.href ='" + key + "';");
		}   	1
    	
    	
    }).fail(function(xhr, textStatus, errorThrown) {
    	console.log("error!");
    	console.log(xhr);
    	console.log(textStatus);
        console.log(errorThrown);
    })
}

function load() {
    console.log("Page load finished");
}

function showDropdownExample1() {
	$("#parameterValues").val("variable1, variable2, variable3, variable4, variable5");
	$("#defaultValues").val("variable3");
}

function showDropdownExample2() {
	$("#dropdownNameData").val("{\r\n\"APIvalue\" : [\"variable1\", \"variable2\", \"variable3\", \"variable4\", \"variable5\"],\r\n\"descriptiveValues\" : [\"variableName1\", \"variableName2\", \"variableName3\", \"variableName4\", \"variableName5\"]\r\n}");
	$("#defaultValues").val("variable2");
}

function showDependencyExample1() {
	$("#preview").val("{\"Dep1Item1\" : [[\"Group Name\"], [\"Dep2Item1\", \"Dep2Item2\", \"Dep2Item3\"]],\r\n\"Dep1Item2\" : [[\"Group Name\"], [\"Dep2Item3\", \"Dep2Item4\", \"Dep2Item5\"]],\r\n\"Dep1Item3\" : [[\"Group Name\"], [\"Dep2Item4\", \"Dep2Item5\", \"Dep2Item6\"]]}");
	
}

function showDependencyExample2() {
	$("#preview").val("");
	$("#dataSource").val("Dep1Item1");
	$("#dataGroup").val("Group Name");
	$("#variable").val("Dep2Item1");
	
}

function replaceFile(id) {
	console.log(id);
	var x = document.getElementById(id).files[0];
		
	$.ajax({
	    url: "replaceFile",
	    type: "POST",
	    data: x,	    
	    processData: false,
	    contentType: false,
	    success: function (res) {
	    	 console.log("Page load finished");
	    }
    });
	
}