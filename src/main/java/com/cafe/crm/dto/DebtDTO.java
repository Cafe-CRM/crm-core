package com.cafe.crm.dto;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;
import com.yc.easytransformer.annotations.Transform;

import java.time.LocalDate;

@Transform(Debt.class)
public class DebtDTO {
    private Long id;

    private String debtor;

    private Double debtAmount;

    private LocalDate givenDate;

    private LocalDate repaidDate;

    private boolean visible;

    private boolean cashBoxDebt;

    private boolean deleted;

    private Shift givenShift;

    private Shift repaidShift;

    private Calculate calculate;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public void setRepaidShift(Shift repaidShift) {
        this.repaidShift = repaidShift;
    }

    public Calculate getCalculate() {
        return calculate;
    }

    public void setCalculate(Calculate calculate) {
        this.calculate = calculate;
    }
}
