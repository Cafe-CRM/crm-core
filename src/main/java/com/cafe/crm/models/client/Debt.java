package com.cafe.crm.models.client;

import com.cafe.crm.models.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table
public class Debt extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String debtor;

	private Double debtAmount;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	private boolean visible = true;

	public Debt() {
	}

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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Debt debt = (Debt) o;
		return Objects.equals(id, debt.id) &&
				Objects.equals(debtor, debt.debtor) &&
				Objects.equals(debtAmount, debt.debtAmount) &&
				Objects.equals(date, debt.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, debtor, debtAmount, date);
	}
}
