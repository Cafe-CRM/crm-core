package com.cafe.crm.dto;

import java.util.Objects;

public class WeekDayFilter {
    private int number;
    private String name;
    private boolean check;

    public WeekDayFilter(int number, String name, boolean check) {
        this.number = number;
        this.name = name;
        this.check = check;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekDayFilter that = (WeekDayFilter) o;
        return number == that.number &&
                check == that.check &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(number, name, check);
    }

    @Override
    public String toString() {
        return "WeekDayFilter{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", check=" + check +
                '}';
    }
}
