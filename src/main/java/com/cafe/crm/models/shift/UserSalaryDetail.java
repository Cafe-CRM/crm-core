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

	private int balance;

	private int salary;

	private int shiftSalary;

	private int shiftAmount;

	private int bonus;

	private boolean isPaidDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shift_id", nullable = false)
	private Shift shift;

	public UserSalaryDetail(User user, int balance, int salary, int shiftSalary, int shiftAmount,
							int bonus, Shift shift, boolean isPaidDetail) {
		this.user = user;
		this.balance = balance;
		this.salary = salary;
		this.shiftAmount = shiftAmount;
		this.bonus = bonus;
		this.shiftSalary = shiftSalary;
		this.shift = shift;
		this.isPaidDetail = isPaidDetail;
	}

	public UserSalaryDetail() {
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

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getShiftSalary() {
		return shiftSalary;
	}

	public void setShiftSalary(int shiftSalary) {
		this.shiftSalary = shiftSalary;
	}

	public int getShiftAmount() {
		return shiftAmount;
	}

	public void setShiftAmount(int shiftAmount) {
		this.shiftAmount = shiftAmount;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public boolean isPaidDetail() {
		return isPaidDetail;
	}

	public void setPaidDetail(boolean paidDetail) {
		isPaidDetail = paidDetail;
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

		UserSalaryDetail detail = (UserSalaryDetail) o;

		if (balance != detail.balance) return false;
		if (salary != detail.salary) return false;
		if (shiftSalary != detail.shiftSalary) return false;
		if (shiftAmount != detail.shiftAmount) return false;
		if (bonus != detail.bonus) return false;
		if (isPaidDetail != detail.isPaidDetail) return false;
		if (!id.equals(detail.id)) return false;
		if (!user.equals(detail.user)) return false;
		return shift.equals(detail.shift);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + balance;
		result = 31 * result + salary;
		result = 31 * result + shiftSalary;
		result = 31 * result + shiftAmount;
		result = 31 * result + bonus;
		result = 31 * result + (isPaidDetail ? 1 : 0);
		result = 31 * result + shift.hashCode();
		return result;
	}
}
