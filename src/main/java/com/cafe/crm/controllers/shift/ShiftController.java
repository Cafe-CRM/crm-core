package com.cafe.crm.controllers.shift;


import com.cafe.crm.dto.CalculateDTO;
import com.cafe.crm.dto.ShiftCloseDTO;
import com.cafe.crm.dto.ShiftView;
import com.cafe.crm.exceptions.transferDataException.TransferException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.email.EmailService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping(path = "/manager")
public class ShiftController {

    private static final String EMAIL_RECIPIENT_ROLE_IN_CASE_SHORTAGE = "BOSS";
    private final ShiftService shiftService;
    private final UserService userService;
    private final TimeManager timeManager;
    private final EmailService emailService;
    private final VkService vkService;
    private final ChecklistService checklistService;
    private final ShiftCalculationService shiftCalculationService;

    @Autowired
    public ShiftController(ShiftService shiftService, TimeManager timeManager, EmailService emailService,
						   VkService vkService, UserService userService, ChecklistService checklistService,
						   ShiftCalculationService shiftCalculationService) {
        this.shiftService = shiftService;
        this.timeManager = timeManager;
        this.emailService = emailService;
        this.vkService = vkService;
        this.userService = userService;
        this.checklistService = checklistService;
        this.shiftCalculationService = shiftCalculationService;
    }

    @Transactional
    @RequestMapping(value = "/shift/", method = RequestMethod.GET)
    public String showStartShiftPage(Model model) {
        Shift lastShift = shiftService.getLast();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.YYYY");
        LocalDateTime date = timeManager.getDateTime();
        if (lastShift != null && lastShift.isOpen()) {
            return "redirect:/manager";
        } else if (lastShift != null) {
            model.addAttribute("shiftCashBox", shiftService.getLast().getCashBox());
            model.addAttribute("bankCashBox", shiftService.getLast().getBankCashBox());
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("date", dateTimeFormatter.format(date));
        model.addAttribute("openChecklist", checklistService.getAllForOpenShift());
        model.addAttribute("closeChecklist", checklistService.getAllForCloseShift());
        return "shift/shiftPage";
    }


    @Transactional
    @RequestMapping(value = "/shift/begin", method = RequestMethod.POST)
    public String startShift(@RequestParam(name = "userId", required = false) long[] usersIdsOnShift,
                             @RequestParam(name = "cashBox", required = false) Double cashBox,
                             @RequestParam(name = "bankCashBox", required = false) Double bankCashBox,
                             Authentication authentication) {
        Shift lastShift = shiftService.getLast();
        if (lastShift != null && lastShift.isOpen()) {
            return "redirect:/manager";
        }
        if (lastShift != null) {
            cashBox = lastShift.getCashBox();
            bankCashBox = lastShift.getBankCashBox();
        }
        if (usersIdsOnShift == null) {
            User user = userService.findByEmail(authentication.getName());
            shiftService.createNewShift(cashBox, bankCashBox, user.getId());
        } else {
            shiftService.createNewShift(cashBox, bankCashBox, usersIdsOnShift);
        }

        return "redirect:/manager";
    }

    @RequestMapping(value = "/shift/settings", method = RequestMethod.GET)
    public String showShiftSettingsPage(Model model) {
        Shift lastShift = shiftService.getLast();
		List<CalculateDTO> calculates = shiftCalculationService.getCalculates(lastShift);
        model.addAttribute("usersOnShift", lastShift.getUsers());
        model.addAttribute("usersNotOnShift", shiftService.getUsersNotOnShift());
        model.addAttribute("closeShiftView", shiftCalculationService.createShiftView(lastShift));
        model.addAttribute("calculates", calculates);
        model.addAttribute("clientOnDetail", shiftCalculationService.getClientsOnDetails(lastShift.getCalculates()));
        model.addAttribute("clients", shiftCalculationService.getClients(lastShift));
        model.addAttribute("closeChecklist", checklistService.getAllForCloseShift());
        model.addAttribute("repaidDebts", shiftService.getLast().getRepaidDebts());
        model.addAttribute("receipts", shiftService.getLast().getReceipts());
        return "shift/shiftSettings";
    }

    @RequestMapping(value = "/shift/deleteUser", method = RequestMethod.POST)
    public String deleteUserFromShift(@RequestParam(name = "userId") Long userId) {
        shiftService.deleteUserFromShift(userId);

        return "redirect:/manager/shift/settings";
    }

    @RequestMapping(value = "/shift/addUser", method = RequestMethod.POST)
    public String addUserToShift(@RequestParam(name = "userId") Long userId) {
        shiftService.addUserToShift(userId);

        return "redirect:/manager/shift/settings";
    }

    @RequestMapping(value = "/shift/close", method = RequestMethod.GET)
    public String showShiftClosePage(Model model) {
        model.addAttribute("closeShiftView", shiftCalculationService.createShiftView(shiftService.getLast()));
        return "shift/shiftClose";
    }

    @RequestMapping(value = "/shift/close", method = RequestMethod.POST)
    public String closeShift(ShiftCloseDTO shiftCloseDTO) {
        Shift lastShift = shiftService.getLast();
        ShiftView shiftView = shiftCalculationService.createShiftView(lastShift);
        if (!shiftView.getActiveCalculate().isEmpty()) {
            return "redirect:shift/shiftClose";
        }
        Double allPrice = shiftView.getAllPrice();
        Double payWithCard = shiftView.getCard();
        addPercentBonusesToUsers(shiftCloseDTO, shiftView);
        Integer totalBonusSum = getTotalBonusSum(shiftCloseDTO);
        Double totalCashBox = shiftView.getTotalCashBox() - totalBonusSum;
        Double cashBox = shiftCloseDTO.getCashBox();
        Double bankCashBox = shiftCloseDTO.getBankCashBox();
        Double shortage = totalCashBox - (cashBox + bankCashBox);

        if (shortage > 0) {
            List<User> users = userService.findByRoleName(EMAIL_RECIPIENT_ROLE_IN_CASE_SHORTAGE);
            emailService.sendCloseShiftInfoFromText(totalCashBox, cashBox, bankCashBox, payWithCard, allPrice, users, shortage);
        }
        closeShiftAndSendDailyReport(shiftCloseDTO, allPrice, cashBox, bankCashBox);

        return "redirect:/login";
    }

    private void addPercentBonusesToUsers(ShiftCloseDTO shiftCloseDTO, ShiftView shiftView) {
        Integer percentBonus;
        Map<Long, Integer> mapOfUsersIdsAndBonuses = shiftCloseDTO.getMapOfUsersIdsAndBonuses();
        Map<Long, Integer> staffPercentBonuses = shiftView.getStaffPercentBonuses();
        for (Map.Entry<Long, Integer> idAndBonus : mapOfUsersIdsAndBonuses.entrySet()) {
            percentBonus = getStaffPercentBonus(staffPercentBonuses, idAndBonus.getKey());
            Integer prevBonus = idAndBonus.getValue() != null ? idAndBonus.getValue() : 0;
            idAndBonus.setValue(prevBonus + percentBonus);
        }
    }

    private Integer getTotalBonusSum(ShiftCloseDTO shiftCloseDTO) {
        Integer totalBonusSum = 0;
        Map<Long, Integer> mapOfUsersIdsAndBonuses = shiftCloseDTO.getMapOfUsersIdsAndBonuses();
        for (Map.Entry<Long, Integer> idAndBonus : mapOfUsersIdsAndBonuses.entrySet()) {
            totalBonusSum += idAndBonus.getValue();
        }
        return totalBonusSum;
    }

    private Integer getStaffPercentBonus(Map<Long, Integer> staffPercentBonuses, Long id) {
        Integer percentBonus = staffPercentBonuses.get(id);
        return percentBonus == null ? 0 : percentBonus;
    }

    private void closeShiftAndSendDailyReport(ShiftCloseDTO shiftCloseDTO, Double allPrice, Double cashBox, Double bankCashBox) {
        Shift shift = shiftService.closeShift(shiftCloseDTO.getMapOfUsersIdsAndBonuses(), allPrice, cashBox, bankCashBox, shiftCloseDTO.getComment(), shiftCloseDTO.getMapOfNoteNameAndValue());
        vkService.sendDailyReportToConference(shift);
    }


    @ResponseBody
    @RequestMapping(value = "/shift/recalculation", method = RequestMethod.POST)
    public List<Object> recalculation(@RequestParam(name = "usersBonuses") Integer usersBonuses) {
        Shift lastShift = shiftService.getLast();
        ShiftView shiftView = shiftCalculationService.createShiftView(lastShift);
        int salaryWorkerOnShift = shiftView.getUsersTotalShiftSalary() + usersBonuses;
        Double totalCashBox = shiftView.getTotalCashBox() - usersBonuses;
        List<Object> result = new ArrayList<>();
        result.add(salaryWorkerOnShift);
        result.add(totalCashBox);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/shift/edit/transferCashToBankCashBox", method = RequestMethod.POST)
    public List<Object> transferToBankCashBox(@RequestParam(name = "transferBankCashBox") Double transferBankCashBox) {
        Shift lastShift = shiftService.getLast();
        if (transferBankCashBox > lastShift.getCashBox()) {
            throw new TransferException("Сумма превышает допустимое значение средств в кассе!");
        } else {
			shiftCalculationService.transferFromBankToCashBox(transferBankCashBox);
            List<Object> shiftRecalculationType = new ArrayList<>();
            shiftRecalculationType.add(lastShift.getCashBox());
            shiftRecalculationType.add(lastShift.getBankCashBox());
            return shiftRecalculationType;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/shift/edit/transferCashToCashBox", method = RequestMethod.POST)
    public List<Object> transferToCashBox(@RequestParam(name = "transferCashBox") Double transferCashBox) {
        Shift lastShift = shiftService.getLast();
        if (transferCashBox > lastShift.getBankCashBox()) {
            throw new TransferException("Сумма превышает допустимое значение средств  на карте!");
        } else {
			shiftCalculationService.transferFromCashBoxToBank(transferCashBox);
            List<Object> shiftRecalculationType = new ArrayList<>();
            shiftRecalculationType.add(lastShift.getCashBox());
            shiftRecalculationType.add(lastShift.getBankCashBox());
            return shiftRecalculationType;
        }
    }

    @ExceptionHandler(value = TransferException.class)
    public ResponseEntity<?> handleTransferException(TransferException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

