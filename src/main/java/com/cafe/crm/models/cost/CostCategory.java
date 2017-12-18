package com.cafe.crm.models.cost;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
@Table(name = "cost_category")
public class CostCategory extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"Название\" не может быть пустым")
	private String name;

	public CostCategory() {
	}

	public CostCategory(String name) {
		this.name = name;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CostCategory that = (CostCategory) o;

		return name != null ? name.equals(that.name) : that.name == null;

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "CostCategory{" +
				"name='" + name + '\'' +
				'}';
	}
}
