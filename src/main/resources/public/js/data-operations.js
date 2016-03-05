function saveData(data) {
	$.ajax({
		url: '/simplexdb/findbyid/' + data,
		type: 'GET',
		success: function(response) {
			console.log(response);
		}
	});
}

function deleteData(data) {
	$.ajax({
		url: '/simplexdb/delete/' + data,
		type: 'DELETE',
		success: function(response) {
			console.log(response);
		}
	});
}

function exportData() {
	$.ajax({
		url: '/simplexdb/export/',
		type: 'GET',
		success: function(response) {
			console.log(response);
		}
	});
}