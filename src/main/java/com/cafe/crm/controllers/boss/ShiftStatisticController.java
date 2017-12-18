package com.cafe.crm.controllers.boss;

import com.cafe.crm.dto.DetailStatisticView;
import com.cafe.crm.dto.ShiftView;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.impl.calculation.ShiftCalculationServiceImpl;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/boss/statistics")
public class ShiftStatisticController {

	private final ShiftService shiftService;
	private final CostService costService;
	private final ShiftCalculationService shiftCalculationService;


	@Autowired
	public ShiftStatisticController(ShiftService shiftService, CostService costService, ShiftCalculationService shiftCalculationService) {
		this.shiftService = shiftService;
		this.costService = costService;
		this.shiftCalculationService = shiftCalculationService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getAdminPage() {
		ModelAndView mv = new ModelAndView("shift/shiftStatistics");
		List<Shift> allShifts = shiftService.findAll();
		mv.addObject("shifts", allShifts);
		mv.addObject("date", shiftService.getLastShiftDate());
		return mv;
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView search(@Param("fromDate") String start, @Param("toDate") String end) {
		ModelAndView mv = new ModelAndView("shift/shiftStatistics");
		Set<Shift> dates = shiftService.findByDates(LocalDate.parse(start), LocalDate.parse(end));
		mv.addObject("shifts", dates);
		mv.addObject("date", start);
		return mv;
	}

	@RequestMapping(value = "/search/shiftDetail/{id}", method = RequestMethod.GET)
	public ModelAndView shiftDetail(@PathVariable("id") Long id) throws IOException {
		ModelAndView mv = new ModelAndView("shift/shiftDetail");
		Shift shift = shiftService.findOne(id);
		DetailStatisticView detailStatisticView = shiftCalculationService.createDetailStatisticView(shift);
		mv.addObject("detailStatisticView", detailStatisticView);
		return mv;
	}
}
