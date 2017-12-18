package com.cafe.crm.services.impl.company;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.company.CompanyRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyRepository companyRepository;

	@Autowired
	public CompanyServiceImpl(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@Override
	public void save(Company company) {
		companyRepository.save(company);
	}

	@Override
	public Company findOne(Long id) {
		return companyRepository.findOne(id);
	}
}