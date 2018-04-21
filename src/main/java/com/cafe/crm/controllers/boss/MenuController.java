package com.cafe.crm.controllers.boss;

import com.cafe.crm.dto.CategoryDTO;
import com.cafe.crm.dto.WrapperOfEditProduct;
import com.cafe.crm.exceptions.category.CategoryServiceException;
import com.cafe.crm.exceptions.cost.category.CostCategoryDataException;
import com.cafe.crm.exceptions.services.IngredientsServiceException;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.dto.WrapperOfProduct;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.services.interfaces.menu.MenuService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/boss/menu")
public class MenuController {

	private final MenuService menuService;
	private final CategoriesService categoriesService;
	private final ProductService productService;
	private final IngredientsService ingredientsService;
	private final PositionService positionService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	public MenuController(CategoriesService categoriesService, ProductService productService, IngredientsService ingredientsService, MenuService menuService, PositionService positionService) {
		this.categoriesService = categoriesService;
		this.productService = productService;
		this.ingredientsService = ingredientsService;
		this.menuService = menuService;
		this.positionService = positionService;
	}

	@ModelAttribute(value = "product")
	public Product newProduct() {
		return new Product();
	}

	@ModelAttribute(value = "ingredients")
	public List<Ingredients> getAll() {
		return ingredientsService.getAll();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getAdminPage() {
		ModelAndView mv = new ModelAndView("menu/bossMenu");
		mv.addObject("menu", menuService.getOne(1L));
		mv.addObject("categories", categoriesService.findAll());
		mv.addObject("products", productService.findAllOrderByRatingDesc());
		mv.addObject("positions", positionService.findAll());

		return mv;
	}

	@RequestMapping(value = {"/deleteProduct"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteProduct(@RequestParam(value = "del", required = false) Long id) throws IOException {
		Product product = productService.findOne(id);
		productService.delete(id);

		logger.info("Удалён продукт с именем: \"" + product.getName() + "\" и id: " + id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updCategory", method = RequestMethod.POST)
	public String updCategory(@RequestParam(name = "upd") Long id,
							  @RequestParam(name = "name") String name,
							  @RequestParam(name = "dirtyProfit", required = false, defaultValue = "true") String dirtyProfit,
							  @RequestParam(name = "accountability", required = false, defaultValue = "false") String accountability) {
		Boolean isDirty = Boolean.valueOf(dirtyProfit);
		Boolean isAccountability = Boolean.valueOf(accountability);
		Category category = categoriesService.getOne(id);
		CategoryDTO dto = new CategoryDTO(category);

		if (category != null) {
			category.setName(name);
			category.setDirtyProfit(isDirty);
			category.setAccountability(isAccountability);

			categoriesService.saveAndFlush(category);

			logger.info("Изменена категория с id: " + category.getId() +
					"\n Название: " + dto.getName() + " -> " + category.getName() +
					"\n Добавлять в отчёт: " + dto.isAccountability() + " -> " + category.isAccountability() +
					"\n Учитывать в прибыль грязными: " + dto.isDirtyProfit() + " -> " + category.isDirtyProfit() +
					"\n Плавающая цена: " + dto.isFloatingPrice() + " -> " + category.isFloatingPrice()

			);
		}
		return "redirect:/boss/menu/";
	}

	@RequestMapping(value = "/getProduct", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> getProductForAjax(@RequestParam(name = "id") Long id) {

		Product product = productService.findOne(id);
		product.setCategory(null);
		return new ResponseEntity<>(product, HttpStatus.OK);
	}

	@RequestMapping(value = "/addProduct", method = RequestMethod.POST)
	@ResponseBody
	public WrapperOfProduct createProd(@RequestBody @Valid final WrapperOfProduct wrapper, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new IngredientsServiceException(fieldError);
		}

		Category category = categoriesService.getOne(wrapper.getId());
		Map<Ingredients, Double> recipe;
		double recipeCost;

		if (!wrapper.getNames().isEmpty()) {
			recipe = ingredientsService.createRecipe(wrapper);
			recipeCost = ingredientsService.getRecipeCost(recipe);
		} else {
			recipe = null;
			recipeCost = 0;
		}

		Map<Position, Integer> staffPercent = productService.createStaffPercent(wrapper);
		if (category != null) {
			Product product = new Product();
			product.setCategory(category);
			product.setName(wrapper.getName());
			product.setCost(wrapper.getCost());
			if (recipeCost == 0) {
				product.setSelfCost(wrapper.getSelfCost());
			} else {
				product.setSelfCost(recipeCost);
			}
			product.setDescription(wrapper.getDescription());
			product.setRecipe(recipe);
			product.setStaffPercent(staffPercent);
			Product savedProduct = productService.saveAndFlush(product);
			category.getProducts().add(product);
			categoriesService.saveAndFlush(category);

			logger.info("В категорию " + category.getName() + " добавлен продукт с названием: \"" + product.getName() +
					"\" и id: " + savedProduct.getId());

			wrapper.setProductId(product.getId());
			wrapper.setSelfCost(wrapper.getSelfCost() + recipeCost);
		}
		return wrapper;
	}

	@RequestMapping(value = "/addCategory", method = RequestMethod.POST)
	public String addCategories(@Valid Category category, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new CategoryServiceException("Не удалось добавить категорию " + fieldError);
		}
		List<Product> listProducts = new ArrayList<>();
		category.setProducts(listProducts);
		Category savedCat = categoriesService.saveAndFlush(category);

		logger.info("Добавлена категория с названием: \"" + category.getName() + "\" и id: " + savedCat.getId());

		return "redirect:/boss/menu";
	}

	@RequestMapping(value = "/deleteCat", method = RequestMethod.POST)
	public String deleteCategories(@RequestParam(name = "del") Long id) {
		Category category = categoriesService.getOne(id);
		if (category != null) {
			categoriesService.delete(id);
			logger.info("Удалена категория с названием: \"" + category.getName() + "\" и id: " + id);
		}
		return "redirect:/boss/menu";
	}

	@RequestMapping(value = "/updProd", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public WrapperOfEditProduct updProd(@RequestBody WrapperOfEditProduct wrapper) {

		Product product = productService.findOne(wrapper.getId());
		WrapperOfEditProduct existProduct = new WrapperOfEditProduct(product);

		if (product != null) {
			product.setName(wrapper.getName());
			product.setCost(wrapper.getCost());
			product.setSelfCost(wrapper.getSelfCost());
			product.setDescription(wrapper.getDescription());

			productService.saveAndFlush(product);

			logger.info("Изменена категория с id: " + existProduct.getId() +
					"\n Название: " + existProduct.getName() + " -> " + product.getName() +
					"\n Цена: " + existProduct.getCost() + " -> " + product.getCost() +
					"\n Себестоимость: " + existProduct.getSelfCost() + " -> " + product.getSelfCost() +
					"\n Описание: " + existProduct.getDescription() + " -> " + product.getDescription()

			);
		}

		return wrapper;
	}

	@RequestMapping(value = "/get/recipe/", method = RequestMethod.GET)
	public ModelAndView getIngredientsForEdit(@RequestParam("id") Long idProduct) {

		ModelAndView modelAndView = new ModelAndView("menu/editRecipe");
		modelAndView.addObject("recipe", productService.findOne(idProduct).getRecipe());
		modelAndView.addObject("ingredients", ingredientsService.getAll());
		modelAndView.addObject("product", productService.findOne(idProduct));

		return modelAndView;
	}

	@RequestMapping(value = "/get/staffPercent/", method = RequestMethod.GET)
	public ModelAndView getStaffPercentForEdit(@RequestParam("id") Long idProduct) {

		ModelAndView modelAndView = new ModelAndView("menu/editStaffPercent");
		modelAndView.addObject("staffPercent", productService.findOne(idProduct).getStaffPercent());
		modelAndView.addObject("positions", positionService.findAll());
		modelAndView.addObject("product", productService.findOne(idProduct));

		return modelAndView;
	}

	@RequestMapping(value = "/edit/recipe", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editRecipe(@RequestBody WrapperOfProduct wrapper) {
		Product product = productService.findOne(wrapper.getId()); // id product
		Map<Ingredients, Double> recipe = ingredientsService.createRecipe(wrapper);

		if (product != null) {
			product.setSelfCost(ingredientsService.getRecipeCost(recipe));
			product.setRecipe(recipe);
			productService.saveAndFlush(product);

			StringBuilder editRecipeLog = new StringBuilder("Изменён рецепт продукта с id: ")
					.append(product.getId())
					.append(" и названием: \"")
					.append(product.getName())
					.append("\"\nНовый рецепт:");

			for (Map.Entry<Ingredients, Double> entry : recipe.entrySet()) {
				editRecipeLog
						.append("\n")
						.append(entry.getKey().getName())
						.append(" - ")
						.append(entry.getValue())
						.append(entry.getKey().getDimension());
			}

			logger.info(editRecipeLog.toString());
		}
		return new ResponseEntity<>(1L, HttpStatus.OK);
	}

	@RequestMapping(value = "/edit/staffPercent", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editStaffPercent(@RequestBody WrapperOfProduct wrapper) {
		Product product = productService.findOne(wrapper.getId()); // id product
		Map<Position, Integer> staffPercent = productService.createStaffPercent(wrapper);

		if (product != null) {
			product.setStaffPercent(staffPercent);
			productService.saveAndFlush(product);

			StringBuilder editPercentLog = new StringBuilder("Изменёны проценты сотрудникам у продукта с id: ")
					.append(product.getId())
					.append(" и названием: \"")
					.append(product.getName())
					.append("\"\n");

			for (Map.Entry<Position, Integer> entry : staffPercent.entrySet()) {
				editPercentLog.append(entry.getKey().getName())
						.append(" - ")
						.append(entry.getValue())
						.append("%")
						.append("\n");
			}

			logger.info(editPercentLog.toString());

		}
		return new ResponseEntity<>(1L, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/recipe/{id}", method = RequestMethod.POST)
	public String deleteRecipe(@PathVariable(name = "id") Long id, HttpServletRequest request) {
		Product product = productService.findOne(id);
		if (product != null) {
			double recipeCost = ingredientsService.getRecipeCost(product.getRecipe());
			product.setSelfCost(product.getSelfCost() - recipeCost);
			product.getRecipe().clear();
			productService.saveAndFlush(product);

			logger.info("Удалён рецепт у продукта с названием: \"" + product.getName() + "\" и id: " + product.getId());
		}
		String referrer = request.getHeader("Referer");
		return "redirect:" + referrer;
	}

	@RequestMapping(value = "/delete/staffPercent/{id}", method = RequestMethod.POST)
	public String deleteStaffPercent(@PathVariable(name = "id") Long id, HttpServletRequest request) {
		Product product = productService.findOne(id);
		if (product != null) {
			product.getStaffPercent().clear();
			productService.saveAndFlush(product);

			logger.info("Удалены проценты сотрудникам у продукта с названием: " + product.getName() + " и id: "
					+ product.getId());
		}
		String referrer = request.getHeader("Referer");
		return "redirect:" + referrer;
	}

	@ExceptionHandler(value = IngredientsServiceException.class)
	public ResponseEntity<?> handleIngredientsServiceException(IngredientsServiceException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = CategoryServiceException.class)
	public ResponseEntity<?> handleCategoryServiceException(CategoryServiceException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

}
