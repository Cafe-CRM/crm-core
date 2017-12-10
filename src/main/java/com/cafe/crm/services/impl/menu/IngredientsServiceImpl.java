package com.cafe.crm.services.impl.menu;

import com.cafe.crm.exceptions.services.NegativeOrZeroInputsIngredientsServiceException;
import com.cafe.crm.exceptions.services.NotEnoughIngredientsIngredientsServiceException;
import com.cafe.crm.exceptions.services.NullInputsIngredientsServiceException;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.dto.WrapperOfProduct;
import com.cafe.crm.repositories.menu.IngredientsRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IngredientsServiceImpl implements IngredientsService {

	private final IngredientsRepository ingredientsRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public IngredientsServiceImpl(IngredientsRepository ingredientsRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.ingredientsRepository = ingredientsRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Ingredients ingredients) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		ingredients.setCompany(company);
	}

	@Override
	public List<Ingredients> getAll() {
		return ingredientsRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public void save(Ingredients ingredients) {
		setCompany(ingredients);
		ingredientsRepository.saveAndFlush(ingredients);
	}

	@Override
	public Ingredients getOne(Long id) {
		return ingredientsRepository.findOne(id);
	}

	@Override
	public void delete(Long id) {
		ingredientsRepository.delete(id);
	}

	@Override
	public Ingredients findByName(String name) {
		return ingredientsRepository.findByNameAndCompanyId(name, companyIdCache.getCompanyId());
	}

	@Override
	public Map<Ingredients, Double> createRecipe(WrapperOfProduct wrapper) {

		if (wrapper == null) {
			throw new NullInputsIngredientsServiceException("Null input in createRecipe");
		}
		Map<Ingredients, Double> recipe = new HashMap<>();
		List<String> names = wrapper.getNames();
		List<Double> amount = wrapper.getAmount();
		String ingredientName;
		if ((names == null) || (amount == null)) {
			throw new NullInputsIngredientsServiceException("Null input in createRecipe");
		}

		Ingredients ingredients;
		for (int i = 0; i < names.size(); i++) {
			ingredientName = names.get(i);
			ingredients = findByName(ingredientName);
			if (ingredients == null) {
				throw new NullInputsIngredientsServiceException("Couldn't find " + ingredientName +
						" ingredient for recipe");
			}
			if (amount.get(i) <= 0) {
				throw new NegativeOrZeroInputsIngredientsServiceException("Negative or zero amount for " + ingredientName +
						" ingredient");
			}
			if (ingredients.getAmount() < amount.get(i)) {
				throw new NotEnoughIngredientsIngredientsServiceException("There are no enough " + ingredientName +
						" ingredient for recipe");
			}
			recipe.put(ingredients, amount.get(i));
		}

		return recipe;
	}

	@Override
	public Double getRecipeCost(Map<Ingredients, Double> recipe) {
		double result = 0L;
		if (recipe == null) {
			throw new NullInputsIngredientsServiceException("Null input in getRecipeCost");
		}

		for (Map.Entry<Ingredients, Double> entry : recipe.entrySet()) {
			if (entry.getKey() == null) {
				throw new NullInputsIngredientsServiceException("Null input in getRecipeCost");
			}
			if (entry.getValue() <= 0) {
				throw new NegativeOrZeroInputsIngredientsServiceException("Negative or zero amount for " + entry.getKey().getName());
			}
			result += entry.getKey().getPrice() * entry.getValue();
		}
		return result;
	}

	@Override
	public void reduceIngredientAmount(Map<Ingredients, Double> recipe) {
		for (Map.Entry<Ingredients, Double> entry : recipe.entrySet()) {
			entry.getKey().setAmount(entry.getKey().getAmount() - entry.getValue());
			save(entry.getKey());
		}
	}

	@Override
	public void retrieveIngredientAmount(Map<Ingredients, Double> recipe) {
		for (Map.Entry<Ingredients, Double> entry : recipe.entrySet()) {
			entry.getKey().setAmount(entry.getKey().getAmount() + entry.getValue());
			save(entry.getKey());
		}
	}
}
