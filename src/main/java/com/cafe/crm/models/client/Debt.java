package com.cafe.crm.models.client;

import com.cafe.crm.dto.DebtDTO;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.shift.Shift;
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
public class Debt extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String debtor;

	private Double debtAmount;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotTransform
	private LocalDate date;

	private boolean repaired = false;

	private boolean cashBoxDebt = false;

	@NotTransform
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "given_shift")
	private Shift givenShift;

	@NotTransform
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repaired_shift")
	private Shift repaidShift;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "calculate_id")
	@NotTransform
	private Calculate calculate;

	public Debt() {}

	public Debt(String debtor, Double debtAmount, LocalDate date) {
		this.debtor = debtor;
		this.debtAmount = debtAmount;
		this.date = date;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isRepaired() {
		return repaired;
	}

	public void setRepaired(boolean repaired) {
		this.repaired = repaired;
	}

	public boolean isCashBoxDebt() {
        return cashBoxDebt;
    }

    public void setCashBoxDebt(boolean cashBoxDebt) {
        this.cashBoxDebt = cashBoxDebt;
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
