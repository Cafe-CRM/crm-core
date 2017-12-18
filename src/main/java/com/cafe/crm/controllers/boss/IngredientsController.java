package com.cafe.crm.controllers.boss;


import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("boss/menu/ingredients")
public class IngredientsController {

	private final IngredientsService ingredientsService;

	@Autowired
	public IngredientsController(IngredientsService ingredientsService) {
		this.ingredientsService = ingredientsService;
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
		}
		return "redirect:/boss/menu/ingredients";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteIng(Long id) {
		ingredientsService.delete(id);
		return "redirect:/boss/menu/ingredients";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addAmount(@RequestParam("add") Double amount, @RequestParam("id") Long id) {

		Ingredients ingredients = ingredientsService.getOne(id);
		if (ingredients != null && amount > 0) {
			double am = ingredients.getAmount();
			ingredients.setAmount(am + amount);
			ingredientsService.save(ingredients);
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
		}
		return "redirect:/boss/menu/ingredients";
	}


}


