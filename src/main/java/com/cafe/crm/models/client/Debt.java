package com.cafe.crm.models.client;

import com.cafe.crm.dto.DebtDTO;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.utils.annotation.Dateable;
import com.cafe.crm.utils.annotation.SelfDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yc.easytransformer.annotations.NotTransform;
import com.yc.easytransformer.annotations.Transform;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table
@Transform(DebtDTO.class)
@Dateable
public class Debt extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String debtor;

	private Double debtAmount;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotTransform
	@SelfDate
	private LocalDate givenDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotTransform
	private LocalDate repaidDate;

	private boolean repaid = false;

	private boolean cashBoxDebt = false;

	private boolean deleted = false;

	@NotTransform
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "given_shift")
	private Shift givenShift;

	@NotTransform
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repaid_shift")
	private Shift repaidShift;

	@NotTransform
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_shift")
	private Shift deletedShift;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "calculate_id")
	@NotTransform
	private Calculate calculate;

	public Debt() {}

	public Debt(String debtor, Double debtAmount, LocalDate givenDate) {
		this.debtor = debtor;
		this.debtAmount = debtAmount;
		this.givenDate = givenDate;
	}

	public Debt(String debtor, Double debtAmount, LocalDate givenDate, Shift givenShift) {
		this.debtor = debtor;
		this.debtAmount = debtAmount;
		this.givenDate = givenDate;
		this.givenShift = givenShift;
	}

	public Long getId() {
		return id;
	}

	public String getDebtor() {
		return debtor;
	}

	public void setDebtor(String debtor) {
		this.debtor = debtor;
	}

	public Double getDebtAmount() {
		return debtAmount;
	}

	public void setDebtAmount(Double debtAmount) {
		this.debtAmount = debtAmount;
	}

	public LocalDate getGivenDate() {
		return givenDate;
	}

	public void setGivenDate(LocalDate givenDate) {
		this.givenDate = givenDate;
	}

	public LocalDate getRepaidDate() {
		return repaidDate;
	}

	public void setRepaidDate(LocalDate repaidDate) {
		this.repaidDate = repaidDate;
	}

	public boolean isRepaired() {
		return repaid;
	}

	public void setRepaired(boolean repaired) {
		this.repaid = repaired;
	}

	public boolean isCashBoxDebt() {
        return cashBoxDebt;
    }

    public void setCashBoxDebt(boolean cashBoxDebt) {
        this.cashBoxDebt = cashBoxDebt;
    }

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Shift getGivenShift() {
		return givenShift;
	}

	public void setGivenShift(Shift givenShift) {
		this.givenShift = givenShift;
	}

	public Shift getRepaidShift() {
		return repaidShift;
	}

	public Shift getDeletedShift() {
		return deletedShift;
	}

	public void setDeletedShift(Shift deletedShift) {
		this.deletedShift = deletedShift;
	}

	public void setRepairedShift(Shift repaidShift) {
		this.repaidShift = repaidShift;
	}

	public Calculate getCalculate() {
		return calculate;
	}

	public void setCalculate(Calculate calculate) {
		this.calculate = calculate;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return cashBoxDebt == debt.cashBoxDebt &&
                Objects.equals(id, debt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cashBoxDebt);
    }
}
