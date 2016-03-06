function uploadProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.round(evt.loaded * 100 / evt.total);
		document.getElementById('uploadBtn').value = 'Progress: ' + percentComplete.toString() + '%';
	}
	else {
		document.getElementById('uploadBtn').value = 'Progress: Unable to compute';
	}
}

function uploadComplete(evt) {
	document.getElementById('uploadBtn').value = 'Upload';
}

function uploadFailed(evt) {
	document.getElementById('uploadBtn').value = 'Failed';
}

function uploadCanceled(evt) {
	document.getElementById('uploadBtn').value = 'Canceled';
}

function fileSizeCalculator(fileSize) {
	var fileSizeString = "";
	if (fileSize > 1000 * 1000) {
		fileSizeString = (Math.round(fileSize * 100 / (1000 * 1000)) / 100).toString() + 'MB';
	}
	else {
		fileSizeString = (Math.round(fileSize * 100 / 1000) / 100).toString() + 'KB';
	}
	return fileSizeString;
}

function saveData(data) {
	$.ajax({
		url: '/simplexdb/findbyid/' + data,
		type: 'GET',
		statusCode: {
			302: function (response) {
				document.getElementById('saveBtn_' + data).style.backgroundColor = 'green';
			},
			404: function (response) {
				document.getElementById('saveBtn_' + data).style.backgroundColor = 'red';
			}
		}
	});
}

function deleteData(data) {
	$.ajax({
		url: '/simplexdb/delete/' + data,
		type: 'DELETE',
		success: function (response) {
			document.getElementById('saveBtn_' + data).disabled = true;
			document.getElementById('deleteBtn_' + data).disabled = true;
		}
	});
}

function exportData() {
	$.ajax({
		url: '/simplexdb/export/',
		type: 'GET',
		success: function (response) {
			console.log(response);
		}
	});
}

function fileSelected() {
	var files = document.getElementById('fileUploadBtn').files;
	if (files.length > 1) {
		var filesCount = files.length;
		document.getElementById('fileName').innerHTML = 'File count: ' + files.length;
		document.getElementById('fileType').innerHTML = 'Type: multiple';
		var fileSize = 0;
		for (i = 0; i < files.length; i++) {
			fileSize += files[i].size;
		}
		var fileSizeTotal = fileSizeCalculator(fileSize);

		document.getElementById('fileSize').innerHTML = 'Size: ' + fileSizeTotal;
	} else {
		var file = document.getElementById('fileUploadBtn').files[0];
		if (file) {
			var fileSizeTotal = fileSizeCalculator(file.size);

			document.getElementById('fileName').innerHTML = 'Name: ' + file.name;
			document.getElementById('fileSize').innerHTML = 'Size: ' + fileSizeTotal;
			document.getElementById('fileType').innerHTML = 'Type: ' + file.type;
		}
	}
}

function uploadFile() {
	var filesToUpload = document.getElementById('fileUploadBtn').files;
	for (i = 0; i < filesToUpload.length; i++) {
		var formData = new FormData();
		formData.append("fileToUpload", filesToUpload[i]);
		var xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.upload.addEventListener("progress", uploadProgress, false);
		xmlHttpRequest.addEventListener("load", uploadComplete, false);
		xmlHttpRequest.addEventListener("error", uploadFailed, false);
		xmlHttpRequest.addEventListener("abort", uploadCanceled, false);
		xmlHttpRequest.open("POST", "save");
		xmlHttpRequest.send(formData);
	}
}

$(document).ready(function () {
	$('#dataTable').DataTable();
});