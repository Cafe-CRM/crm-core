package com.cafe.crm.models.menu;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ingredients")
public class Ingredients extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Заполните поле")
	@Size(max = 30, message = "Максимальный размер 30 символов")
	private String name;

	@NotBlank(message = "Заполните поле")
	@Size(max = 20, message = "Максимальный размер 20 символов")
	private String dimension;

	@NotNull(message = "Заполните поле")
	private Double amount = 0D;

	private Double price = 0D;

	public Ingredients() {

	}

	public Ingredients(String name, String dimension, Double amount, Double price) {
		this.name = name;
		this.dimension = dimension;
		this.amount = amount;
		this.price = price;
	}

	public Ingredients(Long id, String name, String dimension, Double amount) {
		this.id = id;
		this.name = name;
		this.dimension = dimension;
		this.amount = amount;
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

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}


	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Ingredients that = (Ingredients) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;
		return amount != null ? amount.equals(that.amount) : that.amount == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (dimension != null ? dimension.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Ingredients{" +
				"id=" + id +
				", name='" + name + '\'' +
				", dimension='" + dimension + '\'' +
				", amount=" + amount +
				'}';
	}
}
