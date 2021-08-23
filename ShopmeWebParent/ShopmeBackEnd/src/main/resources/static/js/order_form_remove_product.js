
$(document).ready(function() {
    $("#productList").on("click", ".linkRemove", function(e) {
        e.preventDefault();
        link = $(this);
        if (doesOrderHaveOnlyOneProduct()) {
            showWarningModal("Could not remove product. The order must have at least one product.");
        } else {
            removeProduct(link);  
            // sau khi remove product -> ta sẽ update lại các amounts ở form overview 
            updateOrderAmounts(); 
        }    
    })
});

function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
    
    // khi remove 1 product -> count_number sẽ thay đổi 
    $(".divCount").each(function(index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length;
    return productCount == 1;
}





