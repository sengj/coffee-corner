package com.jseng.coffee.corner.service;

import java.util.Set;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.repository.ProductRepository;
import com.jseng.coffee.corner.repository.ProductRepositoryImpl;

public class ProductServiceImpl implements ProductService {

	public static final String PRODUCT_NOT_FOUND_EXCEPTION = "Product not found.";

	private final ProductRepository productRepository;

	public ProductServiceImpl() {
		super();
		this.productRepository = new ProductRepositoryImpl();
	}

	@Override
	public Set<Product> getDrinks() {
		return productRepository.getDrinks();
	}

	@Override
	public Set<Product> getExtras() {
		return productRepository.getExtras();
	}

	@Override
	public Set<Product> getSnacks() {
		return productRepository.getSnacks();
	}

	@Override
	public Set<Product> getDrinksWithExtra() {
		return productRepository.getDrinksWithExtra();
	}

	@Override
	public void loadProducts() {
		productRepository.loadProducts();
	}

	@Override
	public Product findProductByName(String name) {
		Product product = productRepository.findProductByName(name)
				.orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND_EXCEPTION));

		return new Product(product);
	}
}
