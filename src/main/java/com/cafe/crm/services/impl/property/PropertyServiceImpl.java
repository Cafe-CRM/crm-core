package com.cafe.crm.services.impl.property;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.repositories.property.PropertyRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

	private final PropertyRepository propertyRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;
	private final PriceNameProperties priceNameProperties;

	@Autowired
	public PropertyServiceImpl(PropertyRepository propertyRepository, CompanyService companyService, CompanyIdCache companyIdCache, PriceNameProperties priceNameProperties) {
		this.propertyRepository = propertyRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
		this.priceNameProperties = priceNameProperties;
	}

	private void setCompanyId(Property property) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		property.setCompany(company);
	}

	@Override
	public void save(Property property) {
		setCompanyId(property);
		propertyRepository.saveAndFlush(property);
	}

	@Override
	public List<Property> findAll() {
		return propertyRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public Property findByName(String name) {
		return propertyRepository.findByNameAndCompanyId(name, companyIdCache.getCompanyId());
	}

	@Override
	public void delete(Long id) {
		propertyRepository.delete(id);
	}

	@Override
	public void saveCollection(List<Property> properties) {
		for (Property property : properties){
			setCompanyId(property);
		}
			propertyRepository.save(properties);
	}

	@Override
	public List<Property> findByNameIn(String... name) {
		return propertyRepository.findByNameIn(name);
	}

}
