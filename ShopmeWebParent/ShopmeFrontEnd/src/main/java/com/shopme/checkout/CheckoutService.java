package com.shopme.checkout;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
public class CheckoutService {
	
	private static final int DIM_DIVISOR = 139;
	
	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		float productCost = calculateProductCost(cartItems);
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCostTotal;
		
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		
		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setPaymentTotal(paymentTotal);
		return checkoutInfo;
	}

	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			// tính DIM weight, với length, width và height là của package chứa product chứ k phải của product
			float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
			float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
			// shipping cost của mỗi 1 product = trọng lượng cuối cùng * số lượng của product đó * giá cước vận chuyển ở từng state
			float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();
			cartItem.setShippingCost(shippingCost);
			shippingCostTotal += shippingCost;
		}
		return shippingCostTotal;
	}
	
	private float calculateProductTotal(List<CartItem> cartItems) {
		float productTotal = 0.0f;
		for (CartItem cartItem : cartItems) {
			productTotal += cartItem.getSubtotal();
		}
		return productTotal;
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		float productCost = 0.0f;
		for (CartItem cartItem : cartItems) {
			productCost += cartItem.getQuantity() * cartItem.getProduct().getCost();
		}
		return productCost;
	}
	
}


