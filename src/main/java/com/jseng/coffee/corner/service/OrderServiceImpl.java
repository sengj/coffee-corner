package com.jseng.coffee.corner.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.jseng.coffee.corner.domain.Product;
import com.jseng.coffee.corner.domain.ProductType;

public class OrderServiceImpl implements OrderService {

	public static final String EMPTY_PRODUCT_ORDERED_EXCEPTION = "Products ordered must have at least one product.";
	public static final String NB_OF_CURRENT_STAMP_EXCEPTION = "Number of current stamp can't be negative.";
	public static final String EXTRA_CAN_T_BE_ORDERED_ALONE_EXCEPTION = "Number of current stamp can't be negative.";
	public static final String ONLY_DRINKS_CAN_HAVE_EXTRA_EXCEPTION = "Only drinks can have extras.";
	public static final String EXTRA_CAN_T_HAVE_EXTRA_EXCEPTION = "Extra can't have extras.";
	public static final String ONLY_EXTRA_CAN_BE_EXTRA_EXCEPTION = "Only extra can be extra.";

	private List<Product> productList;

	private List<Product> freeProductList;

	public OrderServiceImpl() {
		super();
		this.freeProductList = new ArrayList<>();
	}

	@Override
	public void orderProducts(List<Product> productsToOrder) {
		validateProducts(productsToOrder);

		productList = productsToOrder;
	}

	private void validateProducts(List<Product> productsToOrder) {
		if (productsToOrder == null || productsToOrder.isEmpty()) {
			throw new IllegalArgumentException(EMPTY_PRODUCT_ORDERED_EXCEPTION);
		}

		if (hasOrderedExtraAlone(productsToOrder)) {
			throw new IllegalArgumentException(EXTRA_CAN_T_BE_ORDERED_ALONE_EXCEPTION);
		}

		if (hasOrderedExtraWithNotDrinks(productsToOrder)) {
			throw new IllegalArgumentException(ONLY_DRINKS_CAN_HAVE_EXTRA_EXCEPTION);
		}

		if (hasOrderedExtraWithExtra(productsToOrder)) {
			throw new IllegalArgumentException(EXTRA_CAN_T_HAVE_EXTRA_EXCEPTION);
		}

		if (hasDrinkOrSnackAsExtra(productsToOrder)) {
			throw new IllegalArgumentException(ONLY_EXTRA_CAN_BE_EXTRA_EXCEPTION);
		}
	}

	private boolean hasDrinkOrSnackAsExtra(List<Product> productsToOrder) {
		return productsToOrder.stream()
				.filter(product -> ProductType.DRINK == product.getType() && product.getExtraList() != null
						&& !product.getExtraList().isEmpty())
				.flatMap(drink -> drink.getExtraList().stream()).filter(extra -> ProductType.EXTRA != extra.getType())
				.count() > 0;
	}

	private boolean hasOrderedExtraWithExtra(List<Product> productsToOrder) {
		return productsToOrder.stream()
				.filter(product -> ProductType.DRINK == product.getType() && product.getExtraList() != null
						&& !product.getExtraList().isEmpty())
				.flatMap(drink -> drink.getExtraList().stream())
				.filter(extra -> extra.getExtraList() != null && !extra.getExtraList().isEmpty()).count() > 0;
	}

	private boolean hasOrderedExtraWithNotDrinks(List<Product> productsToOrder) {
		return productsToOrder.stream().filter(product -> ProductType.DRINK != product.getType()
				&& product.getExtraList() != null && !product.getExtraList().isEmpty()).count() > 0;
	}

	private boolean hasOrderedExtraAlone(List<Product> productsToOrder) {
		return productsToOrder.stream().filter(product -> ProductType.EXTRA == product.getType()).count() > 0;
	}

	@Override
	public List<Product> getProducts() {
		return productList;
	}

	@Override
	public List<Product> getFreeProducts() {
		return freeProductList;
	}

	@Override
	public BigDecimal getTotalAmount() {
		BigDecimal freeProductsTotalAmount = freeProductList.stream().map(Product::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal extraProductsTotalAmount = productList.stream()
				.filter(product -> ProductType.DRINK == product.getType() && product.getExtraList() != null
						&& !product.getExtraList().isEmpty())
				.flatMap(drink -> drink.getExtraList().stream()).map(Product::getPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal productTotalAmount = productList.stream().map(Product::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		return productTotalAmount.add(extraProductsTotalAmount).subtract(freeProductsTotalAmount);

	}

	@Override
	public void applyBonusProgram(int nbOfCurrentStamp) {
		if (nbOfCurrentStamp < 0) {
			throw new IllegalArgumentException(NB_OF_CURRENT_STAMP_EXCEPTION);
		}
		if (productList == null || productList.isEmpty()) {
			throw new IllegalArgumentException(EMPTY_PRODUCT_ORDERED_EXCEPTION);
		}

		applyFreeExtraOnCheapestOne();
		applyFreeDrinksWithStampCard(nbOfCurrentStamp);
	}

	private void applyFreeDrinksWithStampCard(int nbOfCurrentStamp) {
		List<Product> freeDrinks = new ArrayList<>();

		List<Product> drinksOrdered = productList.stream().filter(product -> ProductType.DRINK == product.getType())
				.toList();

		IntStream.iterate(0, i -> i + 1).limit(drinksOrdered.size())
				.filter(i -> (i + nbOfCurrentStamp) > 0 && (i + nbOfCurrentStamp + 1) % 5 == 0)
				.forEach(i -> freeDrinks.add(drinksOrdered.get(i)));

		freeProductList.addAll(freeDrinks);
	}

	private void applyFreeExtraOnCheapestOne() {
		long numberOfDrinkOrdered = productList.stream().filter(product -> ProductType.DRINK == product.getType())
				.count();
		long numberOfSnacksOrdered = productList.stream().filter(product -> ProductType.SNACK == product.getType())
				.count();
		long numberOfFreeExtra = Math.min(numberOfDrinkOrdered, numberOfSnacksOrdered);

		List<Product> cheapestFreeExtraToOffer = productList.stream()
				.filter(product -> ProductType.DRINK == product.getType() && product.getExtraList() != null
						&& !product.getExtraList().isEmpty())
				.flatMap(drink -> drink.getExtraList().stream())
				.sorted((extra1, extra2) -> extra1.getPrice().compareTo(extra2.getPrice())).limit(numberOfFreeExtra)
				.toList();

		freeProductList.addAll(cheapestFreeExtraToOffer);
	}
}
