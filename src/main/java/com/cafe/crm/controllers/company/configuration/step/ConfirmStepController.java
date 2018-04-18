package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.services.impl.company.configuration.step.ConfirmStep;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/company/configuration/step/confirm")
public class ConfirmStepController {
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;
	private final ConfirmStep confirmStep;

	@Autowired
	public ConfirmStepController(CompanyService companyService, CompanyIdCache companyIdCache, ConfirmStep confirmStep) {
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
		this.confirmStep = confirmStep;
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity<?> deleteBoard() {
		Company company = companyService.findOne(companyIdCache.getCompanyId());
		company.setConfigured(true);
		companyService.save(company);
		return ResponseEntity.ok("");
	}
}
