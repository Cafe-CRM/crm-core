package com.cafe.crm.dto;


public class ProductStat {
	private String name;
	private Long amount;
	private Double price;
	private boolean deleted;

	public ProductStat() {

	}

	public ProductStat(String name, Long amount, Double price, boolean deleted) {
		this.name = name;
		this.amount = amount;
		this.price = price;
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
