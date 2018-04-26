package com.cafe.crm.models.user;

import com.cafe.crm.dto.RoleDTO;
import com.cafe.crm.dto.UserDTO;
import com.yc.easytransformer.annotations.Transform;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Transform(RoleDTO.class)
public class Role {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"name\" не может быть пустым")
	@Column(unique = true, nullable = false)
	private String name;

	public Role() {
	}

	public Role(String name) {
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

		Role role = (Role) o;

		return name != null ? name.equals(role.name) : role.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return name;
	}
}