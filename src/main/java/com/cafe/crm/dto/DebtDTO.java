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

    private LocalDate date;

    private boolean visible = true;

    private boolean cashBoxDebt = false;

    private Shift shift;

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

    public boolean isCashBoxDebt() {
        return cashBoxDebt;
    }

    public void setCashBoxDebt(boolean cashBoxDebt) {
        this.cashBoxDebt = cashBoxDebt;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Calculate getCalculate() {
        return calculate;
    }

    public void setCalculate(Calculate calculate) {
        this.calculate = calculate;
    }
}
