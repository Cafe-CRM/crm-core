package com.cafe.crm.dto;

import com.cafe.crm.controllers.report.EnumWeekDay;

import java.util.List;
import java.util.Objects;

public class WeekDayFilterList {
//    private String filterName;
//    private List<WeekDayFilter> checkedWeekDays;
    private List<EnumWeekDay> selectedWeekDays;

    public WeekDayFilterList() {
    }

//    public String getFilterName() {
//        return filterName;
//    }

    public List<EnumWeekDay> getSelectedWeekDays() {
        return selectedWeekDays;
    }

    public void setSelectedWeekDays(List<EnumWeekDay> selectedWeekDays) {
        this.selectedWeekDays = selectedWeekDays;
    }

//    public void setFilterName(String filterName) {
//        this.filterName = filterName;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekDayFilterList that = (WeekDayFilterList) o;
        return Objects.equals(selectedWeekDays, that.selectedWeekDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedWeekDays);
    }
}
