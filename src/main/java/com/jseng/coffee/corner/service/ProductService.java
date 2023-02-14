package com.jseng.coffee.corner.service;

import java.util.Set;

import com.jseng.coffee.corner.domain.Product;

public interface ProductService {
	void loadProducts();

	Set<Product> getDrinks();

	Set<Product> getDrinksWithExtra();

	Set<Product> getExtras();

	Set<Product> getSnacks();

	Product findProductByName(String name);
}
