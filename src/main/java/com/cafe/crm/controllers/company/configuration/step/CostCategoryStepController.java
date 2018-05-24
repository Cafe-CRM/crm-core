package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.exceptions.cost.category.CostCategoryDataException;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.services.impl.company.configuration.step.CostCategoryStep;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/company/configuration/step/costCategory")
public class CostCategoryStepController {
	private final CostCategoryService categoryService;

	@Autowired
	public CostCategoryStepController(CostCategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResponseEntity<?> add(@ModelAttribute @Valid CostCategory category, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new CostCategoryDataException("Не удалось добавить товар! " + fieldError);
		}
		CostCategory costCategory = categoryService.save(category);
		return ResponseEntity.ok(costCategory);
	}

	@RequestMapping(value = "/check-as-salary", method = RequestMethod.POST)
	public ResponseEntity<?> checkAsSalary(@RequestParam(name = "categoryId") Long categoryId,
										   @RequestParam(name = "isSalary") boolean isSalary,
										   @RequestParam(name = "isChangeable") boolean isChangeable) {
		if (isChangeable) {
			CostCategory category = categoryService.getSalaryCategory();
			if (category != null) {
				category.setSalaryCost(false);
				categoryService.update(category);
			}
		}
		CostCategory category = categoryService.find(categoryId);
		category.setSalaryCost(isSalary);
		categoryService.update(category);
		return ResponseEntity.ok(category);
	}

	@RequestMapping(value = "/check-exist-category", method = RequestMethod.POST)
	public ResponseEntity<?> checkExistSalary() {
		if (!categoryService.isAnyCostCategoryExist()) {
			throw new CostCategoryDataException("В системе нет ни одной категории расходов!");
		}

		if (!categoryService.isSalaryCostExist()) {
			throw new CostCategoryDataException("В системе должна быть зарплатная категория расходов!");
		}

		return ResponseEntity.ok("");
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteBoard(@RequestParam(name = "categoryId") Long categoryId) {
		categoryService.delete(categoryId);
		return ResponseEntity.ok(categoryId);
	}

	@ExceptionHandler(value = CostCategoryDataException.class)
	public ResponseEntity<?> handleUserUpdateException(CostCategoryDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
