package com.cafe.crm.controllers.report;

public enum EnumWeekDay {
    Monday("Понедельник"),
    Tuesday("Вторник"),
    Wednesday("Среда"),
    Thursday("Четверг"),
    Friday("Пятница"),
    Saturday("Суббота"),
    Sunday("Воскресенье");

    private String description;

    private EnumWeekDay(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}