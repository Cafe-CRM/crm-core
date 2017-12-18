package com.cafe.crm.controllers.receipt;

import com.cafe.crm.exceptions.receipt.ReceiptDataException;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.receipt.ReceiptService;
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
@RequestMapping(value = "/manager/tableReceipt")
public class ReceiptController {

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final ReceiptService receiptService;
	private final TimeManager timeManager;
	private final ShiftService shiftService;
	private final PasswordEncoder encoder;
	private final PropertyService propertyService;
	private final ChecklistService checklistService;

	@Value("${property.name.masterKey}")
	private String masterKeyPropertyName;


	@Autowired
	public ReceiptController(ReceiptService receiptService, TimeManager timeManager, ShiftService shiftService,
							 PasswordEncoder encoder, PropertyService propertyService, ChecklistService checklistService) {
		this.receiptService = receiptService;
		this.timeManager = timeManager;
		this.shiftService = shiftService;
		this.encoder = encoder;
		this.propertyService = propertyService;
		this.checklistService = checklistService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showReceiptPage() {
		LocalDate today = timeManager.getDate();
		LocalDate lastShiftDate = shiftService.getLastShiftDate();
		List<Receipt> receiptList = receiptService.findByDateBetween(lastShiftDate, today);
		Double totalReceiptAmount = getTotalPrice(receiptList);
		ModelAndView modelAndView = new ModelAndView("receipt/receipt");
		modelAndView.addObject("receiptsList", receiptList);
		modelAndView.addObject("totalReceiptAmount", totalReceiptAmount);
		modelAndView.addObject("receiptComment", null);
		modelAndView.addObject("formReceipt", new Receipt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", lastShiftDate);
		modelAndView.addObject("toDate", null);
		modelAndView.addObject("closeChecklist", checklistService.getAllForCloseShift());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView updatePageAfterSearch(@RequestParam(name = "fromDate") String fromDate,
											  @RequestParam(name = "toDate") String toDate,
											  @RequestParam(name = "receiptComment") String receiptComment) {
		LocalDate today = timeManager.getDate();
		List<Receipt> receiptList = filter(receiptComment, fromDate, toDate, today);
		LocalDate from = (fromDate == null || fromDate.isEmpty()) ? null : LocalDate.parse(fromDate, formatter); // убрать
		LocalDate to = (toDate == null || toDate.isEmpty()) ? null : LocalDate.parse(toDate, formatter);
		Double totalReceiptAmount = getTotalPrice(receiptList);
		ModelAndView modelAndView = new ModelAndView("receipt/receipt");
		modelAndView.addObject("receiptsList", receiptList);
		modelAndView.addObject("totalReceiptAmount", totalReceiptAmount);
		modelAndView.addObject("receiptComment", receiptComment);
		modelAndView.addObject("formReceipt", new Receipt());
		modelAndView.addObject("today", today);
		modelAndView.addObject("fromDate", from);
		modelAndView.addObject("toDate", to);

		return modelAndView;
	}

	private List<Receipt> filter(String receiptComment, String fromDate, String toDate, LocalDate today) {
		receiptComment = (receiptComment == null) ? null : receiptComment.trim();
		LocalDate from = (fromDate == null || fromDate.isEmpty())
				? today.minusYears(100) : LocalDate.parse(fromDate, formatter);
		LocalDate to = (toDate == null || toDate.isEmpty())
				? today.plusYears(100) : LocalDate.parse(toDate, formatter);

		if (receiptComment == null || receiptComment.isEmpty()) {
			return receiptService.findByDateBetween(from, to);
		} else {
			return receiptService.findByReceiptCommentAndDateBetween(receiptComment, from, to);
		}

	}

	@RequestMapping(value = "/addReceipt", method = RequestMethod.POST)
	public ResponseEntity<?> saveReceipt(@ModelAttribute @Valid Receipt receipt) {
		receiptService.save(receipt);
		return ResponseEntity.ok("Поступление успешно добавлено!");
	}

	@RequestMapping(value = "/deleteReceiptBoss", method = RequestMethod.POST)
	public ResponseEntity<?> deleteReceiptsBoss(@RequestParam(name = "receiptId") Long id) {

		Receipt receipt = receiptService.get(id);

		if (receipt != null) {
			receiptService.delete(receipt);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}


	@RequestMapping(value = "/deleteReceiptManager", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteReceiptsManager(@RequestParam(name = "receiptId") Long id,
												   @RequestParam(name = "masterKey") String masterKey) {
		Property masterKeyProperty = propertyService.findByName(masterKeyPropertyName);
		String dbMasterKey;

		if (masterKeyProperty != null) {
			dbMasterKey = masterKeyProperty.getValue();
		} else {
			throw new ReceiptDataException("Мастер ключ не настроен");
		}

		if (encoder.matches(masterKey, dbMasterKey)) {
			Receipt receipt = receiptService.get(id);
			receiptService.delete(receipt);
			return ResponseEntity.ok("Поступление успешно удалено");
		} else {
			throw new ReceiptDataException("Введен неверный мастер ключ");
		}

	}

	@ExceptionHandler(value = ReceiptDataException.class)
	public ResponseEntity<?> handleUserUpdateException(ReceiptDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}


	private Double getTotalPrice(List<Receipt> receipts) {
		return receipts
				.stream().mapToDouble(Receipt::getReceiptAmount).sum();
	}
}
