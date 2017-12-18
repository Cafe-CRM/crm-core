package com.cafe.crm.services.impl.note;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.note.NoteRecord;
import com.cafe.crm.repositories.note.NoteRecordRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.note.NoteRecordService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteRecordServiceImpl implements NoteRecordService {

	private final NoteRecordRepository noteRecordRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public NoteRecordServiceImpl(NoteRecordRepository noteRecordRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.noteRecordRepository = noteRecordRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(NoteRecord noteRecord) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		noteRecord.setCompany(company);
	}

	@Override
	public NoteRecord save(NoteRecord noteRecord) {
		setCompany(noteRecord);
		return noteRecordRepository.save(noteRecord);
	}
}
