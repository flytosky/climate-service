function addRow() {

    var name = document.getElementById("parameterName");
    var type = document.getElementById("parameterType");
    var values = document.getElementById("parameterValues");
    var defaultValues = document.getElementById("defaultValues");

    var table = document.getElementById("parameterPreview");

    var rowCount = table.rows.length;

    switch(type.value) {
        case "Input text":
            appendInput(name, defaultValues);
            break;
        case "Input area":
            appendArea(name);
            break;
        case "Multiple selects":
            appendSelects(name, values, defaultValues);
            break;
        case "Radio button":
            appendRadioButton(name, values, defaultValues);
            break;
        case "Dropdown list":
            appendDropdownList(name, values, defaultValues);
            break;
        default:
            appendInput(name, defaultValues);
    }

}

function appendInput(name, defaultValues) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr><td>' + name.value + '</td>';
    str += '<td>';

    str += '<input type="text" class="form-control" id="' + text + '" placeholder="' + defaultValues.value + '">';

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendArea(name) {
    var res = name.value.split(" ");
    var text = res[0];
    for (i = 1; i < res.length; i++) {
        var temp = res[i].charAt(0).toUpperCase();
        temp += res[i].substring(1);
        text += temp;
    }

    var str = "";
    str += '<tr><td>' + name.value + '</td>';
    str += '<td>';

    str += '<textarea class="form-control" rows="5" id="' + text + '">';
    str += '</textarea>';

    str += '</td>';
    str += '<td><button type="button" class="btn btn-danger"' +
                   'onclick="Javascript:deleteRow(this)">delete</button></td></tr>';

    $("#tbody").append(str);
}

function appendSelects(name, values, defaultValues) {
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
    str += '<tr><td>' + name.value + '</td>';

    str += '<td>';
    for (i = 0; i < array.length; i++) {
        str += '<label class="checkbox-inline"><input type="checkbox" id="' + text + i + '"' +
        'value=' + text + '"option' + i + '"';
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

function appendRadioButton(name, values, defaultValues) {
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
    str += '<tr><td>' + name.value + '</td>';

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

function appendDropdownList(name, values, defaultValues) {

    // generate value array
    var array = values.value.split(",");

    var str = "";
    str += '<tr><td>' + name.value + '</td>';

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

function load() {

    console.log("Page load finished");

}