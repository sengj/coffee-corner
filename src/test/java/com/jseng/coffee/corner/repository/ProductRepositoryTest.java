package com.jseng.coffee.corner.repository;

import static com.jseng.coffee.corner.repository.ProductRepository.BACON_ROLL;
import static com.jseng.coffee.corner.repository.ProductRepository.SMALL_COFFEE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jseng.coffee.corner.domain.Product;

class ProductRepositoryTest {
	private static final String SHOULD_NOT_BE_NULL = " should not be null";
	private static final String SHOULD_NOT_BE_EMPTY = " should not be empty";
	private static final String SHOULD_BE_EMPTY = " should be empty";
	private static final String SHOULD_CONTAIN = " should contain ";
	private static final String PRODUCTS = "Products";
	private static final String DRINKS = "Drinks";
	private static final String SNACKS = "Snacks";
	private static final String EXTRAS = "Extras";

	private static final int NB_OF_EXTRAS = 3;
	private static final int NB_OF_DRINKS = 4;
	private static final int NB_OF_ONLY_DRINKS_WITH_EXTRA = 3;
	private static final int NB_OF_SNACKS = 1;

	private ProductRepository productRepository;

	@BeforeEach
	public void setUp() {
		productRepository = new ProductRepositoryImpl();
	}

	@Test
	void shouldGetEmptyExtras() {
		Set<Product> extraList = productRepository.getExtras();

		assertAll(() -> assertNotNull(extraList, EXTRAS + SHOULD_NOT_BE_NULL),
				() -> assertTrue(extraList.isEmpty(), EXTRAS + SHOULD_BE_EMPTY));
	}

	@Test
	void shouldGetExtrasWhenLoadProducts() {
		productRepository.loadProducts();
		Set<Product> extraList = productRepository.getExtras();

		assertAll(() -> assertNotNull(extraList, EXTRAS + SHOULD_NOT_BE_NULL),
				() -> assertFalse(extraList.isEmpty(), EXTRAS + SHOULD_NOT_BE_EMPTY),
				() -> assertEquals(NB_OF_EXTRAS, extraList.size(), EXTRAS + SHOULD_CONTAIN + NB_OF_EXTRAS + PRODUCTS));
	}

	@Test
	void shouldGetEmptyDrinks() {
		Set<Product> drinkList = productRepository.getDrinks();

		assertAll(() -> assertNotNull(drinkList, EXTRAS + SHOULD_NOT_BE_NULL),
				() -> assertTrue(drinkList.isEmpty(), EXTRAS + SHOULD_BE_EMPTY));
	}

	@Test
	void shouldGetDrinksWhenLoadProducts() {
		productRepository.loadProducts();
		Set<Product> drinkList = productRepository.getDrinks();

		assertAll(() -> assertNotNull(drinkList, DRINKS + SHOULD_NOT_BE_NULL),
				() -> assertFalse(drinkList.isEmpty(), DRINKS + SHOULD_NOT_BE_EMPTY),
				() -> assertEquals(NB_OF_DRINKS, drinkList.size(), EXTRAS + SHOULD_CONTAIN + NB_OF_DRINKS + PRODUCTS));
	}

	@Test
	void shouldGetEmptyOnlyDrinksWithExtra() {
		Set<Product> drinkWithExtraList = productRepository.getDrinksWithExtra();

		assertAll(() -> assertNotNull(drinkWithExtraList, EXTRAS + SHOULD_NOT_BE_NULL),
				() -> assertTrue(drinkWithExtraList.isEmpty(), EXTRAS + SHOULD_BE_EMPTY));
	}

	@Test
	void shouldGetOnlyDrinksWithExtraWhenLoadProducts() {
		productRepository.loadProducts();
		Set<Product> drinkWithExtraList = productRepository.getDrinksWithExtra();

		assertAll(() -> assertNotNull(drinkWithExtraList, DRINKS + SHOULD_NOT_BE_NULL),
				() -> assertFalse(drinkWithExtraList.isEmpty(), DRINKS + SHOULD_NOT_BE_EMPTY),
				() -> assertEquals(NB_OF_ONLY_DRINKS_WITH_EXTRA, drinkWithExtraList.size(),
						EXTRAS + SHOULD_CONTAIN + NB_OF_ONLY_DRINKS_WITH_EXTRA + PRODUCTS));
	}

	@Test
	void shouldGetEmptySnacks() {
		Set<Product> snackList = productRepository.getSnacks();

		assertAll(() -> assertNotNull(snackList, EXTRAS + SHOULD_NOT_BE_NULL),
				() -> assertTrue(snackList.isEmpty(), EXTRAS + SHOULD_BE_EMPTY));
	}

	@Test
	void shouldGetSnacksWhenLoadProducts() {
		productRepository.loadProducts();
		Set<Product> snackList = productRepository.getSnacks();

		assertAll(() -> assertNotNull(snackList, SNACKS + SHOULD_NOT_BE_NULL),
				() -> assertFalse(snackList.isEmpty(), SNACKS + SHOULD_NOT_BE_EMPTY),
				() -> assertEquals(NB_OF_SNACKS, snackList.size(), DRINKS + SHOULD_CONTAIN + NB_OF_SNACKS + PRODUCTS));
	}

	@Test
	void shouldFindNothing() {
		Optional<Product> optBaconRoll = productRepository.findProductByName(BACON_ROLL);

		assertAll(() -> assertTrue(optBaconRoll.isEmpty()));
	}

	@Test
	void shouldFindBaconRoll() {
		productRepository.loadProducts();
		Optional<Product> optBaconRoll = productRepository.findProductByName(BACON_ROLL);

		assertAll(() -> assertFalse(optBaconRoll.isEmpty()),
				() -> assertEquals(BACON_ROLL, optBaconRoll.get().getName()));
	}

	@Test
	void shouldFindSmallCoffee() {
		productRepository.loadProducts();
		Optional<Product> optSmallCoffee = productRepository.findProductByName(SMALL_COFFEE);

		assertAll(() -> assertFalse(optSmallCoffee.isEmpty()),
				() -> assertEquals(SMALL_COFFEE, optSmallCoffee.get().getName()));
	}
}
