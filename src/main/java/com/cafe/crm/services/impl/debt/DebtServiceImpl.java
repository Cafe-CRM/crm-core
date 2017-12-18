package com.cafe.crm.services.impl.debt;


import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.debt.DebtRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class DebtServiceImpl implements DebtService {

	private final DebtRepository repository;
	private final ShiftService shiftService;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public DebtServiceImpl(DebtRepository repository, ShiftService shiftService, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.repository = repository;
		this.shiftService = shiftService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Debt debt) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		debt.setCompany(company);
	}

	@Override
	public void save(Debt debt) {
		if (debt.getDebtAmount() > 0) {
			setCompany(debt);
			repository.save(debt);
		} else {
			throw new DebtDataException("Введена недопустимая сумма долга");
		}
	}


	@Override
	public void delete(Debt debt) {
		repository.delete(debt);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public Debt get(Long id) {
		return repository.findOne(id);
	}

	@Override
	public List<Debt> getAll() {
		return repository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findByVisibleIsTrueAndDateBetween(LocalDate from, LocalDate to) {
		return repository.findByVisibleIsTrueAndDateBetweenAndCompanyId(from, to, companyIdCache.getCompanyId());
	}

	@Override
	public void offVisibleStatus(Debt debt) {
		debt.setVisible(false);
		repository.save(debt);
	}

	@Override
	public List<Debt> findByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to) {
		return repository.findByDebtorAndDateBetweenAndCompanyId(debtor, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public void repayDebt(Long id) {
		Shift lastShift = shiftService.getLast();
		Debt debt = repository.findOne(id);
		lastShift.addRepaidDebtToList(debt);
		Shift shiftWithDebt = debt.getShift();
		if (lastShift.equals(shiftWithDebt)) {
			shiftWithDebt.getGivenDebts().remove(debt);
		}
		shiftService.saveAndFlush(lastShift);
		offVisibleStatus(debt);
	}
}
