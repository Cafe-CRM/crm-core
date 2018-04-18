package com.cafe.crm.services.impl.company.configuration.step;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CostCategoryStep implements ConfigurationStep<List<CostCategory>> {

	private static final String NAME = "costCategory";

	private static final String ATTR_NAME = "categories";

	private static boolean isReconfigured = false;

	private final CostCategoryService costCategoryService;

	private final CompanyIdCache companyIdCache;

	private final CompanyService companyService;

	@Autowired
	public CostCategoryStep(CostCategoryService costCategoryService, CompanyIdCache companyIdCache, CompanyService companyService) {
		this.costCategoryService = costCategoryService;
		this.companyIdCache = companyIdCache;
		this.companyService = companyService;
	}


	@Override
	public void setIsReconfigured(boolean value) {
		isReconfigured = value;
	}

	@Override
	public boolean isIsReconfigured() {
		Company company = companyService.findOne(companyIdCache.getCompanyId());
		return company.isConfigured() || isReconfigured;

	}

	@Override
	public boolean isConfigured() {
		return costCategoryService.isSalaryCostExist();
	}

	@Override
	public String getStepName() {
		return NAME;
	}

	@Override
	public String getAttrName() {
		return ATTR_NAME;
	}

	@Override
	public List<CostCategory> getAttribute() {
		return costCategoryService.findAll();
	}
}
