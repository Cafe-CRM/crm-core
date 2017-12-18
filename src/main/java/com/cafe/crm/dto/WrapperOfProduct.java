package com.cafe.crm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


public class WrapperOfProduct implements Serializable {  // wrapper for menuController

	private Long productId;

	private List<String> names;
	private List<Double> amount;
	private Long idCat;

	@NotNull(message = "Укажите название")
	@NotEmpty
	@Length(min = 1, max = 30, message = "Длина названия должна быть от 1 до 30 символов")
	private String name;

	@NotNull(message = "Укажите описание")
	private String description;

	@NotNull(message = "Укажите цену")
	private Double cost;

	private List<Long> staffPercentPosition;
	private List<Integer> staffPercentPercent;

	@NotNull(message = "selfCost field have to be greater or equal to zero")
	@Min(value = 0, message = "selfCost field have to be greater or equal to zero")
	private Double selfCost;

	public WrapperOfProduct() {

	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getId() {
		return idCat;
	}

	public void setIdCat(Long idCat) {
		this.idCat = idCat;
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

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Double> getAmount() {
		return amount;
	}

	public void setAmount(List<Double> amount) {
		this.amount = amount;
	}

	public List<Long> getStaffPercentPosition() {
		return staffPercentPosition;
	}

	public void setStaffPercentPosition(List<Long> staffPercentPosition) {
		this.staffPercentPosition = staffPercentPosition;
	}

	public List<Integer> getStaffPercentPercent() {
		return staffPercentPercent;
	}

	public void setStaffPercentPercent(List<Integer> staffPercentPercent) {
		this.staffPercentPercent = staffPercentPercent;
	}
}

