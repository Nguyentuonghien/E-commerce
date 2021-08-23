
$(document).ready(function() {
    $("#products").on("click", "#linkAddProduct", function(e) {
        e.preventDefault();
        link = $(this);
        // khi click vào button "Add a Product" có url: /orders/search_product -> load contains for iframe in modal dialog
        url = link.attr("href");
        $("#addProductModal").on("shown.bs.modal", function() {
            $(this).find("iframe").attr("src", url);
        });
        $("#addProductModal").modal();
    })
});

function addProduct(productId, productName) {
    getShippingCost(productId);
}

// check product đã tồn tại chưa trước khi add vào order
function isProductAlreadyAdded(productId) {
    productExists = false;
    // read all productId on order_form
    $(".hiddenProductId").each(function(e) {
        aProductId = $(this).val();
        // nếu 2 id trùng nhau -> product đã tồn tại
        if (aProductId == productId) {
            productExists = true;
            return;
        }
    });
    return productExists;
}

function getShippingCost(productId) {
    // get countryId, state từ order_form_shipping
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();
    state = $("#state").val();
    if (state.length == 0) {
        state = $("#city").val();
    }
    
    requestURL = contextPath + "get_shipping_cost";
    params = {productId: productId, countryId: countryId, state: state};
    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
        data: params
    }).done(function(shippingCost) {
        getProductInfo(productId, shippingCost);
        // alert("Shipping Cost = " + shippingCost);
    }).fail(function(error) {
        showWarningModal(error.responseJSON.message);
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    }).always(function(){
        $("#addProductModal").modal("hide");
    });
}

function getProductInfo(productId, shippingCost) {
    requestURL = contextPath + "products/get/" + productId;    
    params = {productId: productId};
    $.ajax({
        type: 'GET',
        url: requestURL,
        data: params
    }).done(function(productJSON) {
        console.log(productJSON);
        
        productName = productJSON.name;
        mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJSON.imagePath;
        productCost = $.number(productJSON.cost, 2);
        productPrice = $.number(productJSON.price, 2);
        
        htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost);
        $("#productList").append(htmlCode);
        
        // sau khi add 1 product -> ta se update lại các amounts ở form overview
        updateOrderAmounts();
    }).fail(function(error) {
        showWarningModal(error.responseJSON.message);
    });
}

function generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost) {
    nextCount = $(".hiddenProductId").length + 1;
    quantityId = "quantity" + nextCount;
    priceId = "price" + nextCount;
    subtotalId = "subtotal" + nextCount;
    rowId = "row" + nextCount;
    blankLineId = "blankLine" + nextCount;
    
    htmlCode = `
        <div class="border rounded p-1" id="${rowId}">
		    <input type="hidden" name="productId" value="${productId}" class="hiddenProductId" />
            <div class="row">
                <div class="col-1">
                    <div class="divCount">${nextCount}</div>
                    <div><a class="fas fa-trash icon-dark linkRemove" href="" rowNumber="${nextCount}"></a></div>	
                </div>
                <div class="col-3">
                    <img src="${mainImagePath}" class="img-fluid" />
                </div>
            </div>
            <div class="row m-2">
                <b>${productName}</b>
            </div>
            <div class="row m-2">
                <table>
                    <tr>
                        <td>Product Cost:</td>
                        <td>
                            <input type="text" value="${productCost}" required 
                                   rowNumber="${nextCount}" 
                                   class="form-control m-1 cost-input" style="max-width: 140px" />
                        </td>
                    </tr>
                    <tr>
                        <td>Quantity:</td>
                        <td>
                            <input type="number" step="1" min="1" max="5"  
                                   value="1" rowNumber="${nextCount}" 
                                   id="${quantityId}" 
                                   required class="form-control m-1 quantity-input" style="max-width: 140px" />
                        </td>
                    </tr>
                    <tr>
                        <td>Unit Price:</td>
                        <td>
                            <input type="text" value="${productPrice}"  
                                   rowNumber="${nextCount}" 
                                   id="${priceId}" required
                                   class="form-control m-1 price-input" style="max-width: 140px" />
                        </td>
                    </tr>
                    <tr>
                        <td>Subtotal:</td>
                        <td>
                            <input type="text" value="${productPrice}" readonly="readonly"
                                   id="${subtotalId}"
                                   class="form-control m-1 subtotal-output" style="max-width: 140px" />
                        </td>
                    </tr>
                    <tr>
                        <td>Shipping Cost:</td>
                        <td>
                            <input type="text" value="${shippingCost}" required 
                                   class="form-control m-1 ship-input" style="max-width: 140px" />
                        </td>
                    </tr>
                </table>
            </div>
	    </div>
	    <div id="${blankLineId}" class="row">&nbsp;</div>
    `;    
    return htmlCode;
}
















