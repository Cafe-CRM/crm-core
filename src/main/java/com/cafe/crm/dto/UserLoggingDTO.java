package com.cafe.crm.dto;


import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.yc.easytransformer.annotations.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transform(User.class)
public class UserLoggingDTO {

	private Long id;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

	private int shiftSalary;

	private List<RoleDTO> roles = new ArrayList<>();

	private List<PositionDTO> positions = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getShiftSalary() {
		return shiftSalary;
	}

	public void setShiftSalary(int shiftSalary) {
		this.shiftSalary = shiftSalary;
	}

	public List<RoleDTO> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDTO> roles) {
		this.roles = roles;
	}

	public List<PositionDTO> getPositions() {
		return positions;
	}

	public void setPositions(List<PositionDTO> positions) {
		this.positions = positions;
	}

	public void addRole(RoleDTO role) {
		this.roles.add(role);
	}

	public void addPosition(PositionDTO position) {
		this.positions.add(position);
	}
}
