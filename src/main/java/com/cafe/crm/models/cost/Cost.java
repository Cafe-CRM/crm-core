package com.cafe.crm.models.cost;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.shift.Shift;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Cost extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"Название\" не может быть пустым")
	private String name;

	@NotNull(message = "Поле \"Цена\" не может быть пустым")
	private double price;

	@NotNull(message = "Поле \"Коливество\" не может быть пустым")
	private double quantity;

	@NotNull(message = "Поле \"Дата\" не может быть пустым")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Valid
	@NotNull(message = "Поле \"Категория\" не может быть пустым")
	@ManyToOne
	@JoinColumn(name = "cost_category_id")
	private CostCategory category;

	private boolean visible = true;

	@ManyToOne
	@JoinColumn(name = "shift_id", nullable = false)
	private Shift shift;

	public Cost() {
	}

	public Cost(String name, double price, double quantity, CostCategory category, LocalDate date) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.category = category;
		this.date = date;
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

	public CostCategory getCategory() {
		return category;
	}

	public void setCategory(CostCategory category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Cost goods = (Cost) o;

		if (Double.compare(goods.price, price) != 0) return false;
		if (name != null ? !name.equals(goods.name) : goods.name != null) return false;
		return category != null ? category.equals(goods.category) : goods.category == null;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = name != null ? name.hashCode() : 0;
		temp = Double.doubleToLongBits(price);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (category != null ? category.hashCode() : 0);
		return result;
	}
}
