package com.cafe.crm.controllers.boss;

import com.cafe.crm.dto.MissingUserDTO;
import com.cafe.crm.dto.OpenMissingShiftDTO;
import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.missing.MissingProductService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.cafe.crm.services.interfaces.salary.UserSalaryDetailService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.Target;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(path = "/boss/add-missing-shift")
public class AddMissingShiftDataController {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final ShiftService shiftService;
    private final TimeManager timeManager;
    private final ShiftCalculationService shiftCalculationService;
    private final CategoriesService categoriesService;
    private final MissingProductService missingProductService;
    private final DebtService debtService;
    private final CostCategoryService costCategoryService;
    private final CostService costService;
    private final UserService userService;
    private final UserSalaryDetailService userSalaryDetailService;
    private final VkService vkService;
    private final PositionService positionService;

    @Autowired
    public AddMissingShiftDataController(ShiftService shiftService, TimeManager timeManager,
                                         ShiftCalculationService shiftCalculationService, CategoriesService categoriesService,
                                         MissingProductService missingProductService, DebtService debtService,
                                         CostCategoryService costCategoryService, CostService costService,
                                         UserService userService, UserSalaryDetailService userSalaryDetailService,
                                         VkService vkService, PositionService positionService) {
        this.shiftService = shiftService;
        this.timeManager = timeManager;
        this.shiftCalculationService = shiftCalculationService;
        this.categoriesService = categoriesService;
        this.missingProductService = missingProductService;
        this.debtService = debtService;
        this.costCategoryService = costCategoryService;
        this.costService = costService;
        this.userService = userService;
        this.userSalaryDetailService = userSalaryDetailService;
        this.vkService = vkService;
        this.positionService = positionService;
    }

    @GetMapping
    public ModelAndView openMissingPage() {
        LocalDate now = timeManager.getDate();
        Shift shift = shiftService.getLastMissingShift();
        if (shift == null || !shift.isOpen()) {
            ModelAndView modelAndView = new ModelAndView("shift/openMissingShift");
            modelAndView.addObject("date", now);
            return modelAndView;
        }

        return getPopulatingModel(shift);
    }

    private ModelAndView getPopulatingModel(Shift shift) {
        ModelAndView modelAndView = new ModelAndView("shift/missingShiftPopulating");
        modelAndView.addObject("categories", categoriesService.findAll());
        modelAndView.addObject("shift", shift);
        modelAndView.addObject("products", missingProductService.findByShiftWithCategory(shift));
        modelAndView.addObject("otherDebt", debtService.findAllGivenOtherDebt(shift));
        modelAndView.addObject("cashBoxDebt", debtService.findAllGivenCashBoxDebt(shift));
        modelAndView.addObject("costCategories", costCategoryService.findAllOtherCost());
        modelAndView.addObject("costs", costService.findOtherCostAtShift(shift));
        modelAndView.addObject("adminsDTO", getAdminsMissing(shift));
        modelAndView.addObject("workersDTO", getWorkersMissing(shift));
        modelAndView.addObject("usersDTO", getUsersMissing(shift));
        modelAndView.addObject("admins", userService.findByPositionIdWithAnyEnabledStatus(1L));
        modelAndView.addObject("workers", findAllWorkers());
        return modelAndView;
    }

    @PostMapping(value = "/check-shift-date")
    public String checkShiftsDate(@Param("shiftDate") String shiftDate) {
        LocalDate from = (shiftDate == null || shiftDate.isEmpty()) ? null : LocalDate.parse(shiftDate);
        long count = shiftService.countingByDateWithoutMissing(from);

        if (count > 0) {
            return "redirect:/boss/add-missing-shift/get-all-shifts-date/" + shiftDate;
        }

        long missingCount = shiftService.countingByMissing();
        if (missingCount == 0) {
            shiftService.createMissingShift(from);
        }
        return "redirect:/boss/add-missing-shift/populating";
    }

    @GetMapping(value = "/populating")
    public ModelAndView populateForm() {
        Shift shift = shiftService.getMissingLast();
        return getPopulatingModel(shift);
    }

    @RequestMapping(value = "/get-all-shifts-date/{shiftDate}", method = RequestMethod.GET)
    public ModelAndView getAllShiftsOnDate(@PathVariable("shiftDate") String shiftDate) {
        LocalDate from = (shiftDate == null || shiftDate.isEmpty()) ? null : LocalDate.parse(shiftDate);
        List<Shift> shifts = shiftService.findAllByDate(from);

        ModelAndView modelAndView = new ModelAndView("shift/existedShifts");
        modelAndView.addObject("date", from);
        modelAndView.addObject("shiftsDTO", convertShiftToDto(shifts));
        return modelAndView;
    }

    private List<OpenMissingShiftDTO> convertShiftToDto(List<Shift> shifts) {
        List<OpenMissingShiftDTO> dtos = new ArrayList<>();

        for (Shift shift : shifts) {
            OpenMissingShiftDTO dto = new OpenMissingShiftDTO();
            dto.setId(shift.getId());
            dto.setBankCashBox(shift.getBankCashBox());
            dto.setCashBox(shift.getCashBox());
            dto.setShiftDate(shift.getShiftDate());
            dto.setProfit(shift.getProfit());

            dtos.add(dto);
        }

        return dtos;
    }

    private List<MissingUserDTO> getUsersMissing(Shift shift) {
        List<UserSalaryDetail> details = userSalaryDetailService.findByShiftId(shift.getId());
        return convertDetailsToDto(details);
    }

    private List<MissingUserDTO> getAdminsMissing(Shift shift) {
        List<UserSalaryDetail> details = userSalaryDetailService.findAllAdminsDetailByShift(shift);
        return convertDetailsToDto(details);
    }

    private List<MissingUserDTO> getWorkersMissing(Shift shift) {
        List<UserSalaryDetail> details = new ArrayList<>();
        Position admin = positionService.findById(1L);
        for (UserSalaryDetail detail : userSalaryDetailService.findAllWorkersDetailByShift(shift)) {
            User user = detail.getUser();
            if (!user.getPositions().contains(admin)) {
                details.add(detail);
            }
        }
        return convertDetailsToDto(details);
    }

    private List<User> findAllWorkers() {
        List<User> workers = new ArrayList<>();
        Position admin = positionService.findById(1L);
        for (User user : userService.findByPositionIdIsNotWithAnyEnabledStatus(1L)) {
            if (!user.getPositions().contains(admin)) {
                workers.add(user);
            }
        }
        return workers;
    }

    private List<MissingUserDTO> convertDetailsToDto(List<UserSalaryDetail> details) {
        List<MissingUserDTO> users = new ArrayList<>();

        for (UserSalaryDetail detail : details) {
            User user = detail.getUser();
            MissingUserDTO dto = new MissingUserDTO();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setShiftSalary(user.getShiftSalary());
            dto.setPaidBonus(detail.getPaidBonus());
            dto.setSalary(detail.isPaidDetail());
            users.add(dto);
        }

        return users;
    }

    @PostMapping
    public ModelAndView createMissingShift(@Param("date") String date) {
        LocalDate shiftDate = LocalDate.parse(date);
        Shift shiftOnThisDate = shiftService.findByDate(shiftDate);
        ModelAndView modelAndView = new ModelAndView("shift/missingShiftPopulating");

        /*if (shiftOnThisDate.isEmpty()) {
            Shift shift = new Shift();
            shift.setShiftDate(shiftDate);
        }*/
        return modelAndView;
    }

    @PostMapping(value = "/add-missing-product")
    @ResponseBody
    public ResponseEntity<?> addMissingProduct(@Param("catId") Long catId,
                                                  @Param("amount") Integer amount,
                                                  @Param("price") Double price) {
        Shift shift = shiftService.getMissingLast();
        Category category = categoriesService.getOne(catId);

        MissingProduct product = new MissingProduct(category.getName(), amount, price, category, shift);

        missingProductService.saveAndFlush(product);
        return ResponseEntity.ok("Продукт успешно добавлен!");
    }

    @PostMapping(value = "/delete-missing-product")
    @ResponseBody
    public ResponseEntity<?> deleteMissingProduct(@Param("prodId") Long prodId) {
        //Shift shift = shiftService.getMissingLast();
        MissingProduct product = missingProductService.findOne(prodId);
        //for the logging

        if (product != null) {
            missingProductService.delete(prodId);
        }

        return ResponseEntity.ok("Продукт успешно удалён!");
    }

    @PostMapping(value = "/add-missing-debt")
    @ResponseBody
    public ResponseEntity<?> addOtherDebtProduct(@Param("debtor") String debtor,
                                                 @Param("amount") Double amount,
                                                 @Param("isCashBox") Boolean isCashBox) {
        Shift shift = shiftService.getMissingLast();

        Debt debt = new Debt(debtor, amount, shift.getShiftDate(), shift);
        debt.setCashBoxDebt(isCashBox);
        debtService.save(debt);
        return ResponseEntity.ok("Долг успешно добавлен!");
    }

    @PostMapping(value = "/add-missing-cost")
    @ResponseBody
    public ResponseEntity<?> addOtherDebtProduct(@Param("costName") String costName,
                                                 @Param("costCat") Long costCat,
                                                 @Param("costPrice") Double costPrice,
                                                 @Param("costAmount") Integer costAmount) {
        Shift shift = shiftService.getMissingLast();

        CostCategory costCategory = costCategoryService.find(costCat);

        if (costCategory != null) {
            Cost cost = new Cost(costName, costPrice, costAmount, costCategory, shift, shift.getShiftDate());
            costService.save(cost);
            shiftService.saveAndFlush(shift);
        }

        return ResponseEntity.ok("Долг успешно добавлен!");
    }

    @PostMapping(value = "/add-user")
    @ResponseBody
    public ResponseEntity<?> addUserOnShift(@Param("userId") Long userId) {
        Shift shift = shiftService.getMissingLast();
        User user = userService.findById(userId);
        if (user != null) {

            if (shift.getUsers().contains(user)) {
                return ResponseEntity.ok("Работник уже есть на этой смене!");
            }

            shift.getUsers().add(user);
            user.getShifts().add(shift);

            UserSalaryDetail salaryDetail = shiftCalculationService.getUserSalaryDetail(user, 0, 0, shift);
            salaryDetail.setPaidSalary(user.getShiftSalary());
            user.addSalaryDetail(salaryDetail);
            userSalaryDetailService.save(salaryDetail);
            userService.save(user);
            shiftService.saveAndFlush(shift);
        }


        return ResponseEntity.ok("Работник успешно добавлен!");
    }

    @PostMapping(value = "/edit-bonus")
    @ResponseBody
    public ResponseEntity<?> editBonus(@Param("userId") Long userId,
                                       @Param("bonus") Integer bonus) {
        Shift shift = shiftService.getMissingLast();
        UserSalaryDetail detail = userSalaryDetailService.findFirstByUserIdAndShiftId(userId, shift.getId());

        if (detail != null) {
            detail.setPaidBonus(bonus);
            userSalaryDetailService.save(detail);
        }

        return ResponseEntity.ok("Бонус успешно изменён!");
    }

    @PostMapping(value = "/delete-debt")
    @ResponseBody
    public ResponseEntity<?> deleteOtherDebt(@Param("debtId") Long debtId) {
        //Shift shift = shiftService.getMissingLast();
        Debt debt = debtService.get(debtId);
        //for the logging

        if (debt != null) {
            debtService.delete(debtId);
        }

        return ResponseEntity.ok("Долг успешно удалён!");
    }

    @PostMapping(value = "/delete-cost")
    @ResponseBody
    public ResponseEntity<?> deleteMissingCost(@Param("costId") Long costId) {
        //Shift shift = shiftService.getMissingLast();
        Cost cost = costService.getOne(costId);
        //for the logging

        if (cost != null) {
            costService.delete(costId);
        }

        return ResponseEntity.ok("Долг успешно удалён!");
    }

    @PostMapping(value = "/delete-user")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@Param("userId") Long userId) {
        Shift shift = shiftService.getMissingLast();
        User user = userService.findById(userId);
        UserSalaryDetail detail = userSalaryDetailService.findFirstByUserIdAndShiftId(userId, shift.getId());
        if (user != null) {
            shift.getUsers().remove(user);
            user.getShifts().remove(shift);
            userSalaryDetailService.deleteByUserIdAndShiftId(userId, shift.getId());
        }
        shiftService.saveAndFlush(shift);
        return ResponseEntity.ok("Работник успешно удалён!");
    }

    @PostMapping(value = "/change-salary-state")
    @ResponseBody
    public ResponseEntity<?> changeSalaryState(@Param("userId") Long userId,
                                               @Param("state") Boolean state) {
        Shift shift = shiftService.getMissingLast();
        User user = userService.findById(userId);
        UserSalaryDetail detail = userSalaryDetailService.findFirstByUserIdAndShiftId(userId, shift.getId());

        if (user != null) {
            detail.setPaidDetail(state);
            userSalaryDetailService.save(detail);
        }

        return ResponseEntity.ok("Состояние успешно изменено!");
    }

    @PostMapping(value = "/send-cash-box-data")
    @ResponseBody
    public String closeShiftData(@Param("profit") Double profit,
                                               @Param("cashBox") Double cashBox,
                                               @Param("bankCashBox") Double bankCashBox) {
        Shift shift = shiftService.getMissingLast();
        shift.setProfit(profit);
        shift.setCashBox(cashBox);
        shift.setBankCashBox(bankCashBox);
        Shift savedShift = shiftService.saveAndFlush(shift);

        return vkService.getReportMessage(savedShift);
    }

    @PostMapping(value = "/delete-missing-shifts")
    @ResponseBody
    public String deleteShifts(@Param("shiftId") Long shiftId) {
        shiftService.deleteMissingShift(shiftId);
        return "redirect:/boss/menu";
    }

    @PostMapping(value = "/delete-shifts")
    @ResponseBody
    public String deleteShift(@Param("date") String date, @Param("password") String password) {
        LocalDate from = (date == null || date.isEmpty()) ? null : LocalDate.parse(date);
        List<Shift> shifts = shiftService.findAllByDate(from);

        long[] shiftIds = shifts.stream().mapToLong(Shift::getId).toArray();

        shiftService.deleteShifts(password, shiftIds);

        checkShiftsDate(date);

        return checkShiftsDate(date);
    }

    @PostMapping(value = "/close-missing-shift")
    @ResponseBody
    public String closeMissingShift() {
        Shift shift = shiftService.getMissingLast();

        List<UserSalaryDetail> paidDetails = userSalaryDetailService.findPaidDetailsByShiftId(shift.getId());
        List<UserSalaryDetail> otherDetails = userSalaryDetailService.findOtherDetailsByShiftId(shift.getId());
        List<User> salaryUser = new ArrayList<>();

        if (!paidDetails.isEmpty()) {
            StringBuilder costName = new StringBuilder();
            double totalCost = 0;

            for (int i = 0; i < paidDetails.size(); i++) {
                UserSalaryDetail detail = paidDetails.get(i);
                User user = detail.getUser();
                costName.append(user.getFirstName())
                        .append(" ")
                        .append(user.getLastName());
                if (i == (paidDetails.size() - 1)) {
                    costName.append("\n");
                } else {
                    costName.append(",\n");
                }

                totalCost += detail.getPaidBonus() + detail.getPaidSalary();
            }

            CostCategory salaryCategory = costCategoryService.getSalaryCategory();
            LocalDate lastDate = shift.getShiftDate();
            Cost cost = new Cost(costName.toString(), totalCost, 1, salaryCategory, shift, lastDate);
            costService.save(cost);
        }

        for (UserSalaryDetail detail : otherDetails) {
            User user = detail.getUser();
            int bonus = detail.getPaidBonus() + user.getBonusBalance();
            int salary = detail.getPaidSalary() + user.getSalaryBalance();

            user.setBonusBalance(bonus);
            user.setSalaryBalance(salary);

            detail.setPaidBonus(0);
            detail.setBonusBalance(bonus);
            detail.setPaidSalary(0);
            detail.setSalaryBalance(salary);
        }

        userSalaryDetailService.save(otherDetails);
        userService.save(salaryUser);

        shift.setOpened(false);
        Shift savedShift = shiftService.saveAndFlush(shift);
        vkService.sendDailyReportToConference(shift);

        return "redirect:/boss/menu";
    }

    @RequestMapping(value = {"/send-delete-shifts-token"}, method = RequestMethod.POST)
    public ResponseEntity sendCalculateDeletePassword(@RequestParam(name = "date") String date) {
        LocalDate from = (date == null || date.isEmpty()) ? null : LocalDate.parse(date);

        List<OpenMissingShiftDTO> shifts = convertShiftToDto(shiftService.findAllByDate(from));

        StringBuilder prefix = new StringBuilder("Одноразовый пароль для подтверждения удаления смен c id: ");

        for (int i = 0; i < shifts.size(); i++) {
            OpenMissingShiftDTO shift = shifts.get(i);
            prefix.append(shift.getId());
            if (i < shifts.size() - 1) {
                prefix.append(", ");
            }
        }
        prefix.append("\n");

        vkService.sendConfirmToken(prefix.toString(), Target.DELETE_SHIFT);
        return ResponseEntity.ok("Пароль послан");
    }


    @PostMapping(value = "/get-all-category")
    @ResponseBody
    public List<Category> getAllCategory() {
        return categoriesService.findAll();
    }

    @PostMapping(value = "/get-all-product")
    @ResponseBody
    public List<MissingProduct> getAllProduct() {
        Shift shift = shiftService.getMissingLast();
        return missingProductService.findByShiftWithCategory(shift);
    }

    @PostMapping(value = "/get-all-other-debt")
    @ResponseBody
    public List<Debt> getAllOtherDebt() {
        Shift shift = shiftService.getMissingLast();
        return debtService.findAllGivenOtherDebt(shift);
    }

    @PostMapping(value = "/get-all-cash-box-debt")
    @ResponseBody
    public List<Debt> getAllCashBoxDebt() {
        Shift shift = shiftService.getMissingLast();
        return debtService.findAllGivenCashBoxDebt(shift);
    }

    @PostMapping(value = "/get-all-missing-cost")
    @ResponseBody
    public List<Cost> getAllMissingCost() {
        Shift shift = shiftService.getMissingLast();
        return costService.findOtherCostByShiftId(shift);
    }

    @PostMapping(value = "/get-all-admins-on-shift")
    @ResponseBody
    public List<MissingUserDTO> getAllAdmins() {
        Shift shift = shiftService.getMissingLast();
        return getAdminsMissing(shift);
    }

    @PostMapping(value = "/get-all-workers-on-shift")
    @ResponseBody
    public List<MissingUserDTO> getAllWorkers() {
        Shift shift = shiftService.getMissingLast();
        return getWorkersMissing(shift);
    }

    @PostMapping(value = "/get-all-users-on-shift")
    @ResponseBody
    public List<MissingUserDTO> getUsersMissing() {
        Shift shift = shiftService.getMissingLast();
        return getUsersMissing(shift);
    }

    @ExceptionHandler(value = PasswordException.class)
    public ResponseEntity<?> handleTransferException(PasswordException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
