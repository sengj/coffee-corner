package com.jseng.coffee.corner.service;

import static com.jseng.coffee.corner.repository.ProductRepository.BACON_ROLL;
import static com.jseng.coffee.corner.repository.ProductRepository.EXTRA_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FOAMED_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FRESH_ORANGE_JUICE;
import static com.jseng.coffee.corner.repository.ProductRepository.LARGE_COFFEE;
import static com.jseng.coffee.corner.repository.ProductRepository.SMALL_COFFEE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.domain.ProductType;

class OrderServiceTest {
	private ProductService productService;
	private OrderService orderService;

	@BeforeEach
	public void setUp() {
		orderService = new OrderServiceImpl();
		productService = new ProductServiceImpl();
		productService.loadProducts();
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenOrderProductsWithNull() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(null));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EMPTY_PRODUCT_ORDERED_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenOrderProductsWithEmptyList() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(Collections.emptyList()));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EMPTY_PRODUCT_ORDERED_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenExtraOnly() {
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);

		List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(extraMilk);

		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(productsToOrder));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EXTRA_CAN_T_BE_ORDERED_ALONE_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenBaconRollWithExtra() {
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);
		final Product baconRoll = productService.findProductByName(BACON_ROLL);
		baconRoll.addExtra(extraMilk);

		List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(baconRoll);

		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(productsToOrder));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.ONLY_DRINKS_CAN_HAVE_EXTRA_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenDrinkHasSnackAsExtra() {
		final Product orangeJuice = productService.findProductByName(FRESH_ORANGE_JUICE);
		final Product baconRoll = productService.findProductByName(BACON_ROLL);
		
		List<Product> productsToOrder = new ArrayList<>();
		orangeJuice.addExtra(baconRoll);

		productsToOrder.add(orangeJuice);

		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(productsToOrder));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.ONLY_EXTRA_CAN_BE_EXTRA_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenDrinkWithExtraAndExtraGotExtra() {
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);
		final Product foamedMilk = productService.findProductByName(FOAMED_MILK);
		extraMilk.addExtra(foamedMilk);

		final Product smallCoffee1 = productService.findProductByName(SMALL_COFFEE);
		smallCoffee1.addExtra(extraMilk);

		List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffee1);

		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.orderProducts(productsToOrder));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EXTRA_CAN_T_HAVE_EXTRA_EXCEPTION)));
	}

	@Test
	void shouldGetProductsAsNull() {
		final List<Product> products = orderService.getProducts();

		Assertions.assertNull(products);
	}

	@Test
	void shouldGetProductsNotEmptyAfterOrderingProducts() {
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 1);
		orderSnacks(productsToOrder, 1);
		orderDrinksWithOneExtra(productsToOrder, 1);
		orderDrinksWithAllExtra(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);

		final List<Product> products = orderService.getProducts();

		assertAll(() -> assertNotNull(products), () -> assertFalse(products.isEmpty()),
				() -> assertTrue(products.containsAll(productsToOrder)));
	}

	@Test
	void shouldGetFreeProductsAsNull() {
		final List<Product> freeProducts = orderService.getFreeProducts();

		assertAll(() -> assertNotNull(freeProducts), () -> assertTrue(freeProducts.isEmpty()));
	}

	@Test
	void shouldNotGetFifthDrinkFreeFromStampDiscount() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeDrinks = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.DRINK == product.getType()).toList();

		assertAll(() -> assertNotNull(freeDrinks), () -> assertTrue(freeDrinks.isEmpty()));
	}

	@Test
	void shouldGetFifthDrinkFreeFromStampDiscount() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 5);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeDrinks = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.DRINK == product.getType()).toList();

		assertAll(() -> assertNotNull(freeDrinks), () -> assertFalse(freeDrinks.isEmpty()),
				() -> assertEquals(productsToOrder.get(4), freeDrinks.get(0)));
	}

	@Test
	void shouldGetThirdDrinkFreeFromStampDiscountWhenAlreadyHas2Stamps() {
		final int numberOfCurrentStamp = 2;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 2);
		orderDrinksWithOneExtra(productsToOrder, 2);
		orderDrinksWithAllExtra(productsToOrder, 2);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeDrinks = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.DRINK == product.getType()).toList();

		assertAll(() -> assertNotNull(freeDrinks), () -> assertFalse(freeDrinks.isEmpty()),
				() -> assertEquals(productsToOrder.get(2), freeDrinks.get(0)));
	}

	@Test
	void shouldGet3FreeDrinksFromStampDiscountWhenOrdered18Drinks() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 6);
		orderDrinksWithOneExtra(productsToOrder, 6);
		orderDrinksWithAllExtra(productsToOrder, 6);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeDrinks = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.DRINK == product.getType()).toList();

		final int expectedFreeDrinks = 3;
		assertAll(() -> assertNotNull(freeDrinks), () -> assertFalse(freeDrinks.isEmpty()),
				() -> assertEquals(expectedFreeDrinks, freeDrinks.size()));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenNegativeNbOfCurrentStamp() {
		final int numberOfCurrentStamp = -1;
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.applyBonusProgram(numberOfCurrentStamp));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.NB_OF_CURRENT_STAMP_EXCEPTION)));
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenNoOrderForBonusProgram() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> orderService.applyBonusProgram(0));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EMPTY_PRODUCT_ORDERED_EXCEPTION)));
	}

	@Test
	void shouldGet0FreeExtraWhenOrderNoSnack() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 0;
		assertAll(() -> assertNotNull(freeExtras), () -> assertTrue(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGet0FreeExtraWhenNoDrink() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderSnacks(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 0;
		assertAll(() -> assertNotNull(freeExtras), () -> assertTrue(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGet0FreeExtraWhenOrder1DrinkWithoutExtraAnd1Snack() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 1);
		orderSnacks(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 0;
		assertAll(() -> assertNotNull(freeExtras), () -> assertTrue(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGet1FreeExtraWhenOrder1DrinkWithExtraAnd1Snack() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinksWithOneExtra(productsToOrder, 1);
		orderSnacks(productsToOrder, 1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 1;
		assertAll(() -> assertNotNull(freeExtras), () -> assertFalse(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGet4FreeExtraWhenOrder4DrinkAnd6Snack() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 2);
		orderDrinksWithOneExtra(productsToOrder, 1);
		orderDrinksWithAllExtra(productsToOrder, 1);
		orderSnacks(productsToOrder, 6);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 4;
		assertAll(() -> assertNotNull(freeExtras), () -> assertFalse(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGet4FreeExtraWhenOrder6DrinkAnd4Snack() {
		final int numberOfCurrentStamp = 0;
		final List<Product> productsToOrder = new ArrayList<>();
		orderDrinks(productsToOrder, 2);
		orderDrinksWithOneExtra(productsToOrder, 2);
		orderDrinksWithAllExtra(productsToOrder, 2);
		orderSnacks(productsToOrder, 4);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final List<Product> freeExtras = orderService.getFreeProducts().stream()
				.filter(product -> ProductType.EXTRA == product.getType()).toList();

		final int expectedFreeExtras = 4;
		assertAll(() -> assertNotNull(freeExtras), () -> assertFalse(freeExtras.isEmpty()),
				() -> assertEquals(expectedFreeExtras, freeExtras.size()));
	}

	@Test
	void shouldGetTotalAmountWithoutDiscount() {
		final int numberOfCurrentStamp = 0;
		final Product smallCoffee1 = productService.findProductByName(SMALL_COFFEE);
		final Product smallCoffee2 = productService.findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = productService.findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = productService.findProductByName(LARGE_COFFEE);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffee1);
		productsToOrder.add(smallCoffee2);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final BigDecimal totalAmount = orderService.getTotalAmount();
		final BigDecimal expectedTotalAmount = BigDecimal.valueOf(12.00);
		assertAll(() -> assertEquals(expectedTotalAmount, totalAmount));
	}

	@Test
	void shouldGetTotalAmountWithDiscount() {
		final int numberOfCurrentStamp = 0;
		final Product smallCoffee1 = productService.findProductByName(SMALL_COFFEE);
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);
		final Product foamedMilk = productService.findProductByName(FOAMED_MILK);
		smallCoffee1.addExtra(extraMilk);
		smallCoffee1.addExtra(foamedMilk);

		final Product smallCoffee2 = productService.findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = productService.findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = productService.findProductByName(LARGE_COFFEE);
		largeCoffee2.addExtra(foamedMilk);

		final Product orangeJuice1 = productService.findProductByName(FRESH_ORANGE_JUICE);
		final Product orangeJuice2 = productService.findProductByName(FRESH_ORANGE_JUICE);

		final Product baconRoll1 = productService.findProductByName(BACON_ROLL);
		final Product baconRoll2 = productService.findProductByName(BACON_ROLL);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffee1);
		productsToOrder.add(smallCoffee2);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);
		productsToOrder.add(orangeJuice1);
		productsToOrder.add(orangeJuice2);
		productsToOrder.add(baconRoll1);
		productsToOrder.add(baconRoll2);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final BigDecimal totalAmount = orderService.getTotalAmount();
		final BigDecimal expectedTotalAmount = BigDecimal.valueOf(25.45);
		assertAll(() -> assertEquals(expectedTotalAmount, totalAmount));
	}

	private void orderDrinksWithAllExtra(List<Product> productsToOrder, int numberOfDrinks) {
		final List<Product> drinksToOrder = new ArrayList<>();
		orderDrinkWithExtra(drinksToOrder, numberOfDrinks);

		drinksToOrder.stream().forEach(drink -> {
			drink.addExtras(productService.getExtras());
		});
		productsToOrder.addAll(drinksToOrder);
	}

	private void orderDrinksWithOneExtra(List<Product> productsToOrder, int numberOfDrinks) {
		final List<Product> drinksToOrder = new ArrayList<>();
		orderDrinkWithExtra(drinksToOrder, numberOfDrinks);

		drinksToOrder.stream().forEach(drink -> {
			Product extra = productService.getExtras().stream().findAny().get();
			drink.addExtra(extra);
		});
		productsToOrder.addAll(drinksToOrder);
	}

	private void orderDrinkWithExtra(List<Product> drinksToOrder, int numberOfDrinks) {
		IntStream.range(0, numberOfDrinks).forEach(i -> {
			Product drink = new Product(productService.getDrinksWithExtra().stream().findAny().get());
			drinksToOrder.add(drink);
		});
	}

	private void orderSnacks(List<Product> productsToOrder, int numberOfDrinks) {
		IntStream.range(0, numberOfDrinks).forEach(i -> {
			productsToOrder.add(productService.getSnacks().stream().findAny().get());
		});
	}

	private void orderDrinks(List<Product> productsToOrder, int numberOfDrinks) {
		IntStream.range(0, numberOfDrinks).forEach(i -> {
			productsToOrder.add(new Product(productService.getDrinks().stream().findAny().get()));
		});
	}
}
