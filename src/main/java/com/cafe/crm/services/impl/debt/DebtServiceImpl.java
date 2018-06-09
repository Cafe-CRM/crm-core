package com.cafe.crm.services.impl.debt;


import com.cafe.crm.dto.DebtDTO;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.debt.DebtRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.CompanyIdCache;
import com.yc.easytransformer.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class DebtServiceImpl implements DebtService {

	private final DebtRepository repository;
	private final ShiftService shiftService;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;
	private final Transformer transformer;

	@Autowired
	public DebtServiceImpl(DebtRepository repository, ShiftService shiftService, CompanyService companyService,
						   CompanyIdCache companyIdCache, Transformer transformer) {
		this.repository = repository;
		this.shiftService = shiftService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
		this.transformer = transformer;
	}

	private void setCompany(Debt debt) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		debt.setCompany(company);
	}

	@Override
	public Debt save(Debt debt) {
		if (debt.getDebtAmount() > 0) {
			setCompany(debt);
			return repository.save(debt);
		} else {
			throw new DebtDataException("Введена недопустимая сумма долга");
		}
	}

	@Override
	public void delete(Debt debt) {
		Shift lastShift = shiftService.getLast();
		debt.setDeleted(true);
		debt.setDeletedShift(lastShift);
		repository.save(debt);
	}

	@Override
	public void delete(Iterable<? extends Debt> debts) {
		repository.delete(debts);
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
		return repository.findByDeletedIsFalseAndCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findOtherDebtByRepaidIsFalseAndDateBetween(LocalDate from, LocalDate to) {
		return repository.findByDeletedIsFalseAndRepaidIsFalseAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findCashBoxDebtByRepaidIsFalseAndDateBetween(LocalDate from, LocalDate to) {
		return repository.findByDeletedIsFalseAndRepaidIsFalseAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findOtherDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to) {
		return repository.findByDeletedIsFalseAndDebtorAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(debtor, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findCashBoxDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to) {
		return repository.findByDeletedIsFalseAndDebtorAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(debtor, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findGivenDebtThatHaveBeenRemovedOnAnotherShifts(Shift thatShift) {
		return repository.findByDeletedIsTrueAndDeletedShiftIsNotAndGivenShiftAndCompanyIdAndCashBoxDebtIsFalse(thatShift, thatShift, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findGivenDebtThatHaveBeenRemovedOnAnotherShifts(Iterable<? extends Shift> thatShifts) {
		return repository.findByDeletedIsTrueAndDeletedShiftNotInAndGivenShiftInAndCompanyIdAndCashBoxDebtIsFalse(thatShifts, thatShifts, companyIdCache.getCompanyId());
	}

	@Override
	public Debt repayDebt(Long id) {
		Shift lastShift = shiftService.getLast();
		Debt debt = repository.findOne(id);
		debt.setRepaired(true);
		debt.setRepaidDate(lastShift.getShiftDate());
		debt.setRepairedShift(lastShift);
		save(debt);
		return debt;
	}

	@Override
	public List<Debt> findByCalculateId(Long calculateId) {
		return repository.findByDeletedIsFalseAndCalculateId(calculateId);
	}

	@Override
	public List<DebtDTO> transformDebtsWithOutShiftAndCalc(List<Debt> debts) {
		List<DebtDTO> debtsDTOS = new ArrayList<>(debts.size());

		for (Debt debt : debts) {
			DebtDTO dto = transformer.transform(debt, DebtDTO.class);
			dto.setGivenDate(debt.getGivenDate());
			dto.setRepaidDate(debt.getRepaidDate());
			debtsDTOS.add(dto);
		}

		return debtsDTOS;
	}

	@Override
	public Debt addDebtOnLastShift(Debt debt) {
		Shift lastShift = shiftService.getLast();
		debt.setGivenShift(lastShift);
		return save(debt);
	}

	@Override
	public List<Debt> findGivenDebtsByShift(Shift shifts) {
		return repository.findByDeletedIsFalseAndGivenShiftAndCompanyId(shifts, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findRepaidDebtsByShift(Shift shifts) {
		return repository.findByDeletedIsFalseAndRepaidShiftAndCompanyId(shifts, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findGivenDebtsByShifts(Iterable<? extends Shift> shifts) {
		return repository.findByDeletedIsFalseAndGivenShiftInAndCompanyId(shifts, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findRepaidDebtsByShifts(Iterable<? extends Shift> shifts) {
		return repository.findByDeletedIsFalseAndRepaidShiftInAndCompanyId(shifts, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findAllGivenOtherDebt(Shift shifts) {
		return repository.findByDeletedIsFalseAndGivenShiftAndCashBoxDebtIsFalseAndCompanyId(shifts, companyIdCache.getCompanyId());
	}

	@Override
	public List<Debt> findAllGivenCashBoxDebt(Shift shifts) {
		return repository.findByDeletedIsFalseAndGivenShiftAndCashBoxDebtIsTrueAndCompanyId(shifts, companyIdCache.getCompanyId());
	}
}
