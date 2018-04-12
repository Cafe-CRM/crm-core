package com.cafe.crm.services.impl.cost;

import com.cafe.crm.exceptions.cost.category.CostCategoryDataException;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.repositories.cost.CostCategoryRepository;
import com.cafe.crm.repositories.cost.CostRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CostCategoryServiceImpl implements CostCategoryService {

	private final CostCategoryRepository costCategoryRepository;
	private final CostRepository costService;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public CostCategoryServiceImpl(CostCategoryRepository goodsCategoryRepository, CostRepository costService, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.costCategoryRepository = goodsCategoryRepository;
		this.costService = costService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(CostCategory costCategory) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		costCategory.setCompany(company);
	}

	@Override
	public CostCategory save(CostCategory costCategory) {
		setCompany(costCategory);
		checkForUniqueName(costCategory);
		return checkSalaryStateAndSave(costCategory);
	}

	@Override
	public void update(CostCategory costCategory) {
		CostCategory editedCategory = costCategoryRepository.findOne(costCategory.getId());
		if (editedCategory != null) {
			editedCategory.setName(costCategory.getName());
			setCompany(costCategory);
		} else {
			throw new CostCategoryDataException("Вы пытаетесь обновить несуществующую категорию!");
		}

		checkSalaryStateAndSave(costCategory);
	}


	@Override
	public List<CostCategory> findAll() {
		return costCategoryRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public CostCategory find(Long id) {
		return costCategoryRepository.findOne(id);
	}

	@Override
	public CostCategory find(String name) {
		return costCategoryRepository.findByNameIgnoreCaseAndCompanyId(name, companyIdCache.getCompanyId());
	}

	@Override
	public List<CostCategory> findByNameStartingWith(String startName) {
		return costCategoryRepository.findByNameStartingWithAndCompanyId(startName, companyIdCache.getCompanyId());
	}

	@Override
	public void delete(Long id) {
		List<Cost> allCostsOnCategory = costService.findByCategoryNameAndVisibleIsTrueAndCompanyId(costCategoryRepository.findOne(id).getName(), companyIdCache.getCompanyId());
		if (allCostsOnCategory == null || allCostsOnCategory.size() == 0) {
			costCategoryRepository.delete(id);
		} else {
			throw new CostCategoryDataException("Остались не удаленные расходы на категории " + printAllCostOnCategory(allCostsOnCategory));
		}
	}

	@Override
	public CostCategory getSalaryCategory() {
		return costCategoryRepository.findByIsSalaryCostAndCompanyId(true, companyIdCache.getCompanyId());
	}

	@Override
	public boolean isAnyCostCategoryExist() {
		Long costCount = costCategoryRepository.countByCompanyId(companyIdCache.getCompanyId());
		return costCount > 0;
	}

	@Override
	public boolean isSalaryCostExist() {
		CostCategory category = getSalaryCategory();
		return category != null;
	}

	private String printAllCostOnCategory(List<Cost> costs) {
		if (costs.size() < 3) {
			return costs.stream().map(Cost::getName).collect(Collectors.joining("\n"));
		} else {
			String limitList = costs.stream().limit(2).map(Cost::getName).collect(Collectors.joining("\n"));
			return limitList + " и др.";
		}
	}

	private void checkForUniqueName(CostCategory costCategory) {
		CostCategory costCategoryInDataBase = find(costCategory.getName());
		if (costCategoryInDataBase != null) {
			throw new CostCategoryDataException("Категория с таким названием уже существует!");
		}
	}

	private CostCategory checkSalaryStateAndSave(CostCategory category) {
		if (category.isSalaryCost()) {
			CostCategory salaryCategory = getSalaryCategory();
			if (salaryCategory != null) {
				throw new CostCategoryDataException("Категория с именем: \"" + salaryCategory.getName() +
						"\" уже является зарплатной. Нельзя иметь две зарплатные категории!");
			}
			category.setSalaryCost(true);
		}

		return costCategoryRepository.save(category);
	}

}
