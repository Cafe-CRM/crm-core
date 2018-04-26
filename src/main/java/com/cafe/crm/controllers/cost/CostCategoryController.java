package com.cafe.crm.controllers.cost;

import com.cafe.crm.exceptions.cost.category.CostCategoryDataException;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.services.impl.company.configuration.step.CostCategoryStep;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/boss/cost/category")
public class CostCategoryController {

	private final CostCategoryService categoryService;

	private final CostCategoryStep costCategoryStep;

	@Autowired
	public CostCategoryController(CostCategoryService categoryService, CostCategoryStep costCategoryStep) {
		this.categoryService = categoryService;
		this.costCategoryStep = costCategoryStep;
	}


	@RequestMapping(method = RequestMethod.GET)
	public String showCategoryPage(Model model) {
		model.addAttribute("categoryList", categoryService.findAll());
		model.addAttribute("formCategory", new CostCategory());

		return "costs/categories/category";
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResponseEntity<?> add(@ModelAttribute @Valid CostCategory category, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new CostCategoryDataException("Не удалось добавить товар " + fieldError);
		}
		categoryService.save(category);
		return ResponseEntity.ok("Категория успешно добавлена!");
	}

	@RequestMapping(value = "/edit")
	@ResponseBody
	public ResponseEntity<?> edit(@ModelAttribute CostCategory category, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body("Не удалось обновить категорию");
		}
		categoryService.update(category);
		return ResponseEntity.ok("Категория успешно обновлена!");
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestParam(name = "id") Long id) {
		checkAsSalary(id);
		categoryService.delete(id);
		return ResponseEntity.ok("Категория успешно удалена!");
	}

	private void checkAsSalary(Long id) {
		CostCategory category = categoryService.find(id);
		if (category.isSalaryCost()) {
			costCategoryStep.setIsReconfigured(true);
		}
	}

	@ExceptionHandler(value = CostCategoryDataException.class)
	public ResponseEntity<?> handleUserUpdateException(CostCategoryDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

}
