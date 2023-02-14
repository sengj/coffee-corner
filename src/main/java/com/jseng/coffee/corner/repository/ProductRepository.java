package com.jseng.coffee.corner.repository;

import java.util.Optional;
import java.util.Set;

import com.jseng.coffee.corner.domain.Product;

public interface ProductRepository {

	String SMALL_COFFEE = "small coffee";
	String MEDIUM_COFFEE = "medium coffee";
	String LARGE_COFFEE = "large coffee";
	String FRESH_ORANGE_JUICE = "freshly squeezed orange juice";

	String BACON_ROLL = "bacon roll";

	String EXTRA_MILK = "extra milk";
	String FOAMED_MILK = "foamed milk";
	String SPECIAL_ROAST_COFFEE = "special roast coffee";

	void loadProducts();

	Set<Product> getDrinks();

	Set<Product> getDrinksWithExtra();

	Set<Product> getExtras();

	Set<Product> getSnacks();

	Optional<Product> findProductByName(String name);
}
