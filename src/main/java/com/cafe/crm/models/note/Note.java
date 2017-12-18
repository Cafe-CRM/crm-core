package com.cafe.crm.models.note;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Note extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"name\" не может быть пустым")
	private String name;

	private boolean enable = true;

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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
