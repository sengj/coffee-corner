package com.jseng.coffee.corner;

import static com.jseng.coffee.corner.repository.ProductRepository.BACON_ROLL;
import static com.jseng.coffee.corner.repository.ProductRepository.EXTRA_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FOAMED_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FRESH_ORANGE_JUICE;
import static com.jseng.coffee.corner.repository.ProductRepository.LARGE_COFFEE;
import static com.jseng.coffee.corner.repository.ProductRepository.SMALL_COFFEE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.service.OrderServiceImpl;
import com.jseng.coffee.corner.service.ReceiptServiceImpl;

class CoffeeCornerApplicationTest {

	private static final Logger LOGGER = Logger.getLogger(CoffeeCornerApplicationTest.class.getName());

	public static final String RECEIPT_FILE_PATH = "src/test/resources/expectedReceipt/receipt.txt";

	private CoffeeCornerApplication coffeeCorner;
	private PrintStream printStream = System.out;
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	@BeforeEach
	public void setUp() {
		coffeeCorner = new CoffeeCornerApplication();
		System.setOut(new PrintStream(byteArrayOutputStream));
	}

	@AfterEach
	public void tearDown() {
		System.setOut(printStream);
	}

	@Test
	void shouldThrowExceptionWhenNoProductsOrdered() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> coffeeCorner.order(Collections.emptyList()));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EMPTY_PRODUCT_ORDERED_EXCEPTION)));
	}

	@Test
	void shouldThrowExceptionWhenApplyDiscountWithNoOrder() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> coffeeCorner.applyDiscounts(0));

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(OrderServiceImpl.EMPTY_PRODUCT_ORDERED_EXCEPTION)));
	}

	@Test
	void shouldThrowExceptionWhenPrintReceiptWithNoOrder() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> coffeeCorner.printReceipt());

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(ReceiptServiceImpl.NO_PRODUCT_TO_PRINT_EXCEPTION)));
	}

	@Test
	void shouldPrintReceipt() throws IOException {
		final int numberOfCurrentStamp = 0;
		final Product extraMilk = coffeeCorner.getProductService().findProductByName(EXTRA_MILK);
		final Product foamedMilk = coffeeCorner.getProductService().findProductByName(FOAMED_MILK);
		final Product smallCoffeeWithExtraMilkAndFoamedMilk = coffeeCorner.getProductService().findProductByName(SMALL_COFFEE);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(extraMilk);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(foamedMilk);
		final Product smallCoffee = coffeeCorner.getProductService().findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = coffeeCorner.getProductService().findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = coffeeCorner.getProductService().findProductByName(LARGE_COFFEE);
		final Product orangeJuice1 = coffeeCorner.getProductService().findProductByName(FRESH_ORANGE_JUICE);
		final Product orangeJuice2 = coffeeCorner.getProductService().findProductByName(FRESH_ORANGE_JUICE);
		final Product baconRoll1 = coffeeCorner.getProductService().findProductByName(BACON_ROLL);
		final Product baconRoll2 = coffeeCorner.getProductService().findProductByName(BACON_ROLL);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffeeWithExtraMilkAndFoamedMilk);
		productsToOrder.add(smallCoffee);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);
		productsToOrder.add(baconRoll1);
		productsToOrder.add(baconRoll2);
		productsToOrder.add(orangeJuice1);
		productsToOrder.add(orangeJuice2);

		coffeeCorner.order(productsToOrder);
		coffeeCorner.applyDiscounts(numberOfCurrentStamp);
		coffeeCorner.printReceipt();

		final File file = new File(RECEIPT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		final String receipt = byteArrayOutputStream.toString();
		
		LOGGER.info(receipt);

		assertEquals(expectedReceipt, receipt);
	}
}
