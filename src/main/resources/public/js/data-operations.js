function saveData(data) {
	$.ajax({
		url: '/simplexdb/findbyid/' + data,
		type: 'GET',
		success: function (response) {
			console.log(response);
		}
	});
}

function deleteData(data) {
	$.ajax({
		url: '/simplexdb/delete/' + data,
		type: 'DELETE',
		success: function (response) {
			console.log(response);
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
	var file = document.getElementById('fileUploadBtn').files[0];
	if (file) {
		var fileSize = 0;
		if (file.size > 1024 * 1024)
			fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
		else
			fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';

		document.getElementById('fileName').innerHTML = 'Name: ' + file.name;
		document.getElementById('fileSize').innerHTML = 'Size: ' + fileSize;
		document.getElementById('fileType').innerHTML = 'Type: ' + file.type;
	}
}

function uploadFile() {
	var fd = new FormData();
	fd.append("fileToUpload", document.getElementById('fileUploadBtn').files[0]);
	var xhr = new XMLHttpRequest();
	xhr.upload.addEventListener("progress", uploadProgress, false);
	xhr.addEventListener("load", uploadComplete, false);
	xhr.addEventListener("error", uploadFailed, false);
	xhr.addEventListener("abort", uploadCanceled, false);
	xhr.open("POST", "save");
	xhr.send(fd);
}

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
	document.getElementById('uploadBtn').value = 'The upload has been canceled by the user or the browser dropped the connection';
}

$(document).ready(function () {
	$('#dataTable').DataTable();
});