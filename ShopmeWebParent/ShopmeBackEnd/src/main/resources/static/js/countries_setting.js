
var buttonLoad;
var dropDownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var fieldCountryName;
var labelCountryName;
var fieldCountryCode;

$(document).ready(function() {
     buttonLoad = $("#buttonLoadCountries");   
     dropDownCountry = $("#dropDownCountries");
     
     buttonAddCountry = $("#buttonAddCountry");
     buttonUpdateCountry = $("#buttonUpdateCountry");
     buttonDeleteCountry = $("#buttonDeleteCountry");
     
     labelCountryName = $("#labelCountryName");
     fieldCountryName = $("#fieldCountryName");
     
     fieldCountryCode = $("#fieldCountryCode");
     
     buttonLoad.click(function() {
         loadCountries();
     });
     
     dropDownCountry.on("change", function() {
         changeFormStateToSelectedCountry();
     });
     
     buttonAddCountry.click(function() {
         // check nếu buttonAddCountry là Add -> ta lưu country vào DB
         if (buttonAddCountry.val() == "Add") {
             addCountry();
         } else {
             changeFormStateToNewCountry();
         }
     });
     
     buttonUpdateCountry.click(function() {
         updateCountry();
     });
     
     buttonDeleteCountry.click(function(){
         deleteCountry();
     });
});

function loadCountries() {
    // call restful webservice
    url = contextPath + "countries/list";
    $.get(url, function(responseJSON) {
        dropDownCountry.empty();
        // lấy ra từng object trong responseJSON
        $.each(responseJSON, function(index, country) {
            // get value của đối tượng country
            optionValue = country.id + "-" + country.code;
            // khi click vào button -> load ra all tên của countries
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        });
    }).done(function() {
        // load thành công -> hiện thông báo và nút button có giá trị:         
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded.");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error.");
    });
}

// show thông báo
function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}

function changeFormStateToSelectedCountry() {
    // khi ta chưa chọn 1 country nào -> chỉ được Add, Update và Delete sẽ bị disabled 
    // khi đã load 1 list coutries và chọn 1 country bất kì ở dropDownCountry -> Add thành New và Update, Delete sẽ được enabled
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);
    
    // khi chọn 1 country ở dropDownCountry -> hiển thị name của country đó ở fieldCountryName và labelCountryName đổi thành "Selected Country"
    labelCountryName.text("Selected Country:")
    selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);
    
    // lấy ra code từ dropDownCountry và hiển thị nó ở fieldCountryCode
    // dropDownCountry.val() là giá trị được chọn của country có dạng: country.id + "-" + country.code -> code ứng với index=1;
    countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);
}

function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name:");
    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);
    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

// dùng jquery để tạo ajax call server: call restful webservice
function addCountry() {
	url = contextPath + "countries/save";
	
	// lấy ra Name và Code của Country từ form countries.html
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    
    // create 1 json data, nó sẽ được gửi kèm theo request
    jsonData = {name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
	    selectNewlyAddedCountry(countryId, countryCode, countryName);
	    showToastMessage("The new country has been added.");
	}).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error.");
    });
}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);
    
    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus();
}

function updateCountry() {
    url = contextPath + "countries/save";
	
	// lấy ra Id, Name và Code của Country từ form countries.html
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    // dropDownCountry.val() là giá trị được chọn của country có dạng: country.id + "-" + country.code -> id ứng với index=0;
    countryId = dropDownCountry.val().split("-")[0];
    
    // create 1 json data, nó sẽ được gửi kèm theo request
    jsonData = {id: countryId, name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
	    $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
	    $("#dropDownCountries option:selected").text(countryName);
	    showToastMessage("The country has been updated.");
	    
	    changeFormStateToNewCountry();
	}).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error.");
    });
}

function deleteCountry() {
    optionValue = dropDownCountry.val();
    
    // dropDownCountry.val() là giá trị được chọn của country có dạng: country.id + "-" + country.code -> id ứng với index=0;
    countryId = optionValue.split("-")[0];    
    url = contextPath + "countries/delete/" + countryId;
    
    $.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
	}).done(function() {
	    $("#dropDownCountries option[value='" + optionValue + "']").remove();
		changeFormStateToNewCountry();
		showToastMessage("The country has been deleted.");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
	});	
}












