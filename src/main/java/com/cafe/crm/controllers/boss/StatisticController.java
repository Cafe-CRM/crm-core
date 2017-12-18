package com.cafe.crm.controllers.boss;

import com.cafe.crm.dto.TotalStatisticView;
import com.cafe.crm.exceptions.NoStatData;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/boss/totalStatistics")
public class StatisticController {

    private final ShiftService shiftService;
    private final TimeManager timeManager;
    private final ShiftCalculationService shiftCalculationService;

    @Autowired
    public StatisticController(ShiftService shiftService, TimeManager timeManager,
							   ShiftCalculationService shiftCalculationService) {
        this.shiftService = shiftService;
        this.timeManager = timeManager;
        this.shiftCalculationService = shiftCalculationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showStatisticForAllTimePage() {
        ModelAndView modelAndView = new ModelAndView("totalStatistics");
        LocalDate dateFrom = shiftService.getLastShiftDate();
        LocalDate dateTo = timeManager.getDate();
        TotalStatisticView totalStatisticView = shiftCalculationService.createTotalStatisticView(dateFrom, dateTo);
		Set<Shift> allShiftsBetween = shiftService.findByDates(dateFrom, dateTo);
		double totalCashBox = shiftCalculationService.getTotalCashBox(allShiftsBetween);
		modelAndView.addObject("cashBox", totalCashBox);
		modelAndView.addObject("shifts", allShiftsBetween);
        modelAndView.addObject("from", dateFrom);
        modelAndView.addObject("to", dateTo);
        modelAndView.addObject("totalStat", totalStatisticView);
        return modelAndView;

    }

    @RequestMapping(value = "/searchByDate", method = RequestMethod.POST)
    public ModelAndView searchByDateStat(@RequestParam("from") String from, @RequestParam("to") String to) {
        ModelAndView modelAndView = new ModelAndView("totalStatistics");
        TotalStatisticView totalStatisticView = shiftCalculationService.createTotalStatisticView(LocalDate.parse(from), LocalDate.parse(to));
        Set<Shift> allShiftsBetween = shiftService.findByDates(LocalDate.parse(from), LocalDate.parse(to));
		double totalCashBox = shiftCalculationService.getTotalCashBox(allShiftsBetween);
		modelAndView.addObject("cashBox", totalCashBox);
		modelAndView.addObject("shifts", allShiftsBetween);
        modelAndView.addObject("from", from);
        modelAndView.addObject("to", to);
        modelAndView.addObject("totalStat", totalStatisticView);
        return modelAndView;
    }

	@ExceptionHandler(value = NoStatData.class)
	public String handleUserUpdateException(NoStatData ex) {
		return "totalStatisticsEmpty";
	}
}
