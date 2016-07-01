function formatDate(dateValue) {
	return moment(new Date(dateValue)).format('YYYY/MM/DD HH:mm:ss');
}

function uploadProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.round(evt.loaded * 100 / evt.total);
		document.getElementById("uploadBtn").value = "Progress: " + percentComplete.toString() + "%";
	}
	else {
		document.getElementById("uploadBtn").value = "Progress: Unable to compute";
	}
}

function uploadComplete() {
	document.getElementById("uploadBtn").value = "Upload";
}

function uploadFailed() {
	document.getElementById("uploadBtn").value = "Failed";
}

function uploadCanceled() {
	document.getElementById("uploadBtn").value = "Canceled";
}

function fileSizeCalculator(fileSize) {
	var fileSizeString = "";
	if (fileSize > 1000 * 1000) {
		fileSizeString = (Math.round(fileSize * 100 / (1000 * 1000)) / 100).toString() + "MB";
	}
	else {
		fileSizeString = (Math.round(fileSize * 100 / 1000) / 100).toString() + "KB";
	}
	return fileSizeString;
}

function saveData(data) {
	$.ajax({
		url: "/simplexdb/findbyid/" + data,
		type: "GET",
		statusCode: {
			302() {
				document.getElementById("saveBtn_" + data).style.backgroundColor = "green";
			},
			404() {
				document.getElementById("saveBtn_" + data).style.backgroundColor = "red";
			}
		}
	});
}

function deleteData(data) {
	$.ajax({
		url: "/simplexdb/delete/" + data,
		type: "DELETE",
		success() {
			document.getElementById("saveBtn_" + data).disabled = true;
			document.getElementById("deleteBtn_" + data).disabled = true;
		}
	});
}

function fileSelected() {
	var files = document.getElementById("fileUploadBtn").files;
	var fileSizeTotal = 0;
	if (files.length > 1) {
		document.getElementById("fileName").innerHTML = "File count: " + files.length;
		document.getElementById("fileType").innerHTML = "Type: multiple";
		var fileSize = 0;
		for (var x = 0; x < files.length; x++) {
			fileSize += files[x].size;
		}
		fileSizeTotal = fileSizeCalculator(fileSize);

		document.getElementById("fileSize").innerHTML = "Size: " + fileSizeTotal;
	} else {
		var file = document.getElementById("fileUploadBtn").files[0];
		if (file) {
			fileSizeTotal = fileSizeCalculator(file.size);

			document.getElementById("fileName").innerHTML = "Name: " + file.name;
			document.getElementById("fileSize").innerHTML = "Size: " + fileSizeTotal;
			document.getElementById("fileType").innerHTML = "Type: " + file.type;
		}
	}
}

function uploadFile() {
	var filesToUpload = document.getElementById("fileUploadBtn").files;
	for (var y = 0; y < filesToUpload.length; y++) {
		var formData = new FormData();
		formData.append("fileToUpload", filesToUpload[y]);
		var xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.upload.addEventListener("progress", uploadProgress, false);
		xmlHttpRequest.addEventListener("load", uploadComplete, false);
		xmlHttpRequest.addEventListener("error", uploadFailed, false);
		xmlHttpRequest.addEventListener("abort", uploadCanceled, false);
		xmlHttpRequest.open("POST", "save");
		xmlHttpRequest.send(formData);
	}
}

function drawTable(data) {
	var tableData = '';
	var dataTable = $("#dataTable");
	dataTable.find("tbody").empty();
	for (var i = 0; i < data.length; i++) {
		var rowData = "<tr>";
		rowData += "<td>" + data[i].internalId + "</td>";
		rowData += "<td>" + data[i].name + "</td>";
		rowData += "<td>" + formatDate(data[i].date) + "</td>";
		rowData += "<td>" + data[i].size + "</td>";
		rowData += "<td><button type=submit class='btn btn-default' id=saveBtn_" + data[i].internalId +
			" onclick=saveData('" + data[i].internalId + "');>Save</button>";
		rowData += "<button type=submit class='btn btn-danger' id=deleteBtn_" + data[i].internalId +
			" onclick=deleteData('" + data[i].internalId + "');>Delete</button></td></tr>";
		tableData += rowData;
	}
	dataTable.find("tbody").append(tableData);
}

function findByName() {
	var findByNameString = document.getElementById("findByNameTextField").value;
	if (findByNameString.length == 0) {
		$.get("/simplexdb/findall", function (data) {
			drawTable(data);
		});
	} else {
		$.get("/simplexdb/findbyname/" + findByNameString, function (data) {
			drawTable(data);
		});
	}
}