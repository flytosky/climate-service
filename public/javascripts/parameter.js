$(document).ready(function() {
    $("#ruleAlert").hide();

	$('#addAClimateService').click(function() {
		console.log("beeping");
		myElement = document.getElementById("tbody");
		name = document.getElementById("name").value;
		purpose = document.getElementById("purpose").value;
		serviceUrl = document.getElementById("url").value;
		var pageStr = myElement.innerHTML;
		var pageOutput = document.getElementById("output").innerHTML;
		console.log(pageStr);
		//add the output here!!!!!!!
		var obj = {
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
	});
});

function createAutoClosingAlert(delay) {
    $("#ruleAlert").show();
    window.setTimeout(function() { $("#ruleAlert").hide(); }, delay);
}

var dataSourceList = [];//initialization ??
var modelList = [];
var varList = [];
var modelAndVar = [];
var map = {};
var jsonfiedData ="";

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

function addVariable() {
variable= document.getElementById("variable").value;
varList.push(variable); 
document.getElementById("demo1").value= variable  ; 
createAutoClosingAlert(500);
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






function addButton() {
	var outputName = document.getElementById("outputName");
	var outputUrl = document.getElementById("outputUrl");
	//var str = '<button type=\"button\" class=\"btn btn-success btn-lg\" a=\"'+ outputUrl.value+ '\">'+ outputName.value+'</button>';
	//<input type="button" onclick="window.location.href='http://www.google.com';" value="Go to Google" />
	var str = '<button type="button" class="btn btn-success btn-lg" name="' +outputUrl.value + '"  id="' +outputName.value + '" onclick="window.location.href=\''+ outputUrl.value +'\';">'+ outputName.value+'</button>';
	$("#output").append(str);
	
}


function addRow() {

    var name = document.getElementById("parameterNameDescriptive");
    var nameFunc = document.getElementById("parameterNameFunctional");
    var type = document.getElementById("parameterType");
    var values = document.getElementById("parameterValues");
    var defaultValues = document.getElementById("defaultValues");

    var table = document.getElementById("parameterPreview");

    var rowCount = table.rows.length;

    switch(type.value) {
        case "Input text":
            appendInput(name, nameFunc, defaultValues);
            break;
        case "Input area":
            appendArea(name, nameFunc);
            break;
        case "Multiple selects":
            appendSelects(name, nameFunc, values, defaultValues);
            break;
        case "Radio button":
            appendRadioButton(name, nameFunc, values, defaultValues);
            break;
        case "Dropdown list":
            appendDropdownList(name, nameFunc, values, defaultValues);
            break;
        default:
            appendInput(name, nameFunc, defaultValues);
    }

}

function appendInput(name, nameFunc, defaultValues) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr><td id = "' + nameFunc.value + '">' + name.value + '</td>';
    str += '<td>';

    str += '<input type="text" class="form-control" id="' + text + '" placeholder="' + defaultValues.value + '">';

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendArea(name, nameFunc) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr><td id = "' + nameFunc.value + '">' + name.value + '</td>';
    str += '<td>';

    str += '<textarea class="form-control" rows="5" id="' + text + '">';
    str += '</textarea>';

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendSelects(name, nameFunc, values, defaultValues) {
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
    str += '<tr><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td>';
    for (i = 0; i < array.length; i++) {
        str += '<label class="checkbox-inline"><input type="checkbox" id="' + text + i + '"' +
        'value="' + array[i] + '"';
        if (defaultValueSet[array[i].trim()]) {
            str += ' checked';
        }
        str += '>' + array[i] + '</label>';
    }
    str += '</td>';

    str += '<td><button type="button" class="btn btn-danger"' +
               'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendRadioButton(name, nameFunc, values, defaultValues) {
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
    str += '<tr><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td>';
    for (i = 0; i < array.length; i++) {
    	// name is for grouping
        str += '<label class="radio-inline"><input type="radio" id="' + text + i + '"' +
        'value="' + array[i] + '" name=' + text + '"';
        if (array[i].trim() == defaultValues.value.trim()) {
            str += ' checked';
        }
        str += '>' + array[i] + '</label>';
    }
    str += '</td>';

    str += '<td><button type="button" class="btn btn-danger"' +
               'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendDropdownList(name, nameFunc, values, defaultValues) {

    // generate value array
    var array = values.value.split(",");

    var str = "";
    str += '<tr><td id = "' + nameFunc.value + '">' + name.value + '</td>';

    str += '<td><select class="form-control">';
    for (i = 0; i < array.length; i++) {
        str += '<option ';
        if (array[i].trim() == defaultValues.value.trim()) {
            str += ' selected';
        }
        str += '>';
        str += array[i];
        str += '</option>';
    }
    str += '</select></td>';

    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function deleteRow(obj) {

    var index = obj.parentNode.parentNode.rowIndex;
    var table = document.getElementById("parameterPreview");
    table.deleteRow(index);

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


function disableItem() {
    var type = document.getElementById("parameterType");

    switch(type.value) {
        case "Input text":
            document.getElementById("parameterValues").disabled = true;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Input area":
            document.getElementById("parameterValues").disabled = true;
            document.getElementById("defaultValues").disabled = true;
            break;
        case "Multiple selects":
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Radio button":
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;
            break;
        case "Dropdown list":
            document.getElementById("parameterValues").disabled = false;
            document.getElementById("defaultValues").disabled = false;
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
	        	        value = selects[k].innerHTML;	        	        
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

//    alert(JSON.stringify(map));

//    map["model"] = "NASA_AIRS";
//    map["var"] = "ta";
//    map["start_time"] = "200402";
//    map["end_time"] = "200412";
//    map["pr"] = "50000";
//    map["lon1"] = "0";
//    map["lon2"] = "360";
//    map["lat1"] = "-90";
//    map["lat2"] = "90";
//    map["months"] = "1,2,3,4,5,6,7,8,9,10,11,12";
//    map["scale"] = "0";
//    map["purpose"] = "test";

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



function replaceFile(id) {
	console.log(id);
	var x = document.getElementById(id).files[0];
	
	var fileName = "testtttt";
	console.log("!!!!!!!!!!!!!!!!!!");
	console.log(x);
	
	
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