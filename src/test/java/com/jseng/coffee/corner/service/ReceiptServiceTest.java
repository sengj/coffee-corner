package com.jseng.coffee.corner.service;

import static com.jseng.coffee.corner.repository.ProductRepository.BACON_ROLL;
import static com.jseng.coffee.corner.repository.ProductRepository.EXTRA_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FOAMED_MILK;
import static com.jseng.coffee.corner.repository.ProductRepository.FRESH_ORANGE_JUICE;
import static com.jseng.coffee.corner.repository.ProductRepository.LARGE_COFFEE;
import static com.jseng.coffee.corner.repository.ProductRepository.MEDIUM_COFFEE;
import static com.jseng.coffee.corner.repository.ProductRepository.SMALL_COFFEE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jseng.coffee.corner.domain.Product;

class ReceiptServiceTest {
	public static final String RECEIPT_WITH_NO_DISCOUNT_FILE_PATH = "src/test/resources/expectedReceipt/receiptWithNoDiscount.txt";
	public static final String RECEIPT_WITH_FREE_DRINK_DISCOUNT_FILE_PATH = "src/test/resources/expectedReceipt/receiptWithFreeDrinkDiscount.txt";
	public static final String RECEIPT_WITH_FREE_DRINK_AND_2_STAMPS_DISCOUNT_FILE_PATH = "src/test/resources/expectedReceipt/receiptWithFreeDrinkAnd2StampsDiscount.txt";
	public static final String RECEIPT_WITH_FREE_EXTRA_DISCOUNT_FILE_PATH = "src/test/resources/expectedReceipt/receiptWithFreeExtraDiscount.txt";
	public static final String RECEIPT_WITH_ALL_DISCOUNT_FILE_PATH = "src/test/resources/expectedReceipt/receiptWithAllDiscount.txt";

	private ProductService productService;
	private OrderService orderService;
	private ReceiptService receiptService;

	@BeforeEach
	public void setUp() {
		orderService = new OrderServiceImpl();
		receiptService = new ReceiptServiceImpl(orderService);
		productService = new ProductServiceImpl();
		productService.loadProducts();
	}

	@Test
	void shouldThrowIllegalArgumentExceptionWhenNoOrder() {
		final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> receiptService.printReceipt());

		final String exceptionMessage = illegalArgumentException.getMessage();

		assertAll(() -> assertNotNull(illegalArgumentException),
				() -> assertTrue(exceptionMessage.contains(ReceiptServiceImpl.NO_PRODUCT_TO_PRINT_EXCEPTION)));
	}

	@Test
	void shouldPrintReceiptWithNoDiscount() throws IOException {
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

		final String receipt = receiptService.printReceipt();

		final File file = new File(RECEIPT_WITH_NO_DISCOUNT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		assertEquals(expectedReceipt, receipt);
	}

	@Test
	void shouldPrintReceiptWithFreeLargeCoffeeDrinkWhen2StampsDiscount() throws IOException {
		final int numberOfCurrentStamp = 2;
		final Product smallCoffee = productService.findProductByName(SMALL_COFFEE);
		final Product mediumCoffee = productService.findProductByName(MEDIUM_COFFEE);
		final Product largeCoffee = productService.findProductByName(LARGE_COFFEE);
		final Product orangeJuice = productService.findProductByName(FRESH_ORANGE_JUICE);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffee);
		productsToOrder.add(mediumCoffee);
		productsToOrder.add(largeCoffee);
		productsToOrder.add(orangeJuice);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final String receipt = receiptService.printReceipt();

		final File file = new File(RECEIPT_WITH_FREE_DRINK_AND_2_STAMPS_DISCOUNT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		assertEquals(expectedReceipt, receipt);
	}

	@Test
	void shouldPrintReceiptWithFreeDrinkDiscount() throws IOException {
		final int numberOfCurrentStamp = 0;
		final Product smallCoffee1 = productService.findProductByName(SMALL_COFFEE);
		final Product smallCoffee2 = productService.findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = productService.findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = productService.findProductByName(LARGE_COFFEE);
		final Product orangeJuice1 = productService.findProductByName(FRESH_ORANGE_JUICE);
		final Product orangeJuice2 = productService.findProductByName(FRESH_ORANGE_JUICE);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffee1);
		productsToOrder.add(smallCoffee2);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);
		productsToOrder.add(orangeJuice1);
		productsToOrder.add(orangeJuice2);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final String receipt = receiptService.printReceipt();

		final File file = new File(RECEIPT_WITH_FREE_DRINK_DISCOUNT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		assertEquals(expectedReceipt, receipt);
	}

	@Test
	void shouldPrintReceiptWithFreeExtraDiscount() throws IOException {
		final int numberOfCurrentStamp = 0;
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);
		final Product foamedMilk = productService.findProductByName(FOAMED_MILK);
		final Product smallCoffeeWithExtraMilkAndFoamedMilk = productService.findProductByName(SMALL_COFFEE);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(extraMilk);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(foamedMilk);
		final Product smallCoffee = productService.findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = productService.findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = productService.findProductByName(LARGE_COFFEE);
		final Product baconRoll1 = productService.findProductByName(BACON_ROLL);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffeeWithExtraMilkAndFoamedMilk);
		productsToOrder.add(smallCoffee);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);
		productsToOrder.add(baconRoll1);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final String receipt = receiptService.printReceipt();

		final File file = new File(RECEIPT_WITH_FREE_EXTRA_DISCOUNT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		assertEquals(expectedReceipt, receipt);
	}

	@Test
	void shouldPrintReceiptWithAllDiscount() throws IOException {
		final int numberOfCurrentStamp = 0;
		final Product extraMilk = productService.findProductByName(EXTRA_MILK);
		final Product foamedMilk = productService.findProductByName(FOAMED_MILK);
		final Product smallCoffeeWithExtraMilkAndFoamedMilk = productService.findProductByName(SMALL_COFFEE);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(extraMilk);
		smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(foamedMilk);
		final Product smallCoffee = productService.findProductByName(SMALL_COFFEE);
		final Product largeCoffee1 = productService.findProductByName(LARGE_COFFEE);
		final Product largeCoffee2 = productService.findProductByName(LARGE_COFFEE);
		final Product orangeJuice1 = productService.findProductByName(FRESH_ORANGE_JUICE);
		final Product orangeJuice2 = productService.findProductByName(FRESH_ORANGE_JUICE);
		final Product baconRoll1 = productService.findProductByName(BACON_ROLL);
		final Product baconRoll2 = productService.findProductByName(BACON_ROLL);

		final List<Product> productsToOrder = new ArrayList<>();
		productsToOrder.add(smallCoffeeWithExtraMilkAndFoamedMilk);
		productsToOrder.add(smallCoffee);
		productsToOrder.add(largeCoffee1);
		productsToOrder.add(largeCoffee2);
		productsToOrder.add(baconRoll1);
		productsToOrder.add(baconRoll2);
		productsToOrder.add(orangeJuice1);
		productsToOrder.add(orangeJuice2);

		orderService.orderProducts(productsToOrder);
		orderService.applyBonusProgram(numberOfCurrentStamp);

		final String receipt = receiptService.printReceipt();

		final File file = new File(RECEIPT_WITH_ALL_DISCOUNT_FILE_PATH);
		final String expectedReceipt = Files.readString(file.toPath(), StandardCharsets.UTF_8);

		assertEquals(expectedReceipt, receipt);
	}
}
