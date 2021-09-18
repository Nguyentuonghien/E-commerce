
var returnModal;
var modalTitle;
var orderId;
var divReason;
var divMessage;
var firstButton;
var secondButton;

$(document).ready(function() {
    returnModal = $("#returnOrderModal");
    modalTitle = $("#returnOrderModalTitle");
    fieldNote = $("#returnNote");
    divReason = $("#divReason");
    divMessage = $("#divMessage");
    firstButton = $("#firstButton");
    secondButton = $("#secondButton");
    
    handleReturnOrderLink($(this));
});

function showReturnModalDialog(link) {
    divMessage.hide();
    divReason.show();
    firstButton.show();
    secondButton.text("Cancel");
    fieldNote.val("");
    
    // khi click vào "Return" -> show ra modal return order
    orderId = link.attr("orderId");
    returnModal.modal("show"); 
    modalTitle.text("Return Order ID #" + orderId);
}

function showMessageDialog(message) {
    divReason.hide();
    firstButton.hide();
    secondButton.text("Close");
    divMessage.text(message);
    divMessage.show();
}

function handleReturnOrderLink(link) {
    $(".linkReturnOrder").on("click", function(e) {
        e.preventDefault();
        showReturnModalDialog($(this));
    });
}

function submitReturnOrderForm() {
    // reason = giá trị khi ta chọn radio button
    reason = $("input[name='returnReason']:checked").val();
    note = fieldNote.val();
    
    sendReturnOrderRequest(reason, note);
    
    return false;
}

function sendReturnOrderRequest(reason, note) {
    requestURL = contextPath + "orders/return";
    requestBody = {orderId: orderId, reason: reason, note: note};
    
    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(requestBody),
		contentType: 'application/json'
    }).done(function(returnResponse) {
        console.log(returnResponse);
        showMessageDialog("Return request has been sent.");
        updateStatusTextAndHideReturnButton(orderId);
    }).fail(function(error) {
        console.log(error);
        showMessageDialog(error.responseText);
    });
    
}

function updateStatusTextAndHideReturnButton(orderId) {
    // khi thành công -> set status thành RETURN_REQUESTED và ẩn icon RETURN_REQUESTED đi
    $(".textOrderStatus" + orderId).each(function(index) {
        $(this).text("RETURN_REQUESTED");
    });
    
    $(".linkReturn" + orderId).each(function(index) {
        $(this).hide();
    });
}















