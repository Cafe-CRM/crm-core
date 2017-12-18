package com.cafe.crm.services.impl.discount;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.discount.Discount;
import com.cafe.crm.repositories.discount.DiscountRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.discount.DiscountService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

	private final DiscountRepository discountRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public DiscountServiceImpl(DiscountRepository discountRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.discountRepository = discountRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompanyId(Discount discount) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		discount.setCompany(company);
	}

	@Override
	public void save(Discount discount) {
		setCompanyId(discount);
		discountRepository.save(discount);
	}

	@Override
	public void delete(Discount discount) {
		discountRepository.delete(discount);
	}

	@Override
	public List<Discount> getAll() {
		return discountRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public Discount getOne(Long id) {
		return discountRepository.findOne(id);
	}

	@Override
	public void deleteById(Long id) {
		discountRepository.delete(id);
	}

	@Override
	public List<Discount> getAllOpen() {
		return discountRepository.getAllOpenAndCompanyId(companyIdCache.getCompanyId());
	}
}

