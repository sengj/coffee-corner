package com.jseng.coffee.corner.service;

import java.math.BigDecimal;
import java.util.List;

import com.jseng.coffee.corner.domain.Product;

public interface OrderService {
	void orderProducts(List<Product> productsToOrder);

	List<Product> getProducts();

	List<Product> getFreeProducts();

	void applyBonusProgram(int nbOfCurrentStamp);

	BigDecimal getTotalAmount();
}
