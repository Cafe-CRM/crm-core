package com.cafe.crm.services.impl.checklist;

import com.cafe.crm.exceptions.checklist.ChecklistDataException;
import com.cafe.crm.models.checklist.Checklist;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.checklist.ChecklistRepository;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChecklistServiceIml implements ChecklistService{

	private final ChecklistRepository checklistRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public ChecklistServiceIml(ChecklistRepository checklistRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.checklistRepository = checklistRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Checklist checklist) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		checklist.setCompany(company);
	}

	@Override
	public Checklist find(Long id) {
		return checklistRepository.findOne(id);
	}

	@Override
	public Checklist saveChecklistOnCloseShift(String text) {
		Checklist checklist = new Checklist();
		checklist.setOnCloseShiftText(text);
		setCompany(checklist);
		return checklistRepository.save(checklist);
	}

	@Override
	public Checklist saveChecklistOnOpenShift(String text) {
		Checklist checklist = new Checklist();
		checklist.setOnOpenShiftText(text);
		setCompany(checklist);
		return checklistRepository.save(checklist);
	}

	@Override
	public Checklist update(Checklist checklist) {
		Checklist checklistFromDB = checklistRepository.findOne(checklist.getId());
		Checklist presentCheckList;

		if (checklistFromDB != null) {
			setCompany(checklist);
			presentCheckList = checklistRepository.save(checklist);
		} else {
			throw new ChecklistDataException("Не найден указанный параметр");
		}

		return presentCheckList;
	}

	@Override
	public void delete(Long id) {
		checklistRepository.delete(id);
	}

	@Override
	public List<Checklist> getAll() {
		return checklistRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public List<Checklist> getAllForOpenShift() {
		return checklistRepository.getAllForOpenShiftAndCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public List<Checklist> getAllForCloseShift() {
		return checklistRepository.getAllForCloseShiftAndCompanyId(companyIdCache.getCompanyId());
	}


}
