package com.cafe.crm.controllers.boss;

import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.missing.MissingProductService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path = "/boss/add-missing-shift")
public class AddMissingShiftDataController {

    private final ShiftService shiftService;
    private final TimeManager timeManager;
    private final ShiftCalculationService shiftCalculationService;
    private final CategoriesService categoriesService;
    private final MissingProductService missingProductService;
    private final DebtService debtService;
    private final CostCategoryService costCategoryService;
    private final CostService costService;

    @Autowired
    public AddMissingShiftDataController(ShiftService shiftService, TimeManager timeManager,
                                         ShiftCalculationService shiftCalculationService, CategoriesService categoriesService,
                                         MissingProductService missingProductService, DebtService debtService,
                                         CostCategoryService costCategoryService, CostService costService) {
        this.shiftService = shiftService;
        this.timeManager = timeManager;
        this.shiftCalculationService = shiftCalculationService;
        this.categoriesService = categoriesService;
        this.missingProductService = missingProductService;
        this.debtService = debtService;
        this.costCategoryService = costCategoryService;
        this.costService = costService;
    }

    @GetMapping
    public ModelAndView openMissingPage() {
        /*LocalDate now = timeManager.getDate();
        ModelAndView modelAndView = new ModelAndView("shift/addMissingShiftData");
        modelAndView.addObject("now", now);
        return modelAndView;*/

        LocalDate now = timeManager.getDate();

        Shift shift;
        //todo доставать из базы не по дате, а по открытому стостоянию
        Shift ex = shiftService.getLastMissingShift();
        if (ex != null) {
            shift = ex;
        } else {
            shift = shiftService.createMissingShift(now);
        }

        //Shift shift = shiftService.createMissingShift(now);
        List<MissingProduct> products = missingProductService.findByShift(shift);

        ModelAndView modelAndView = new ModelAndView("shift/missingShiftPopulating");
        modelAndView.addObject("categories", categoriesService.findAll());
        modelAndView.addObject("shift", shift);
        modelAndView.addObject("products", products);
        modelAndView.addObject("otherDebt", debtService.findAllGivenOtherDebt(shift));
        modelAndView.addObject("cashBoxDebt", debtService.findAllGivenCashBoxDebt(shift));
        modelAndView.addObject("costCategories", costCategoryService.findAll());
        return modelAndView;
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
                                                 @Param("costAmount") Double costAmount) {
        Shift shift = shiftService.getMissingLast();

        CostCategory costCategory = costCategoryService.find(costCat);

        if (costCategory != null) {
            Cost cost = new Cost(costName, costPrice, costAmount, costCategory, shift, shift.getShiftDate());
            costService.save(cost);
            shift.addCostToSet(cost);
            shiftService.saveAndFlush(shift);
        }

        return ResponseEntity.ok("Долг успешно добавлен!");
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
        return costService.findOtherCostByShiftId(shift.getId());
    }
}
