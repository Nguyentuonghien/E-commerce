
// hàng nghìn thì chia cách nhau bởi dấu ',',  hàng thập phân là dấu '.'
decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.'; 

$(document).ready(function() {
    // xử lý sự kiện khi click vào nut '-'
    $(".linkMinus").on("click", function(evt) {
        evt.preventDefault();
        decreaseQuantity($(this));
    });
    
    // xử lý sự kiện khi click vào nut '+'
    $(".linkPlus").on("click", function(evt) {
        evt.preventDefault();
        increaseQuantity($(this));
    });
    
    // xử lý sự kiện khi click vào nut 'xóa sp'
    $(".linkRemove").on("click", function(evt) {
        evt.preventDefault();
        removeProduct($(this));
    });
});

function decreaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    // khi ấn nút '-' sẽ giảm quantity đi 1
    newQuantity = parseInt(quantityInput.val()) - 1;      
    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Minimum quantity is 1');
    }
}

function increaseQuantity(link) {
		productId = link.attr("pid");
		quantityInput = $("#quantity" + productId);
		
		// khi ấn nút '+' sẽ tăng quantity lên 1
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		if (newQuantity <= 5) {
			quantityInput.val(newQuantity);
			updateQuantity(productId, newQuantity);
		} else {
			showWarningModal('Maximum quantity is 5');
		}	
}

function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	
	// make ajax call to the restful web service running on the server
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		updateTotal();
	}).fail(function() {
		showErrorModal("Error while updating product quantity.");
	});	
}

function updateSubtotal(updatedSubtotal, productId) {
    $("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function updateTotal() {
    total = 0.0;
    productCount = 0;
    
    $(".subtotal").each(function(index, element) {
        productCount++;
        total += parseFloat(clearCurrencyFormat(element.innerHTML));
    });
    
    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        $("#total").text(formatCurrency(total));
    }
}
		
function removeProduct(link) {
    url = link.attr("href");
    $.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
	
	    rowNumber = link.attr("rowNumber");
		removeProductHTML(rowNumber);
		updateTotal();
	    updateCountNumbers();
	    
	    showModalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while removing product.");
	});	
}

function removeProductHTML(rowNumber) {
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	}); 
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

// format currency reference: github.com/customd/jquery-number 
function formatCurrency(amount) {
    return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function clearCurrencyFormat(numberString) {
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
}











