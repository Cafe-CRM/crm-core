package com.cafe.crm.services.interfaces.company;


import com.cafe.crm.models.company.Company;

public interface CompanyService {
	void save(Company company);

	Company findOne(Long id);
}