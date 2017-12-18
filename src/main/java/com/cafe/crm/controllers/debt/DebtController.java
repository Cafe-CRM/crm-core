package com.cafe.crm.controllers.debt;

import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping(value = "/manager/tableDebt")
public class DebtController {

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final DebtService debtService;
	private final TimeManager timeManager;
	private final ShiftService shiftService;
	private final ChecklistService checklistService;
	private final PasswordEncoder encoder;
	private final PropertyService propertyService;

	@Value("${property.name.masterKey}")
	private String masterKeyPropertyName;

	@Autowired
	public DebtController(DebtService debtService, TimeManager timeManager, ShiftService shiftService, ChecklistService checklistService, PasswordEncoder encoder, PropertyService propertyService) {
		this.debtService = debtService;
		this.timeManager = timeManager;
		this.shiftService = shiftService;
		this.checklistService = checklistService;
		this.encoder = encoder;
		this.propertyService = propertyService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showDebtPage() {
		LocalDate today = timeManager.getDate();
		List<Debt> debtList = debtService.findByVisibleIsTrueAndDateBetween(today, today.plusYears(100));
		Double totalDebtAmount = getTotalPrice(debtList);

		ModelAndView modelAndView = new ModelAndView("debt/debt");
		modelAndView.addObject("debtsList", debtList);
		modelAndView.addObject("totalDebtAmount", totalDebtAmount);
		modelAndView.addObject("debtorName", null);
		modelAndView.addObject("formDebt", new Debt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", shiftService.getLastShiftDate());
		modelAndView.addObject("toDate", null);
		modelAndView.addObject("closeChecklist", checklistService.getAllForCloseShift());

		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView updatePageAfterSearch(@RequestParam(name = "fromDate") String fromDate,
											  @RequestParam(name = "toDate") String toDate,
											  @RequestParam(name = "debtorName") String debtorName) {
		LocalDate today = timeManager.getDate();
		List<Debt> debtList = filter(debtorName, fromDate, toDate);
		Double totalDebtAmount = getTotalPrice(debtList);

		LocalDate from = (fromDate == null || fromDate.isEmpty()) ? null : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty()) ? null : LocalDate.parse(toDate, formatter);

		ModelAndView modelAndView = new ModelAndView("debt/debt");
		modelAndView.addObject("debtsList", debtList);
		modelAndView.addObject("totalDebtAmount", totalDebtAmount);
		modelAndView.addObject("debtorName", debtorName);
		modelAndView.addObject("formDebt", new Debt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", from);
		modelAndView.addObject("toDate", to);

		return modelAndView;
	}

	private List<Debt> filter(String debtorName, String fromDate, String toDate) {
		debtorName = (debtorName == null) ? null : debtorName.trim();

		LocalDate today = timeManager.getDate();
		LocalDate from = (fromDate == null || fromDate.isEmpty())
				? today.minusYears(100) : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty())
				? today.plusYears(100) : LocalDate.parse(toDate, formatter);

		if (debtorName == null || debtorName.isEmpty()) {
			return debtService.findByVisibleIsTrueAndDateBetween(from, to);
		} else {
			return debtService.findByDebtorAndDateBetween(debtorName, from, to);
		}

	}

	private Double getTotalPrice(List<Debt> goodsList) {
		return goodsList
				.stream().mapToDouble(Debt::getDebtAmount).sum();
	}

	@RequestMapping(value = "/addDebt", method = RequestMethod.POST)
	public ResponseEntity<?> saveGoods(@ModelAttribute @Valid Debt debt) {
		debt.setShift(shiftService.getLast());

		debtService.save(debt);

		return ResponseEntity.ok("Долг успешно добавлен!");
	}

	@RequestMapping(value = "/repay", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> repayDebts(@RequestParam(name = "debtId") Long id) {

		debtService.repayDebt(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteBoss", method = RequestMethod.POST)
	public ResponseEntity<?> deleteDebtsBoss(@RequestParam(name = "debtId") Long id) {

		Debt debt = debtService.get(id);

		if (debt != null) {
			debtService.delete(debt);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/deleteManager", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteDebtsManager(@RequestParam(name = "debtId") Long id,
												@RequestParam(name = "masterKey") String masterKey) {
		Debt debt = debtService.get(id);
		Property masterKeyProperty = propertyService.findByName(masterKeyPropertyName);
		String dbMasterKey;

		if (masterKeyProperty != null) {
			dbMasterKey = masterKeyProperty.getValue();
		} else {
			throw new DebtDataException("Мастер ключ не настроен");
		}

		if (debt != null && encoder.matches(masterKey, dbMasterKey)) {
			debtService.delete(debt);
			return ResponseEntity.ok("Долг успешно удален");
		} else {
			throw new DebtDataException("Введен неверный мастер ключ");
		}

	}

	@ExceptionHandler(value = DebtDataException.class)
	public ResponseEntity<?> handleUserUpdateException(DebtDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

}
