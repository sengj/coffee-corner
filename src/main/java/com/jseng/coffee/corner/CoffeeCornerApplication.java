package com.jseng.coffee.corner;

import java.util.List;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.service.OrderService;
import com.jseng.coffee.corner.service.OrderServiceImpl;
import com.jseng.coffee.corner.service.ProductService;
import com.jseng.coffee.corner.service.ProductServiceImpl;
import com.jseng.coffee.corner.service.ReceiptService;
import com.jseng.coffee.corner.service.ReceiptServiceImpl;

public class CoffeeCornerApplication {
	private final OrderService orderService;
	private final ProductService productService;
	private final ReceiptService receiptService;

	public CoffeeCornerApplication() {
		this.orderService = new OrderServiceImpl();
		this.productService = new ProductServiceImpl();
		this.receiptService = new ReceiptServiceImpl(orderService);
		productService.loadProducts();
	}

	public void order(List<Product> products){
		orderService.orderProducts(products);
	}
	
	public void applyDiscounts(int nbOfCurrentStamp) {
		this.orderService.applyBonusProgram(nbOfCurrentStamp);
	}

	public void printReceipt() {
		final String receipt = this.receiptService.printReceipt();
		System.out.println(receipt);
	}

	public ProductService getProductService() {
		return productService;
	}
}
