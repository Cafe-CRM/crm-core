package com.cafe.crm.dto;


import com.cafe.crm.models.menu.Product;

public class WrapperOfEditProduct {  // wrapper for menuController

	private Long id;
	private String name;
	private String description;
	private Double cost;
	private Double selfCost;


	public WrapperOfEditProduct() {
	}

	public WrapperOfEditProduct(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.cost = product.getCost();
		this.selfCost = product.getSelfCost();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getSelfCost() {
		return selfCost;
	}

	public void setSelfCost(Double selfCost) {
		this.selfCost = selfCost;
	}
}
