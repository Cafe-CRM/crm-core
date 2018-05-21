package com.cafe.crm.controllers.debt;

import com.cafe.crm.dto.DebtDTO;
import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.Target;
import com.cafe.crm.utils.TimeManager;
import com.yc.easytransformer.Transformer;
import org.slf4j.LoggerFactory;
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
	private final VkService vkService;
	private final ConfirmTokenService confirmTokenService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(DebtController.class);

	@Autowired
	public DebtController(DebtService debtService, TimeManager timeManager, ShiftService shiftService, ChecklistService checklistService,
						  VkService vkService, ConfirmTokenService confirmTokenService) {
		this.debtService = debtService;
		this.timeManager = timeManager;
		this.shiftService = shiftService;
		this.checklistService = checklistService;
		this.vkService = vkService;
		this.confirmTokenService = confirmTokenService;
	}

	@RequestMapping(value = "/other-debt", method = RequestMethod.GET)
	public ModelAndView showOtherDebts() {
		LocalDate today = timeManager.getDate();
		LocalDate lastShiftDate = shiftService.getLastShiftDate();
		List<Debt> debtList = debtService.findOtherDebtByVisibleIsTrueAndDateBetween(lastShiftDate, today.plusYears(100));

		ModelAndView modelAndView = getDebtPage(debtList, today, lastShiftDate);
		modelAndView.addObject("isCashBoxDebt", false);

		return modelAndView;
	}

	@RequestMapping(value = "/cash-box-debt", method = RequestMethod.GET)
	public ModelAndView showCashBoxDebts() {
		LocalDate today = timeManager.getDate();
		LocalDate lastShiftDate = shiftService.getLastShiftDate();
		List<Debt> debtList = debtService.findCashBoxDebtByVisibleIsTrueAndDateBetween(lastShiftDate, today.plusYears(100));

		ModelAndView modelAndView = getDebtPage(debtList, today, lastShiftDate);
		modelAndView.addObject("isCashBoxDebt", true);

		return modelAndView;
	}

	private ModelAndView getDebtPage(List<Debt> debtList, LocalDate today, LocalDate lastShiftDate) {
		Double totalDebtAmount = getTotalPrice(debtList);

		ModelAndView modelAndView = new ModelAndView("debt/debt");
		modelAndView.addObject("debtsList", debtList);
		modelAndView.addObject("totalDebtAmount", totalDebtAmount);
		modelAndView.addObject("debtorName", null);
		modelAndView.addObject("formDebt", new Debt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", lastShiftDate);
		modelAndView.addObject("toDate", null);
		modelAndView.addObject("closeChecklist", checklistService.getAllForCloseShift());

		return modelAndView;
	}

	@RequestMapping(value = "/find-other-debt", method = RequestMethod.POST)
	public ModelAndView updateOtherPageAfterSearch(@RequestParam(name = "fromDate") String fromDate,
											  @RequestParam(name = "toDate") String toDate,
											  @RequestParam(name = "debtorName") String debtorName) {
		List<Debt> debtList = otherDebtFilter(debtorName, fromDate, toDate);


		return getSearchDebtModel(fromDate, toDate, debtorName, debtList);
	}

	@RequestMapping(value = "/find-cash-box-debt", method = RequestMethod.POST)
	public ModelAndView updateCashBoxPageAfterSearch(@RequestParam(name = "fromDate") String fromDate,
											  @RequestParam(name = "toDate") String toDate,
											  @RequestParam(name = "debtorName") String debtorName) {

		List<Debt> debtList = cashBoxDebtFilter(debtorName, fromDate, toDate);


		return getSearchDebtModel(fromDate, toDate, debtorName, debtList);
	}

	private ModelAndView getSearchDebtModel(String fromDate, String toDate, String debtorName, List<Debt> debtList) {
		LocalDate today = timeManager.getDate();
		Double totalDebtAmount = getTotalPrice(debtList);

		LocalDate from = (fromDate == null || fromDate.isEmpty()) ? null : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty()) ? null : LocalDate.parse(toDate, formatter);

		List<DebtDTO> debtDTOS = debtService.transformDebtsWithOutShiftAndCalc(debtList);

		ModelAndView modelAndView = new ModelAndView("debt/debt");
		modelAndView.addObject("debtsList", debtDTOS);
		modelAndView.addObject("totalDebtAmount", totalDebtAmount);
		modelAndView.addObject("debtorName", debtorName);
		modelAndView.addObject("formDebt", new Debt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", from);
		modelAndView.addObject("toDate", to);

		return modelAndView;
	}

	private List<Debt> otherDebtFilter(String debtorName, String fromDate, String toDate) {
		debtorName = (debtorName == null) ? null : debtorName.trim();

		LocalDate today = timeManager.getDate();
		LocalDate from = (fromDate == null || fromDate.isEmpty())
				? today.minusYears(100) : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty())
				? today.plusYears(100) : LocalDate.parse(toDate, formatter);

		if (debtorName == null || debtorName.isEmpty()) {
			return debtService.findOtherDebtByVisibleIsTrueAndDateBetween(from, to);
		} else {
			return debtService.findOtherDebtByDebtorAndDateBetween(debtorName, from, to);
		}

	}

	private List<Debt> cashBoxDebtFilter(String debtorName, String fromDate, String toDate) {
		debtorName = (debtorName == null) ? null : debtorName.trim();

		LocalDate today = timeManager.getDate();
		LocalDate from = (fromDate == null || fromDate.isEmpty())
				? today.minusYears(100) : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty())
				? today.plusYears(100) : LocalDate.parse(toDate, formatter);

		if (debtorName == null || debtorName.isEmpty()) {
			return debtService.findCashBoxDebtByVisibleIsTrueAndDateBetween(from, to);
		} else {
			return debtService.findCashBoxDebtByDebtorAndDateBetween(debtorName, from, to);
		}

	}

	private Double getTotalPrice(List<Debt> goodsList) {
		return goodsList
				.stream().mapToDouble(Debt::getDebtAmount).sum();
	}

	@RequestMapping(value = "/add-debt", method = RequestMethod.POST)
	public ResponseEntity<?> addOtherDebt(@ModelAttribute @Valid Debt debt) {
		Debt savedDebt = debtService.addDebtOnLastShift(debt);
		Calculate calculate = savedDebt.getCalculate();

		StringBuilder addDebtMessage = new StringBuilder("Долг с названием: \"" + savedDebt.getDebtor() + "\", суммой: "
                + savedDebt.getDebtAmount() + " был добавлен " + savedDebt.getDate());

		if (calculate != null) {
		    addDebtMessage.append("\nСчёт долга: \n")
                    .append("id: ")
                    .append(calculate.getId())
                    .append(", описание: ")
                    .append(calculate.getDescription());
        }

        logger.info(addDebtMessage.toString());

		return ResponseEntity.ok("Долг успешно добавлен!");
	}

	@RequestMapping(value = "/repay", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> repayDebts(@RequestParam(name = "debtId") Long id) {
		Debt debt = debtService.repayDebt(id);

        logger.info("Долг \"" + debt.getDebtor() + "\" за " + debt.getDate() + " суммой: " + debt.getDebtAmount() +
                " был возвращён");

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/delete-debt", method = RequestMethod.POST)
	public ResponseEntity<?> deleteDebtsBoss(@RequestParam(name = "password") String password,
											 @RequestParam(name = "debtId") Long id) {

		if (password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}
		if (!confirmTokenService.confirm(password, Target.DELETE_DEBT)) {
			throw new PasswordException("Пароль не действителен!");
		}

		Debt debt = debtService.get(id);

		if (debt != null) {
			debtService.delete(debt);
			logger.info("Удаление долга \"" + debt.getDebtor() + "\" за " + debt.getDate() + " суммой: " + debt.getDebtAmount());
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/send-delete-debt-pass", method = RequestMethod.POST)
	public ResponseEntity sendDeleteDebtPass(@RequestParam(name = "debtId") Long debtId) {
		Debt debt = debtService.get(debtId);

		if (debt == null) {
			throw new DebtDataException("Выбран несуществующий стол!");
		}

		String prefix = "Одноразовый пароль для подтверждения удаления долга \"" + debt.getDebtor() +
				"\" суммой: " + debt.getDebtAmount() + " : ";
		vkService.sendConfirmToken(prefix, Target.DELETE_DEBT);
		return ResponseEntity.ok("Пароль послан");
	}

	@ExceptionHandler(value = DebtDataException.class)
	public ResponseEntity<?> handleUserUpdateException(DebtDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = PasswordException.class)
	public ResponseEntity<?> handleTransferException(PasswordException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

}
