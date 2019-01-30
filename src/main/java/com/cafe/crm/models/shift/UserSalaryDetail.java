package com.cafe.crm.models.shift;

import com.cafe.crm.dto.SaleProductOnDay;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.user.User;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@SqlResultSetMapping(
		name = "SaleProductOnDayMapping",
		classes = {
				@ConstructorResult(
						targetClass = SaleProductOnDay.class,
						columns = {
								@ColumnResult(name = "productId", type = Long.class),
								@ColumnResult(name = "productName", type = String.class),
								@ColumnResult(name = "date", type = LocalDate.class),
								@ColumnResult(name = "count", type = Long.class),
						}
				)
		}
)

@Entity
public class UserSalaryDetail extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private int totalSalary;

	private int totalBonus;

	private int shiftSalary;

	private int shiftAmount;

	private int salaryBalance;

	private int bonusBalance;

	private int paidSalary;

	private int paidBonus;

	private boolean isPaidDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shift_id", nullable = false)
	private Shift shift;

	public UserSalaryDetail(User user, int totalSalary, int totalBonus, int salaryBalance, int bonusBalance,
							int shiftSalary, int shiftAmount, Shift shift, boolean isPaidDetail) {
		this.user = user;
		this.totalSalary = totalSalary;
		this.totalBonus = totalBonus;
		this.shiftAmount = shiftAmount;
		this.salaryBalance = salaryBalance;
		this.bonusBalance = bonusBalance;
		this.shiftSalary = shiftSalary;
		this.shift = shift;
		this.isPaidDetail = isPaidDetail;
	}

	public UserSalaryDetail(User user, int totalSalary, int totalBonus, int salaryBalance, int bonusBalance,
							int shiftSalary, int shiftAmount, int paidSalary, int paidBonus, Shift shift, boolean isPaidDetail) {
		this.user = user;
		this.totalSalary = totalSalary;
		this.totalBonus = totalBonus;
		this.shiftAmount = shiftAmount;
		this.salaryBalance = salaryBalance;
		this.bonusBalance = bonusBalance;
		this.shiftSalary = shiftSalary;
		this.paidSalary = paidSalary;
		this.paidBonus = paidBonus;
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

	public int getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(int totalSalary) {
		this.totalSalary = totalSalary;
	}

	public int getTotalBonus() {
		return totalBonus;
	}

	public void setTotalBonus(int totalBonus) {
		this.totalBonus = totalBonus;
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

	public int getSalaryBalance() {
		return salaryBalance;
	}

	public void setSalaryBalance(int salaryBalance) {
		this.salaryBalance = salaryBalance;
	}

	public int getBonusBalance() {
		return bonusBalance;
	}

	public void setBonusBalance(int bonusBalance) {
		this.bonusBalance = bonusBalance;
	}

	public int getPaidSalary() {
		return paidSalary;
	}

	public void setPaidSalary(int paidSalary) {
		this.paidSalary = paidSalary;
	}

	public int getPaidBonus() {
		return paidBonus;
	}

	public void setPaidBonus(int paidBonus) {
		this.paidBonus = paidBonus;
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

		if (!id.equals(detail.id)) return false;
		if (!user.equals(detail.user)) return false;
		return shift.equals(detail.shift);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + shift.hashCode();
		return result;
	}
}
