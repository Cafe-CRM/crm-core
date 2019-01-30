package com.cafe.crm.dto;

import java.time.LocalDate;
import java.util.Objects;

public class DateClientAmount {
    private LocalDate date;
    private int clientsNumber;

    public DateClientAmount(LocalDate date, int clientsNumber) {
        this.date = date;
        this.clientsNumber = clientsNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getClientsNumber() {
        return clientsNumber;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setClientsNumber(int clientsNumber) {
        this.clientsNumber = clientsNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateClientAmount that = (DateClientAmount) o;
        return clientsNumber == that.clientsNumber &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, clientsNumber);
    }

    @Override
    public String toString() {
        return "ClientAmountByDateDTO{" +
                "date=" + date +
                ", clientsNumber=" + clientsNumber +
                '}';
    }
}
