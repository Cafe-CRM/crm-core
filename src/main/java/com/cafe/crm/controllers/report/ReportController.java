package com.cafe.crm.controllers.report;

import com.cafe.crm.dto.*;
import com.cafe.crm.services.interfaces.report.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
        return mv;
    }

    @RequestMapping(value = "/attendance/clientsondays", method = RequestMethod.POST)
    public ModelAndView showClientsOnDays(@Param("start") String start, @Param("end") String end) {
        ModelAndView mv = new ModelAndView("report/attendanceOnDays");
        List<DateClientAmount> clientsOnDays = reportService.getClientCount(LocalDate.parse(start), LocalDate.parse(end));
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
        List<WeekDayFilter> weekDays = new ArrayList<>();
//        weekDays.add(new WeekDayFilter(1, "Понедельник", true));
//        weekDays.add(new WeekDayFilter(2, "Вторник", true));
//        weekDays.add(new WeekDayFilter(3, "Среда", true));
//        weekDays.add(new WeekDayFilter(4, "Четверг", true));
//        weekDays.add(new WeekDayFilter(5, "Пятница", true));
//        weekDays.add(new WeekDayFilter(6, "Суббота", true));
//        weekDays.add(new WeekDayFilter(7, "Воскресенье", true));
//
//        mv.addObject("weekDays", weekDays);
//        mv.addObject("weekDays",weekDays);
        return mv;
    }

    @RequestMapping(value = "/attendance/clientsonhours", method = RequestMethod.POST)
    public ModelAndView showClientsOnHours(
//                                            @RequestParam(value = "endd") String endParam,
                                            @Param("start") String start, @Param("end") String end,
                                            @Param("fromHour") String fromHour, @Param("toHour") String toHour,
//                                            @ModelAttribute(value = "form") WeekDayFilterList weekDaysModel
                                            @Param("weekday1") Byte weekday1,
                                            @Param("weekday2") Byte weekday2,
                                            @Param("weekday3") Byte weekday3,
                                            @Param("weekday4") Byte weekday4,
                                            @Param("weekday5") Byte weekday5,
                                            @Param("weekday6") Byte weekday6,
                                            @Param("weekday7") Byte weekday7
    ) {
        List<Integer> weekDays = new ArrayList<>();
        if (weekday1 != null) {
            weekDays.add(1);
        }
        if (weekday2 != null) {
            weekDays.add(2);
        }
        if (weekday3 != null) {
            weekDays.add(3);
        }
        if (weekday4 != null) {
            weekDays.add(4);
        }
        if (weekday5 != null) {
            weekDays.add(5);
        }
        if (weekday6 != null) {
            weekDays.add(6);
        }
        if (weekday7 != null) {
            weekDays.add(7);
        }

        ModelAndView mv = new ModelAndView("report/attendanceOnHours");
        List<HourIntervalClientAmount> clientsOnHours = reportService.getHourIntervalClientCount(LocalDate.parse(start), LocalDate.parse(end), Integer.parseInt(fromHour), Integer.parseInt(toHour), weekDays);
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
        return mv;
    }

    @RequestMapping(value = "/menusales", method = RequestMethod.POST)
    public ModelAndView showMenuSales(
            @Param("start") String start,
            @Param("end") String end,
            @Param("weekday1") Byte weekday1,
            @Param("weekday2") Byte weekday2,
            @Param("weekday3") Byte weekday3,
            @Param("weekday4") Byte weekday4,
            @Param("weekday5") Byte weekday5,
            @Param("weekday6") Byte weekday6,
            @Param("weekday7") Byte weekday7
    ) {

        String weekDaysTemplate = "";
        if (weekday1 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "0" : ",0");
        }
        if (weekday2 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "1" : ",1");
        }
        if (weekday3 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "2" : ",2");
        }
        if (weekday4 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "3" : ",3");
        }
        if (weekday5 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "4" : ",4");
        }
        if (weekday6 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "5" : ",5");
        }
        if (weekday7 != null) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "6" : ",6");
        }
        weekDaysTemplate = "(" + weekDaysTemplate + ")";


        ModelAndView mv = new ModelAndView("report/salesOnMenu");
        List<MenuSale> menuSales = reportService.getMenuSales(LocalDate.parse(start), LocalDate.parse(end), weekDaysTemplate);
        MenuSale menuSaleTotal = reportService.getMenuSalesTotal(menuSales);
        mv.addObject("menuSales", menuSales);
        mv.addObject("menuSalesTotal", menuSaleTotal);
        mv.addObject("start", start);
        mv.addObject("end", end);
        return mv;
    }
}