package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfirmStep implements ConfigurationStep<String> {

	private static final String NAME = "confirm";

	private static final String ATTR_NAME = "name";

	private static final int PRIORITY = 200;

	private final CompanyIdCache companyIdCache;

	private final CompanyService companyService;

	@Autowired
	public ConfirmStep(CompanyIdCache companyIdCache, CompanyService companyService) {
		this.companyIdCache = companyIdCache;
		this.companyService = companyService;
	}

	@Override
	public boolean isConfigured() {
		Company company = companyService.findOne(companyIdCache.getCompanyId());
		return company.isConfigured();
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
	public String getAttribute() {
		Company company = companyService.findOne(companyIdCache.getCompanyId());
		return company.getName();
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}
}
