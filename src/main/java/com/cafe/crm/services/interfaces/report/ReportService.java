package com.cafe.crm.services.interfaces.report;

import com.cafe.crm.dto.DateClientAmount;
import com.cafe.crm.dto.HourIntervalClientAmount;
import com.cafe.crm.dto.MenuSale;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    void
    createDateClientData(String fileName, LocalDate startDate, LocalDate endDate);
    List<DateClientAmount> getClientCount(LocalDate startDate, LocalDate endDate);
    List<HourIntervalClientAmount> getHourIntervalClientCount(LocalDate startDate, LocalDate endDate, int hourStart, int hourEnd, List<Integer> weekDays);
    List<MenuSale> getMenuSales(LocalDate startDate, LocalDate endDate, String weekDaysTemplate);
    MenuSale getMenuSalesTotal(List<MenuSale> menuSales);
}
