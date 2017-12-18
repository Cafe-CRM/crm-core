package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.calculate.CalculateRepository;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateServiceImpl implements CalculateService {

	private final CalculateRepository calculateRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public CalculateServiceImpl(CalculateRepository calculateRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.calculateRepository = calculateRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Calculate calculate) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		calculate.setCompany(company);
	}

	@Override
	public void save(Calculate calculate) {
		setCompany(calculate);
		calculateRepository.save(calculate);
	}

	@Override
	public void delete(Calculate calculate) {
		calculateRepository.delete(calculate);
	}

	@Override
	public List<Calculate> getAll() {
		return calculateRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public Calculate getOne(Long id) {
		return calculateRepository.findOne(id);
	}

	@Override
	public List<Calculate> getAllOpen() {
		return calculateRepository.getAllOpenAndCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public Calculate getAllOpenOnCalculate(Long calculateId) {
		return calculateRepository.getAllOpenOnCalculateAndCompanyId(calculateId, companyIdCache.getCompanyId());
	}

	@Override
	public Calculate findByClientId(Long clientId) {
		return calculateRepository.findByClientId(clientId);
	}

}
