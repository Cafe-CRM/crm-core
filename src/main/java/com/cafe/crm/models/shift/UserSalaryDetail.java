package com.cafe.crm.models.shift;

import com.cafe.crm.models.user.User;
import javax.persistence.*;


@Entity
public class UserSalaryDetail {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private int salary;

	private int shiftSalary;

	private int shiftAmount;

	private int bonus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shift_id", nullable = false)
	private Shift shift;

	public UserSalaryDetail(User user, int salary, int shiftSalary, int shiftAmount, int bonus, Shift shift) {
		this.user = user;
		this.salary = salary;
		this.shiftAmount = shiftAmount;
		this.bonus = bonus;
		this.shiftSalary = shiftSalary;
		this.shift = shift;
	}

	public UserSalaryDetail() {
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = shiftSalary;
	}

	public int getShiftSalary() {
		return shiftSalary;
	}

	public void setShiftSalary(int shiftSalary) {
		this.shiftSalary = shiftSalary;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public int getShiftAmount() {
		return shiftAmount;
	}

	public void setShiftAmount(int shiftAmount) {
		this.shiftAmount = shiftAmount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserSalaryDetail detail = (UserSalaryDetail) o;

		if (user != null ? !user.equals(detail.user) : detail.user != null) return false;
		if (salary != detail.salary) return false;
		if (bonus != detail.bonus) return false;
		if (shiftAmount != detail.shiftAmount) return false;
		return shift != null ? !shift.equals(detail.shift) : detail.shift != null;
	}

	@Override
	public int hashCode() {
		int result = user != null ? user.hashCode() : 0;
		result = 31 * result + (shift != null ? shift.hashCode() : 0);
		result = 31 * result + (salary + bonus + shiftAmount);
		return result;
	}
}
