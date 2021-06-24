
$(document).ready(function() {
    // xử lý sự kiện khi click vào nut '-'
    $(".linkMinus").on("click", function(evt) {
        evt.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        // khi ấn nút '-' sẽ giảm quantity đi 1
        newQuantity = parseInt(quantityInput.val()) - 1;      
        if (newQuantity > 0) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Minimum quantity is 1');
        }
    });
    
    // xử lý sự kiện khi click vào nut '+'
    $(".linkPlus").on("click", function(evt) {
        evt.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        // khi ấn nút '+' sẽ tang quantity đi 1
        newQuantity = parseInt(quantityInput.val()) + 1;      
        if (newQuantity <= 5) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Maximum quantity is 5');
        }
    });
});