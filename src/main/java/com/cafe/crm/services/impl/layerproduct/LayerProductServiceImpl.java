package com.cafe.crm.services.impl.layerproduct;


import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.layerproduct.LayerProductRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LayerProductServiceImpl implements LayerProductService {

	private final LayerProductRepository layerProductRepository;
	private final CompanyIdCache companyIdCache;
	private final CompanyService companyService;

	@Autowired
	public LayerProductServiceImpl(LayerProductRepository layerProductRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.layerProductRepository = layerProductRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(LayerProduct layerProduct){
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		layerProduct.setCompany(company);
	}

	@Override
	public void save(LayerProduct layerProduct) {
		setCompany(layerProduct);
		layerProductRepository.saveAndFlush(layerProduct);
	}

	@Override
	public void delete(LayerProduct layerProduct) {
		layerProductRepository.delete(layerProduct);
	}

	@Override
	public List<LayerProduct> getAll() {
		return layerProductRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public LayerProduct getOne(Long id) {
		return layerProductRepository.findOne(id);
	}

}
