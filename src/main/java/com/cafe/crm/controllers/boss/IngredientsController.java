package com.cafe.crm.controllers.boss;


import com.cafe.crm.controllers.boss.settings.VkPropertiesSettingsController;
import com.cafe.crm.exceptions.ingredients.IngredientsException;
import com.cafe.crm.exceptions.product.ProductException;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.impl.menu.ProductServiceImpl;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("boss/menu/ingredients")
public class IngredientsController {

	private final IngredientsService ingredientsService;
	private final ProductServiceImpl productService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(VkPropertiesSettingsController.class);

	@Autowired
	public IngredientsController(IngredientsService ingredientsService, ProductServiceImpl productService) {
		this.ingredientsService = ingredientsService;
		this.productService = productService;
	}

	@ModelAttribute("ingredients")
	public Ingredients get() {
		return new Ingredients();
	}


	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getPage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ingredients/ingredients");
		model.addObject("list", ingredientsService.getAll());
		return model;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String createNew(@Valid Ingredients ingredients, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("list", ingredientsService.getAll());
			return "ingredients/ingredients";
		}
		if (ingredients.getAmount() > 0) {
			ingredients.setPrice(ingredients.getPrice() / ingredients.getAmount());
			ingredientsService.save(ingredients);

			logger.info("Добавлен ингредиент \"" + ingredients.getName() + "\"," + "колличеством в " + ingredients.getAmount() +
			ingredients.getDimension() + ". С ценой за единицу " + ingredients.getPrice());
		}
		return "redirect:/boss/menu/ingredients";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteIng(Long id) {
		Ingredients ingredients = ingredientsService.getOne(id);
		ingredientsService.delete(id);

		logger.info("Удалён ингредиент с именем: \"" + ingredients.getName() + "\"");

		return "redirect:/boss/menu/ingredients";
	}

	@RequestMapping(value = {"/get-ingredient-products"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Product> checkProductsAndDelete(@RequestParam(name = "ingredientId") Long id) {
		List<Product> receipt = productService.findAllReceiptProducts(ingredientsService.getOne(id));
		if (receipt.size() == 0) {
			deleteIng(id);
		}
		return receipt;
	}

	@RequestMapping(value = {"/delete-ingredient-from-products"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Product> delIngredientFromProducts(@RequestParam(name = "prodIds") long[] ids,
												   @RequestParam(name = "ingredientId") long ingredientId) {
		List<Product> products = productService.findByIds(ids);
		Ingredients ingredients = ingredientsService.getOne(ingredientId);
		productService.deleteIngredientFromProducts(products, ingredients);
		ingredientsService.delete(ingredientId);

		logger.info("Удалён ингредиент с именем: \"" + ingredients.getName() + "\"");

		return null;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addAmount(@RequestParam("add") Double amount, @RequestParam("id") Long id) {

		Ingredients ingredients = ingredientsService.getOne(id);
		if (ingredients != null && amount > 0) {
			double am = ingredients.getAmount();
			ingredients.setAmount(am + amount);
			ingredientsService.save(ingredients);

			logger.info("Доавлено " + amount + ingredients.getDimension() + " к ингридиенту \"" + ingredients.getName() +
					"\" (" + am + " -> " + ingredients.getAmount() + ")");
		}
		return "redirect:/boss/menu/ingredients";
	}

	@RequestMapping(value = "/deduct", method = RequestMethod.POST)
	public String deductAmount(@RequestParam("deduct") Double amount, @RequestParam("id") Long id) {

		Ingredients ingredients = ingredientsService.getOne(id);
		if (ingredients != null && amount > 0) {
			double am = ingredients.getAmount();
			if (am - amount >= 0) {
				ingredients.setAmount(am - amount);
			}
			ingredientsService.save(ingredients);

			logger.info("Колличество ингредиента \"" + ingredients.getName() + "\" уменьшено на "
					+ amount + ingredients.getDimension() +
					" (" + am + " -> " + ingredients.getAmount() + ")");
		}
		return "redirect:/boss/menu/ingredients";
	}

	@ExceptionHandler(value = ProductException.class)
	public ResponseEntity<?> handleUserUpdateException(ProductException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = IngredientsException.class)
	public ResponseEntity<?> handleUserUpdateException(IngredientsException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}


