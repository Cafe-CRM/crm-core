package com.cafe.crm.dto;

import java.util.List;

public class WeekDayFilterList {
    private List<WeekDayFilter> weekDays;

    public WeekDayFilterList() {
    }

    public List<WeekDayFilter> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<WeekDayFilter> weekDays) {
        this.weekDays = weekDays;
    }
}
