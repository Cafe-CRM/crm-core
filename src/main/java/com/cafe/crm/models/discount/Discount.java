package com.cafe.crm.models.discount;

import com.cafe.crm.models.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "discount")
public class Discount extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@Size(max = 30)
	@NotNull
	private String description = "";

	@NotNull
	@Max(100)
	private Long discount = 0L;

	@NotNull
	private boolean isOpen = true;

	public Discount() {
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean open) {
		isOpen = open;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getDiscount() {
		return discount;
	}

	public void setDiscount(Long discount) {
		this.discount = discount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Discount discount = (Discount) o;

		if (id != null ? !id.equals(discount.id) : discount.id != null) return false;
		return description != null ? description.equals(discount.description) : discount.description == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}
