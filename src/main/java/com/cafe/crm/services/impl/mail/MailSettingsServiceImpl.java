package com.cafe.crm.services.impl.mail;


import com.cafe.crm.models.mail.MailSettings;
import com.cafe.crm.repositories.mail.MailSettingsRepository;
import com.cafe.crm.services.interfaces.mail.MailSettingsService;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailSettingsServiceImpl implements MailSettingsService {

	private final MailSettingsRepository repository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public MailSettingsServiceImpl(MailSettingsRepository repository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.repository = repository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(MailSettings mailSettings) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		mailSettings.setCompany(company);
	}

	@Override
	public void save(MailSettings settings) {
		setCompany(settings);
		repository.save(settings);
	}

	@Override
	public void delete(MailSettings settings) {
		repository.delete(settings);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public MailSettings get(Long id) {
		return repository.findOne(id);
	}

	@Override
	public MailSettings findByEmail(String email) {
		return repository.findByEmailIgnoreCaseAndCompanyId(email, companyIdCache.getCompanyId());
	}

	@Override
	public List<MailSettings> getAll() {
		return repository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public boolean isExist() {
		Long count = repository.countByCompanyId(companyIdCache.getCompanyId());
		return count > 0L;
	}

}
