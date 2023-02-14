package com.jseng.coffee.corner.repository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.domain.ProductType;

public class ProductRepositoryImpl implements ProductRepository {

	private Set<Product> productList;

	public ProductRepositoryImpl() {
		super();
		productList = new HashSet<>();
	}

	public void loadProducts() {
		loadExtras();
		loadDrinks();
		loadSnacks();
	}

	private void loadDrinks() {
		productList.add(new Product(SMALL_COFFEE, BigDecimal.valueOf(2.50), ProductType.DRINK, getExtras()));
		productList.add(new Product(MEDIUM_COFFEE, BigDecimal.valueOf(3.00), ProductType.DRINK, getExtras()));
		productList.add(new Product(LARGE_COFFEE, BigDecimal.valueOf(3.50), ProductType.DRINK, getExtras()));
		productList.add(new Product(FRESH_ORANGE_JUICE, BigDecimal.valueOf(3.95), ProductType.DRINK, null));
	}

	private void loadSnacks() {
		productList.add(new Product(BACON_ROLL, BigDecimal.valueOf(4.50), ProductType.SNACK, null));
	}

	private void loadExtras() {
		productList.add(new Product(EXTRA_MILK, BigDecimal.valueOf(0.30), ProductType.EXTRA, null));
		productList.add(new Product(FOAMED_MILK, BigDecimal.valueOf(0.50), ProductType.EXTRA, null));
		productList.add(new Product(SPECIAL_ROAST_COFFEE, BigDecimal.valueOf(0.90), ProductType.EXTRA, null));
	}

	@Override
	public Set<Product> getDrinks() {
		return productList.stream().filter(product -> ProductType.DRINK == product.getType())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Product> getSnacks() {
		return productList.stream().filter(product -> ProductType.SNACK == product.getType())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Product> getExtras() {
		return productList.stream().filter(product -> ProductType.EXTRA == product.getType())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Product> getDrinksWithExtra() {
		return productList.stream().filter(product -> ProductType.DRINK == product.getType()
				&& product.getExtraList() != null && !product.getExtraList().isEmpty()).collect(Collectors.toSet());
	}

	@Override
	public Optional<Product> findProductByName(String name) {
		return productList.stream().filter(product -> product.getName().equalsIgnoreCase(name)).findFirst();
	}
}
