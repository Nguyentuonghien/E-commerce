
// xử lý sự kiện khi click vào nút 'Add to Cart' 
$(document).ready(function() {
	$("#buttonAdd2Cart").on("click", function(evt) {
		addToCart();
	});
});

function addToCart() {
    quantity = $("#quantity" + productId).val();
    
    // vd url có dạng: /Shopme/cart/add/9/1 or /Shopme/cart/add/9/4
    url = contextPath + "cart/add/" + productId + "/" + quantity;
    
    $.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showModalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart.");
	});
    
}