package com.cafe.crm.dto;

import java.time.LocalDate;

public class OpenMissingShiftDTO {
    private long id;

    private double cashBox;

    private double bankCashBox;

    private double profit;

    private LocalDate shiftDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCashBox() {
        return cashBox;
    }

    public void setCashBox(double cashBox) {
        this.cashBox = cashBox;
    }

    public double getBankCashBox() {
        return bankCashBox;
    }

    public void setBankCashBox(double bankCashBox) {
        this.bankCashBox = bankCashBox;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
}
