package com.cafe.crm.dto;

import java.util.Objects;

public class WeekDayFilter {
    private int id;
    private String name;

    public WeekDayFilter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekDayFilter that = (WeekDayFilter) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "WeekDayFilter{" +
                "number=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
