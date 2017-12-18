package com.cafe.crm.models.property;


import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "property")
public class Property extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"name\" не может быть пустым")
	private String name;

	@NotBlank(message = "Поле \"value\" не может быть пустым")
	private String value;

	public Property() {
	}

	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;

		Property property = (Property) object;

		if (id != null ? !id.equals(property.id) : property.id != null) return false;
		if (name != null ? !name.equals(property.name) : property.name != null) return false;
		return value != null ? value.equals(property.value) : property.value == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
