package com.cafe.crm.controllers.rest;

import com.cafe.crm.controllers.report.EnumWeekDay;
import com.cafe.crm.controllers.report.ReportController;
import com.cafe.crm.dto.DateClientAmount;
import com.cafe.crm.dto.SaleProductOnDay;
import com.cafe.crm.dto.WeekDayFilter;
import com.cafe.crm.dto.WeekDayFilterList;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.report.ReportService;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/reports")
public class ReportFilesController {
    private final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final String clientDataName = "clientsData.xlsx";

    private final ReportService reportService;

    @Autowired
    public ReportFilesController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(value = "/createClientsData")
    public ResponseEntity createClientsData(@RequestParam("startDate") String start,
                                            @RequestParam("endDate") String end,
                                            @RequestParam("weekDays[]") String[] weekDays) {
        List<Integer> weekDaysNumer = new ArrayList<>();
        for (String weekDay : weekDays) {
            weekDaysNumer.add(EnumWeekDay.valueOf(weekDay).ordinal());
        }
        reportService.createDateClientData(clientDataName, LocalDate.parse(start), LocalDate.parse(end), weekDaysNumer);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(value = "/getClientsData")
    public ResponseEntity<InputStreamResource> getClientsData() {
        File file = new File(clientDataName);

        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("File not found! ", e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + file.getName())
//                .contentType(MediaType.ALL).contentLength(file.length())
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/getDataForChart")
    public ResponseEntity getDataForChart(@RequestParam("startDate") String start, @RequestParam("endDate") String end,
                                           @RequestParam("weekDays[]") String[] weekDays) {
        List<Integer> weekDaysNumer = new ArrayList<>();
        for (String weekDay : weekDays) {
            weekDaysNumer.add(EnumWeekDay.valueOf(weekDay).ordinal());
        }
        List<DateClientAmount> clientsOnDays = reportService.getClientCount(LocalDate.parse(start), LocalDate.parse(end), weekDaysNumer);
        return ResponseEntity.ok(clientsOnDays);
    }

    @GetMapping("/getDataForChartProducts")
    public ResponseEntity getDataForChartProduct(@RequestParam("startDate") String start, @RequestParam("endDate") String end,
//                                                 @ModelAttribute(value = "weekDaysWrap") WeekDayFilterList weekDaysWrap
                                           @RequestParam("weekDays[]") String[] weekDays,
                                           @RequestParam("products[]") String[] products) {
        String weekDaysTemplate = "";
        for (String weekDay : weekDays) {
            weekDaysTemplate = weekDaysTemplate + (weekDaysTemplate.isEmpty() ? "" : ",") + EnumWeekDay.valueOf(weekDay).ordinal();
        }
        weekDaysTemplate = "(" + weekDaysTemplate + ")";

        String productsTemplate = "";
        for (String product : products) {
            productsTemplate = productsTemplate + (productsTemplate.isEmpty() ? "" : ",") + product;
        }
        productsTemplate = "(" + productsTemplate + ")";

        List<Pair<Product,List<SaleProductOnDay>>> salesProductsOnDays = reportService.getProductOnDays(LocalDate.parse(start), LocalDate.parse(end), weekDaysTemplate, productsTemplate);

        return ResponseEntity.ok(salesProductsOnDays);
    }
}