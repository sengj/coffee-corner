package com.jseng.coffee.corner.domain;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

public class Product implements Comparable<Product>{
	private String name;
	private final BigDecimal price;
	private final ProductType type;
	private Set<Product> extraList;

	public Product(String name, BigDecimal price, ProductType type, Set<Product> extraList) {
		super();
		this.name = name;
		this.price = price;
		this.type = type;
		this.extraList = extraList;
	}

	public Product(Product product) {
		this.name = product.name;
		this.price = product.price;
		this.type = product.type;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public ProductType getType() {
		return type;
	}

	public Set<Product> getExtraList() {
		return extraList;
	}

	public void addExtra(Product extra) {
		if (this.extraList == null) {
			this.extraList = new TreeSet<>();
		}
		this.extraList.add(extra);
	}

	public void addExtras(Set<Product> extraList) {
		if (this.extraList == null) {
			this.extraList = new TreeSet<>();
		}
		this.extraList.addAll(extraList);
	}

	@Override
	public int compareTo(Product product) {
        return name.compareTo(product.name);  
	}
}
