
var dropDownCountry;
var dataListState;
var fieldState;

$(document).ready(function() {
	dropDownCountry = $("#country");
	dataListState = $("#listStates");
	fieldState = $("#state");
	
	dropDownCountry.on("change", function() {
		loadStatesForCountry();
		fieldState.val("").focus();
	});
});

function loadStatesForCountry() {
    // lấy ra value của country được chọn trên form
    selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "settings/list_states_by_country/" + countryId;
	
	$.get(url, function(responseJSON) {
		dataListState.empty();
		// lấy ra từng object trong responseJSON
		$.each(responseJSON, function(index, state) {
			// khi 1 coutry được chọn -> load ra all tên states của country đó
			$("<option>").val(state.name).text(state.name).appendTo(dataListState);
		});
		
	}).fail(function() {
		alert('failed to connect to the server!');
	});
}

function checkPasswordMatch(confirmPassword) {
	if (confirmPassword.value != $("#password").val()) {
		confirmPassword.setCustomValidity("Password do not match!");
	} else {
		confirmPassword.setCustomValidity("");
	}
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
} 
         
function showErrorModal(message) {
    showModalDialog("Error", message);
}
     
function showWarningModal(message) {
    showModalDialog("Warning", message);
}
    
    