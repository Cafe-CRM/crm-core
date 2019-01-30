package com.cafe.crm.controllers.report;

import com.cafe.crm.dto.*;
import com.cafe.crm.services.interfaces.report.ReportService;
import com.cafe.crm.utils.EnumWeekDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(value = "/attendance/clientsondays", method = RequestMethod.GET)
    public ModelAndView getClientsOnDays() {
        ModelAndView mv = new ModelAndView("report/attendanceOnDays");
        WeekDayList weekDaysWrap = new WeekDayList();
        weekDaysWrap.setSelectedWeekDays(Arrays.asList(EnumWeekDay.values()));
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", weekDaysWrap.getSelectedWeekDays());
        return mv;
    }

    @RequestMapping(value = "/attendance/clientsondays", method = RequestMethod.POST)
    public ModelAndView showClientsOnDays(@Param("start") String start, @Param("end") String end,
                                          @ModelAttribute(value = "weekDaysWrap") WeekDayList weekDaysWrap) {
        List<Integer> weekDays = new ArrayList<>();
        for (EnumWeekDay weekDay : weekDaysWrap.getSelectedWeekDays()) {
            weekDays.add(weekDay.ordinal());
        }

        ModelAndView mv = new ModelAndView("report/attendanceOnDays");
        List<DateClientAmount> clientsOnDays = reportService.getClientCount(LocalDate.parse(start), LocalDate.parse(end), weekDays);
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", Arrays.asList(EnumWeekDay.values()));
        mv.addObject("clientsOnDays", clientsOnDays);
        mv.addObject("start", start);
        mv.addObject("end", end);
        return mv;
    }

    @GetMapping(value = "/getAttendanceDaysData")
    public ResponseEntity<InputStreamResource> getAttendanceDaysData() {
        String path = "DownloadData" + File.separator;
        File file = new File(path + "report.css");

        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("File not found! ", e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + file.getName())
                .contentType(MediaType.TEXT_PLAIN).contentLength(file.length())
                .body(resource);
    }


    @RequestMapping(value = "/attendance/clientsonhours", method = RequestMethod.GET)
    public @ModelAttribute("weekDays")
    ModelAndView getClientsOnHours() {
        ModelAndView mv = new ModelAndView("report/attendanceOnHours");
        WeekDayList weekDaysWrap = new WeekDayList();
        weekDaysWrap.setSelectedWeekDays(Arrays.asList(EnumWeekDay.values()));
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", weekDaysWrap.getSelectedWeekDays());
        return mv;
    }

    @RequestMapping(value = "/attendance/clientsonhours", method = RequestMethod.POST)
    public ModelAndView showClientsOnHours(@Param("start") String start, @Param("end") String end,
                                           @Param("fromHour") String fromHour, @Param("toHour") String toHour,
                                           @ModelAttribute(value = "weekDaysWrap") WeekDayList weekDaysWrap) {
        List<Integer> weekDays = new ArrayList<>();
        for (EnumWeekDay weekDay : weekDaysWrap.getSelectedWeekDays()) {
            weekDays.add(weekDay.ordinal());
        }
        ModelAndView mv = new ModelAndView("report/attendanceOnHours");
        List<HourIntervalClientAmount> clientsOnHours = reportService.getHourIntervalClientCount(LocalDate.parse(start), LocalDate.parse(end), Integer.parseInt(fromHour), Integer.parseInt(toHour), weekDays);
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", Arrays.asList(EnumWeekDay.values()));
        mv.addObject("clientsOnHours", clientsOnHours);
        mv.addObject("start", start);
        mv.addObject("end", end);
        mv.addObject("fromHour", fromHour);
        mv.addObject("toHour", toHour);
        return mv;
    }

    @RequestMapping(value = "/menusales", method = RequestMethod.GET)
    public ModelAndView getMenuSales() {
        ModelAndView mv = new ModelAndView("report/salesOnMenu");
        WeekDayList weekDaysWrap = new WeekDayList();
        weekDaysWrap.setSelectedWeekDays(Arrays.asList(EnumWeekDay.values()));
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", weekDaysWrap.getSelectedWeekDays());
        return mv;
    }

    @RequestMapping(value = "/menusales", method = RequestMethod.POST)
    public ModelAndView showMenuSales(@Param("start") String start, @Param("end") String end,
                                      @ModelAttribute(value = "weekDaysWrap") WeekDayList weekDaysWrap) {
        StringBuilder weekDaysTemplate = new StringBuilder();
        for (EnumWeekDay weekDay : weekDaysWrap.getSelectedWeekDays()) {
            weekDaysTemplate.append((weekDaysTemplate.length() == 0) ? "" : ",").append(weekDay.ordinal());
        }
        weekDaysTemplate = new StringBuilder("(" + weekDaysTemplate + ")");
        ModelAndView mv = new ModelAndView("report/salesOnMenu");
        List<MenuSale> menuSales = reportService.getMenuSales(LocalDate.parse(start), LocalDate.parse(end), weekDaysTemplate.toString());
        MenuSale menuSaleTotal = reportService.getMenuSalesTotal(menuSales);
        mv.addObject("weekDaysWrap", weekDaysWrap);
        mv.addObject("weekDays", Arrays.asList(EnumWeekDay.values()));
        mv.addObject("menuSales", menuSales);
        mv.addObject("menuSalesTotal", menuSaleTotal);
        mv.addObject("start", start);
        mv.addObject("end", end);
        return mv;
    }
}

