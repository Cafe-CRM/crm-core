package com.cafe.crm.dto;

import java.util.Date;
import java.util.Objects;

public class HourIntervalClientAmount {
    private int hourStart;
    private int hourEnd;
    private int clientsNumber;
    private double clientsNumberPerDay;

    public HourIntervalClientAmount(int hourStart, int hourEnd, int clientsNumber, double clientsNumberPerDay) {
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
        this.clientsNumber = clientsNumber;
        this.clientsNumberPerDay = clientsNumberPerDay;
    }

    public HourIntervalClientAmount(int hourStart, int hourEnd) {
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
    }

    public int getHourStart() {
        return hourStart;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public int getClientsNumber() {
        return clientsNumber;
    }

    public double getClientsNumberPerDay() {
        return clientsNumberPerDay;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    public void setClientsNumber(int clientsNumber) {
        this.clientsNumber = clientsNumber;
    }

    public void setClientsNumberPerDay(double clientsNumberPerDay) {
        this.clientsNumberPerDay = clientsNumberPerDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourIntervalClientAmount that = (HourIntervalClientAmount) o;
        return hourStart == that.hourStart &&
                hourEnd == that.hourEnd &&
                clientsNumber == that.clientsNumber &&
                clientsNumberPerDay == that.clientsNumberPerDay;
    }

    @Override
    public int hashCode() {

        return Objects.hash(hourStart, hourEnd, clientsNumber, clientsNumberPerDay);
    }

    @Override
    public String toString() {
        return String.valueOf(hourStart) + " - " + hourEnd;
    }
}
