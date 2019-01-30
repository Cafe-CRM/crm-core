package com.cafe.crm.dto;

import com.cafe.crm.utils.EnumWeekDay;

import java.util.List;
import java.util.Objects;

public class WeekDayList {
    private List<EnumWeekDay> selectedWeekDays;

    public WeekDayList() {
    }

    public List<EnumWeekDay> getSelectedWeekDays() {
        return selectedWeekDays;
    }

    public void setSelectedWeekDays(List<EnumWeekDay> selectedWeekDays) {
        this.selectedWeekDays = selectedWeekDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekDayList that = (WeekDayList) o;
        return Objects.equals(selectedWeekDays, that.selectedWeekDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedWeekDays);
    }
}
