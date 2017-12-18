package com.cafe.crm.models.user;


import com.cafe.crm.dto.PositionDTO;
import com.cafe.crm.models.BaseEntity;
import com.yc.easytransformer.annotations.Transform;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "positions")
@Transform(PositionDTO.class)
public class Position extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"name\" не может быть пустым")
	@Column(nullable = false)
	private String name;

	@Column(name = "percent")
	@Min(value = 0, message = "Поле \"percentageOfSales\" не может быть меньше 0")
	@Max(value = 100, message = "Поле \"percentageOfSales\" не может быть больше 100")
	private Integer percentageOfSales = 0;

	@Column(name = "use_percent")
	private boolean isPositionUsePercentOfSales = false;

	public Position() {
	}

	public Position(String name) {
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

	public Integer getPercentageOfSales() {
		return percentageOfSales;
	}

	public void setPercentageOfSales(Integer percentageOfSales) {
		this.percentageOfSales = percentageOfSales;
	}

	public boolean isPositionUsePercentOfSales() {
		return isPositionUsePercentOfSales;
	}

	public void setIsPositionUsePercentOfSales(boolean isPositionUsePercentOfSales) {
		this.isPositionUsePercentOfSales = isPositionUsePercentOfSales;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position position = (Position) o;

		return name != null ? name.equals(position.name) : position.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
