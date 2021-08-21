
var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function() {
    fieldProductCost = $("#productCost");
	fieldSubtotal = $("#subtotal");
	fieldShippingCost = $("#shippingCost");
	fieldTax = $("#tax");
	fieldTotal = $("#total");
	
	formatOrderAmounts()
	formatProductAmounts();
	
	$("#productList").on("change", ".quantity-input", function(e) {
		updateSubtotalWhenQuantityChanged($(this));
		updateOrderAmounts();
	});
	
	$("#productList").on("change", ".price-input", function(e) {
		updateSubtotalWhenPriceChanged($(this));
		updateOrderAmounts();
	});
	
	$("#productList").on("change", ".cost-input", function(e) {
	    updateOrderAmounts();
	});
	
	$("#productList").on("change", ".ship-input", function(e) {
	    updateOrderAmounts();
	});
	
});

function updateOrderAmounts() {
    totalCost = 0.0;
    $(".cost-input").each(function(e) {
        costInputField = $(this);
        rowNumber = costInputField.attr("rowNumber");
        quantityValue = $("#quantity" + rowNumber).val();
        
        productCost = getNumberValueRemovedThousandSeparator(costInputField);
        totalCost += productCost * parseInt(quantityValue);
    });
    // set totalCost cho field productCost ở form overview 
    setAndFormatNumberForField("productCost", totalCost);
    
    orderSubtotal = 0.0;
    $(".subtotal-output").each(function(e) {
		productSubtotal = getNumberValueRemovedThousandSeparator($(this));
		orderSubtotal += productSubtotal;
	});
	// set orderSubtotal cho field subtotal ở form overview 
	setAndFormatNumberForField("subtotal", orderSubtotal);
	
	shippingCost = 0.0;
	$(".ship-input").each(function(e) {
		productShip = getNumberValueRemovedThousandSeparator($(this));
		shippingCost += productShip;
	});
	// set shippingCost cho field shippingCost ở form overview 
	setAndFormatNumberForField("shippingCost", shippingCost);
	
	tax = getNumberValueRemovedThousandSeparator(fieldTax);
	orderTotal = orderSubtotal + tax + shippingCost;
	// set orderTotal cho field total ở form overview 
	setAndFormatNumberForField("total", orderTotal);
	
}

function setAndFormatNumberForField(fieldId, fieldValue) {
	formattedValue = $.number(fieldValue, 2);
	$("#" + fieldId).val(formattedValue);
}

function getNumberValueRemovedThousandSeparator(fieldRef) {
	fieldValue = fieldRef.val().replace(",", "");
	return parseFloat(fieldValue);
} 

// update subtotal khi unit_price thay đổi
function updateSubtotalWhenPriceChanged(input) {
    priceValue = getNumberValueRemovedThousandSeparator(input);
    // lấy rowNumber từ quantity
    rowNumber = input.attr("rowNumber");
    quantityField = $("#quantity" + rowNumber);
    // quantity là số nguyên(int) -> k cần convert sang float
    quantityValue = quantityField.val();
    newSubtotal = parseFloat(quantityValue) * priceValue;
    
    // set newSubtotal cho subtotal ở form products và format nó
    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

// update subtotal khi quantity thay đổi
function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val();
    // lấy rowNumber từ quantity
    rowNumber = input.attr("rowNumber");
    priceField = $("#price" + rowNumber);
    priceValue = getNumberValueRemovedThousandSeparator(priceField);
    newSubtotal = parseFloat(quantityValue) * priceValue;
    
    // set newSubtotal cho subtotal ở form products và format nó
    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

// format các fields ở form products
function formatProductAmounts() {
	$(".cost-input").each(function(e) {
		formatNumberForField($(this));
	});

	$(".price-input").each(function(e) {
		formatNumberForField($(this));
	});	
	
	$(".subtotal-output").each(function(e) {
		formatNumberForField($(this));
	});	
	
	$(".ship-input").each(function(e) {
		formatNumberForField($(this));
	});	
}

// format các fields ở form overview
function formatOrderAmounts() {
	formatNumberForField(fieldProductCost);
	formatNumberForField(fieldSubtotal);
	formatNumberForField(fieldShippingCost);
	formatNumberForField(fieldTax);
	formatNumberForField(fieldTotal);	
}

function formatNumberForField(fieldRef) {
	fieldRef.val($.number(fieldRef.val(), 2));
}


