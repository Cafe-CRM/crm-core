package com.cafe.crm.services.impl.report;

import com.cafe.crm.controllers.report.ReportController;
import com.cafe.crm.dto.DateClientAmount;
import com.cafe.crm.dto.HourIntervalClientAmount;
import com.cafe.crm.dto.MenuSale;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.menu.MenuSaleRepository;
import com.cafe.crm.services.interfaces.calculate.TimerOfPauseService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.report.ReportService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final ShiftService shiftService;
    private final TimerOfPauseService timerOfPauseService;
    private final ClientService clientService;
    private final MenuSaleRepository menuSaleRepository;
    private final Logger logger = LoggerFactory.getLogger(ReportController.class);
//    private final EntityManager entityManager;

    @Autowired
    public ReportServiceImpl(ShiftService shiftService, TimerOfPauseService timerOfPauseService, MenuSaleRepository menuSaleRepository, ClientService clientService
//            , EntityManager entityManager
    ) {
        this.shiftService = shiftService;
        this.timerOfPauseService = timerOfPauseService;
        this.menuSaleRepository = menuSaleRepository;
        this.clientService  = clientService;
//        this.entityManager = entityManager;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DateClientAmount> getClientCount(LocalDate startDate, LocalDate endDate) {
        List<DateClientAmount> clientAmounts = new ArrayList<>();
        List<Shift> shifts = shiftService.findByDates(startDate, endDate);
        for (Shift shift : shifts) {
            DateClientAmount dateClientAmount = new DateClientAmount(shift.getShiftDate(), shift.getClients().size());
            clientAmounts.add(dateClientAmount);
        }
        return clientAmounts;
    }

    @Override
    public void createDateClientData(String fileName, LocalDate startDate, LocalDate endDate) {
        String[] columns = {"Дата", "Количество клиентов"};

        List<DateClientAmount> clientAmounts = getClientCount(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ClientAttendance");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        CellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat((short) 14);

        CellStyle cellStyleInt = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyleInt.setDataFormat(dataFormat.getFormat("#"));

        int rowNum = 1;
        for (DateClientAmount clientAmount : clientAmounts) {
            Row row = sheet.createRow(rowNum++);
            Cell cellDate = row.createCell(0);
            cellDate.setCellValue(Date.from(clientAmount.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cellDate.setCellStyle(cellStyleDate);
            Cell cellCount = row.createCell(1);
            cellCount.setCellValue(clientAmount.getClientsNumber());
            cellCount.setCellStyle(cellStyleInt);
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
        }
        catch (FileNotFoundException ex) {
            logger.error("File not found !", ex);
        }
        catch (IOException ex) {
            logger.error("Error by create file report !", ex);
        }
    }

    @Override
    public List<HourIntervalClientAmount> getHourIntervalClientCount(LocalDate startDate, LocalDate endDate, int hourStart, int hourEnd, List<Integer> weekDays) {
        List<HourIntervalClientAmount> hourIntervalClientAmounts = new ArrayList<>();
        if (hourStart<hourEnd) {
            for (int hour = hourStart; hour < hourEnd; hour++) {
                hourIntervalClientAmounts.add(new HourIntervalClientAmount(hour,hour + 1));
            }
        } else {
            for(int hour = hourStart; hour < 24; hour++) {
                hourIntervalClientAmounts.add(new HourIntervalClientAmount(hour,hour + 1));
            }
            for(byte hour = 0; hour < hourEnd; hour++) {
                hourIntervalClientAmounts.add(new HourIntervalClientAmount(hour,hour + 1));
            }
        }


        List<Client> clients = clientService.findByDates(startDate, endDate);
        List<LocalDate> dates = new ArrayList<>();
        for (Client client : clients) {
            if (weekDays.contains(client.getTimeStart().getDayOfWeek().getValue())) {
                if (!dates.contains(client.getTimeStart().toLocalDate())) {
                    dates.add(client.getTimeStart().toLocalDate());
                }
                int hourStartTimer = client.getTimeStart().getHour();

                LocalTime passedTime = client.getPassedTime();
                LocalDateTime clientEndTime = client.getTimeStart().plusHours(passedTime.getHour()).plusMinutes(passedTime.getMinute()).plusSeconds(passedTime.getSecond());
                int hourEndTimer = clientEndTime.getHour();
                if (clientEndTime.getMinute() > 0) {
                    hourEndTimer++;
                }
                for (HourIntervalClientAmount hourInterval : hourIntervalClientAmounts) {
                    int hourIntervalStart = hourInterval.getHourStart();
                    int hourIntervalEnd = hourInterval.getHourEnd();

                    if (hourEndTimer > hourStartTimer) {
                        if (hourIntervalStart >= hourStartTimer && hourIntervalEnd <= hourEndTimer) {
                            hourInterval.setClientsNumber(hourInterval.getClientsNumber() + 1);
                        }
                    } else {
                        if (hourIntervalStart >= hourStartTimer && hourIntervalEnd <= 24 || hourIntervalStart >= 0 && hourIntervalEnd <= hourEndTimer) {
                            hourInterval.setClientsNumber(hourInterval.getClientsNumber() + 1);
                        }
                    }
                }
            }
        }
        if (dates.size() > 0) {
            for (HourIntervalClientAmount interval : hourIntervalClientAmounts) {
                interval.setClientsNumberPerDay(interval.getClientsNumber() / dates.size());
            }
        }

//        List<TimerOfPause> timers = timerOfPauseService.findByDates(startDate, endDate);

//        for (TimerOfPause timer : timers) {
//            if (weekDays.contains(timer.getStartTime().getDayOfWeek().getValue())) {
//                int hourStartTimer = timer.getStartTime().getHour();
//                int hourEndTimer = timer.getEndTime().getHour();
//                if (timer.getEndTime().getMinute() > 0) {
//                    hourEndTimer++;
//                }
//                for (HourIntervalClientAmount hourInterval : hourIntervalClientAmounts) {
//                    int hourIntervalStart = hourInterval.getHourStart();
//                    int hourIntervalEnd = hourInterval.getHourEnd();
//
//                    if (hourEndTimer > hourStartTimer) {
//                        if (hourIntervalStart >= hourStartTimer && hourIntervalEnd <= hourEndTimer) {
//                            hourInterval.setClientsNumber(hourInterval.getClientsNumber() + 1);
//                        }
//                    } else {
//                        if (hourIntervalStart >= hourStartTimer && hourIntervalEnd <= 24 || hourIntervalStart >= 0 && hourIntervalEnd <= hourEndTimer) {
//                            hourInterval.setClientsNumber(hourInterval.getClientsNumber() + 1);
//                        }
//                    }
//                }
//            }
//        }
        return hourIntervalClientAmounts;
    }

    @Override
    public List<MenuSale> getMenuSales(LocalDate startDate, LocalDate endDate, String weekDaysTemplate) {

//        List<MenuSale> menuSales = entityManager.createNamedQuery("getMenuSales", MenuSale.class)
//        .setParameter("startDate", startDate)
//        .setParameter("endDate", endDate)
//        .getResultList();


//        @ColumnResult(name = "productId"),
//        @ColumnResult(name = "productName"),
//        @ColumnResult(name = "price"),
//        @ColumnResult(name = "count"),
//        @ColumnResult(name = "sumSale"),
//        @ColumnResult(name = "costSale"),
//        @ColumnResult(name = "sumPercentStuff"),
//        @ColumnResult(name = "sumProfit")

//        Query query = entityManager.createNativeQuery("select product.id as productId, product.name as productName, " +
//                "ifnull(layer_products.cost,0) as price, sum(1) as count," +
//                "sum(ifnull(layer_products.cost,0)) as sumSale, sum(ifnull(product.self_cost,0)) as costSale," +
//                "(ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100) as sumPercentStuff," +
//                "(sum(ifnull(layer_products.cost,0)) " +
//                "- sum(ifnull(product.self_cost,0)) " +
//                "- (ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)) as sumProfit " +
//                "from (select * from shifts where shift_date >= :startDate and shift_date <= :endDate) as shiftForPeriod " +
//                "left join shifts_clients on shiftForPeriod.id = shifts_clients.shift_id " +
//                "left join client_layer_product on shifts_clients.clients_id = client_layer_product.client_id " +
//                "left join layer_products on client_layer_product.layer_product_id = layer_products.id " +
//                "left join product on layer_products.product_id = product.id " +
//                "left join product_staff_percent on product.id = product_staff_percent.product_id " +
//                "where ifnull(layer_products.cost,0) > 0 " +
//                " group by product.id, product.name, ifnull(layer_products.cost,0)", "MenuSaleMapping");

        Query query = entityManager.createNativeQuery(
                "select product.id as productId, " +
                "product.name as productName, " +
                "ifnull(layer_products.cost,0) as price, " +
                "sum(1) as count, " +
                "sum(ifnull(layer_products.cost,0)) as sumSale, " +
                "sum(ifnull(product.self_cost,0)) as costSale, " +
                "(ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100) as sumPercentStuff, " +
                "(sum(ifnull(layer_products.cost,0)) - sum(ifnull(product.self_cost,0)) - (ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)) as sumProfit " +
                "from (select *  " +
                "from (select client_layer_product.layer_product_id as layer_product_id from " +
                "(select * from shifts where shift_date >= :startDate and shift_date <= :endDate and weekday(shifts.shift_date) in " + weekDaysTemplate + ") as shiftForPeriod " +
                "left join shifts_clients on shiftForPeriod.id = shifts_clients.shift_id " +
                "left join client_layer_product on shifts_clients.clients_id = client_layer_product.client_id " +
                "group by client_layer_product.layer_product_id) as layer_products_id " +
                "where layer_product_id is not null) as layer_product_ids " +
                "left join layer_products on layer_product_ids.layer_product_id = layer_products.id " +
                "left join product on layer_products.product_id = product.id " +
                "left join product_and_categories on product_and_categories.product_id = product.id " +
                "left join category on product_and_categories.category_id = category.id " +
                "left join product_staff_percent on product.id = product_staff_percent.product_id " +
                "where ifnull(layer_products.cost,0) > 0 and category.dirty_profit <> 0 and category.floating_price <> 1 " +
                "group by product.id, product.name, ifnull(layer_products.cost,0)", "MenuSaleMapping");

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
//        query.setParameter("weekDaysTemplate", weekDaysTemplate);

//        List<MenuSale> menuSales = query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(MenuSale.class)).list();

//        List<MenuSale> menuSales = new ArrayList<>();
        List<MenuSale> menuSales = query.getResultList();
//        List<MenuSale> menuSales = (List<MenuSale>) query.getResultList();

//        List<MenuSale> findMenuSalesByDatesAndCompanyId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


//        return menuSaleRepository.findMenuSalesByDatesAndCompanyId(startDate, endDate);
        return menuSales;
    }

    @Override
    public MenuSale getMenuSalesTotal(List<MenuSale> menuSales) {
        MenuSale menuSalesTotal = new MenuSale(0L, "Итого:",0,0,0,0,0,0);

        for (MenuSale menuSale : menuSales) {
            menuSalesTotal.setCount(menuSalesTotal.getCount() + menuSale.getCount());
            menuSalesTotal.setSumSale(menuSalesTotal.getSumSale() + menuSale.getSumSale());
            menuSalesTotal.setCostSale(menuSalesTotal.getCostSale() + menuSale.getCostSale());
            menuSalesTotal.setSumProfit(menuSalesTotal.getSumProfit() + menuSale.getSumProfit());
        }
        return menuSalesTotal;
    }

    //    List<PostDTO> postDTOs = entityManager.createQuery(
//            "select new " +
//                    "   com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.jpa.PostDTO(" +
//                    "       p.id, " +
//                    "       p.title " +
//                    "   ) " +
//                    "from Post p " +
//                    "where p.createdOn > :fromTimestamp", PostDTO.class)
//            .setParameter( "fromTimestamp", Timestamp.from(
//                    LocalDateTime.of( 2016, 1, 1, 0, 0, 0 )
//                            .toInstant( ZoneOffset.UTC ) ))
//            .getResultList();
}
