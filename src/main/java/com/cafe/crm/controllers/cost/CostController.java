package com.cafe.crm.controllers.cost;

import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@Controller
@RequestMapping(value = "/manager/cost")
public class CostController {

	private final TimeManager timeManager;
	private final CostService costService;
	private final CostCategoryService costCategoryService;
	private final ShiftService shiftService;
	private final ChecklistService checklistService;

	@Autowired
	public CostController(CostService costService, CostCategoryService costCategoryService, TimeManager timeManager, ShiftService shiftService, ChecklistService checklistService) {
		this.costService = costService;
		this.costCategoryService = costCategoryService;
		this.timeManager = timeManager;
		this.shiftService = shiftService;
		this.checklistService = checklistService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showCostsPage(Model model) {
		LocalDate today = timeManager.getDate();
		LocalDate lastShiftDate = shiftService.getLastShiftDate();
		List<Cost> costs = costService.findByDateBetween(lastShiftDate, today);
		List<CostCategory> costCategories = costCategoryService.findAll();
		Double totalPrice = getTotalPrice(costs);

		model.addAttribute("costs", costs);
		model.addAttribute("costCategories", costCategories);
		model.addAttribute("categoryName", null);
		model.addAttribute("costName", null);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("formCost", new Cost());
		model.addAttribute("today", today);
		model.addAttribute("fromDate", lastShiftDate);
		model.addAttribute("toDate", null);
		model.addAttribute("closeChecklist", checklistService.getAllForCloseShift());

		return "costs/costs";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String showCostsPageWithParameters(@RequestParam(name = "fromDate") String fromDate,
											  @RequestParam(name = "toDate") String toDate,
											  @RequestParam(name = "costName") String costName,
											  @RequestParam(name = "categoryName") String categoryName,
											  Model model) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		List<Cost> costs = getCosts(costName, categoryName, fromDate, toDate, formatter);
		List<CostCategory> costCategories = costCategoryService.findAll();
		double totalPrice = getTotalPrice(costs);

		LocalDate from = isBlank(fromDate) ? null : LocalDate.parse(fromDate, formatter);
		LocalDate to = isBlank(toDate) ? null : LocalDate.parse(toDate, formatter);

		model.addAttribute("costs", costs);
		model.addAttribute("costCategories", costCategories);
		model.addAttribute("costName", costName);
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("formCost", new Cost());
		model.addAttribute("fromDate", from);
		model.addAttribute("toDate", to);

		return "costs/costs";
	}

	private double getTotalPrice(List<Cost> costs) {
		double totalPrice = 0d;
		for (Cost cost : costs) {
			totalPrice += cost.getPrice() * cost.getQuantity();
		}
		return totalPrice;
	}

	private List<Cost> getCosts(String costName, String categoryName, String fromDate, String toDate, DateTimeFormatter formatter) {
		costName = (costName == null) ? null : costName.trim();
		categoryName = (categoryName == null) || categoryName.equals("Все категории") ? null : categoryName.trim();

		LocalDate today = timeManager.getDate();
		LocalDate from = (isBlank(fromDate))
				? today.minusYears(100) : LocalDate.parse(fromDate, formatter);
		LocalDate to = (isBlank(toDate))
				? today.plusYears(100) : LocalDate.parse(toDate, formatter);

		if (isBlank(costName) && isBlank(categoryName)) {
			return costService.findByDateBetween(from, to);
		} else if (isBlank(costName)) {
			return costService.findByCategoryNameAndDateBetween(categoryName, from, to);
		} else if (isBlank(categoryName)) {
			return costService.findByNameAndDateBetween(costName, from, to);
		}
		return costService.findByNameAndCategoryNameAndDateBetween(costName, categoryName, from, to);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> add(@ModelAttribute @Valid Cost cost, BindingResult result) {
		if (result.hasErrors()) {
			String fieldError = result.getFieldError().getDefaultMessage();
			return ResponseEntity.badRequest().body("Не удалось добавить товар!\n" + fieldError);
		}
		costService.save(cost);

		return ResponseEntity.ok("Товар успешно добавлен!");
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> update(@ModelAttribute @Valid Cost cost, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body("Не удалось изменить товар!");
		}
		costService.update(cost);

		return ResponseEntity.ok("Товар успешно изменен!");
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> delete(@RequestParam(name = "costId") Long id) {
		costService.delete(id);

		return ResponseEntity.ok("Расход успешно удален!");
	}

	@RequestMapping(value = "/delete/all", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteAll(@RequestParam(name = "ids") String ids) {
		String[] strIds = ids.replace("[", "").replace("]", "").replace("\"", "").split(",");
		try {
			long[] longIds = Arrays.stream(strIds).mapToLong(Long::parseLong).toArray();
			costService.offVisibleStatus(longIds);
		} catch (NumberFormatException ex) {
			return ResponseEntity.badRequest().body("Не удалось удалить товары!");
		}

		return ResponseEntity.ok("Товары успешно удалены!");
	}

	@RequestMapping(value = "/search/category", method = RequestMethod.GET)
	@ResponseBody
	public String[] getCategoryStartWith(@RequestParam(name = "name") String startName) {
		List<CostCategory> categories = costCategoryService.findByNameStartingWith(startName);

		return categories.stream().map(CostCategory::getName).toArray(String[]::new);
	}

	@RequestMapping(value = "/search/cost", method = RequestMethod.GET)
	@ResponseBody
	public String[] getCostStartWith(@RequestParam(name = "name") String startName) {
		Set<Cost> costs = costService.findByNameStartingWith(startName);

		return costs.stream().map(Cost::getName).toArray(String[]::new);
	}

}
