package com.jseng.coffee.corner.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import com.jseng.coffee.corner.domain.Product;

public class ReceiptServiceImpl implements ReceiptService {

	public static final String NO_PRODUCT_TO_PRINT_EXCEPTION = "Order should have at least one product to print receipt";

	public static final String CHARLENE_S_COFFEE_CORNER = "--------------Charlene's Coffee Corner--------------";
	public static final String CURRENCY = " CHF";
	public static final String DISCOUNTS = "---------------------Discounts----------------------";
	public static final String EXTRA = "  - with ";
	public static final String DIVIDER = "----------------------------------------------------";
	public static final String LINE_BREAK = System.lineSeparator();
	public static final String MINUS = "-";
	public static final String PRODUCTS = "----------------------Products----------------------";
	public static final String THANK_YOU = "--------------Thank you for your visit--------------";
	public static final String TOTAL = "Total";

	public static final int TICKET_WIDTH = 52;

	private final OrderService orderService;

	public ReceiptServiceImpl(OrderService orderService) {
		super();
		this.orderService = orderService;
	}

	@Override
	public String printReceipt() {
		if (orderService.getProducts() == null || orderService.getProducts().isEmpty()) {
			throw new IllegalArgumentException(NO_PRODUCT_TO_PRINT_EXCEPTION);
		}

		StringBuilder sb = new StringBuilder();

		printHeaderReceipt(sb);
		printProductsReceipt(sb);
		printDiscountsReceipt(sb);
		printTotalReceipt(sb);
		printFooterReceipt(sb);

		return sb.toString();
	}

	private void printHeaderReceipt(StringBuilder sb) {
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
		sb.append(LINE_BREAK);
		sb.append(CHARLENE_S_COFFEE_CORNER);
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
		sb.append(LINE_BREAK);
	}

	private void printFooterReceipt(StringBuilder sb) {
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
		sb.append(LINE_BREAK);
		sb.append(THANK_YOU);
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
	}

	private void printDiscountsReceipt(StringBuilder sb) {
		if (!orderService.getFreeProducts().isEmpty()) {
			sb.append(LINE_BREAK);
			sb.append(DISCOUNTS);
			orderService.getFreeProducts().stream().forEach(product -> {
				BigDecimal priceWithTwoDecimal = product.getPrice().setScale(2, RoundingMode.HALF_UP);
				sb.append(LINE_BREAK);
				sb.append(product.getName());
				IntStream
						.range(0,
								TICKET_WIDTH - MINUS.length() - product.getName().length()
										- priceWithTwoDecimal.toString().length() - CURRENCY.length())
						.forEach(i -> sb.append(" "));
				sb.append(MINUS);
				sb.append(priceWithTwoDecimal);
				sb.append(CURRENCY);
			});
			sb.append(LINE_BREAK);
		}
	}

	private void printProductsReceipt(StringBuilder sb) {
		sb.append(LINE_BREAK);
		sb.append(PRODUCTS);
		orderService.getProducts().stream().forEach(product -> {
			BigDecimal priceWithTwoDecimal = product.getPrice().setScale(2, RoundingMode.HALF_UP);
			sb.append(LINE_BREAK);
			sb.append(product.getName());
			IntStream.range(0, TICKET_WIDTH - product.getName().length() - priceWithTwoDecimal.toString().length()
					- CURRENCY.length()).forEach(i -> sb.append(" "));
			sb.append(priceWithTwoDecimal);
			sb.append(CURRENCY);
			if (product.getExtraList() != null) {
				printExtrasOfProduct(sb, product);
			}
		});
		sb.append(LINE_BREAK);
	}

	private void printExtrasOfProduct(StringBuilder sb, final Product product) {
		product.getExtraList().stream().forEach(extra -> {
			BigDecimal priceWithTwoDecimal = extra.getPrice().setScale(2, RoundingMode.HALF_UP);
			sb.append(LINE_BREAK);
			sb.append(EXTRA);
			sb.append(extra.getName());
			IntStream
					.range(0,
							TICKET_WIDTH - extra.getName().length() - EXTRA.length()
									- priceWithTwoDecimal.toString().length() - CURRENCY.length())
					.forEach(i -> sb.append(" "));
			sb.append(priceWithTwoDecimal);
			sb.append(CURRENCY);
		});
	}

	private void printTotalReceipt(StringBuilder sb) {
		BigDecimal total = orderService.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
		sb.append(LINE_BREAK);
		sb.append(TOTAL);
		IntStream.range(0, TICKET_WIDTH - TOTAL.length() - total.toString().length() - CURRENCY.length())
				.forEach(i -> sb.append(" "));
		sb.append(total);
		sb.append(CURRENCY);
		sb.append(LINE_BREAK);
		sb.append(DIVIDER);
		sb.append(LINE_BREAK);
	}

}
