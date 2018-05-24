package com.cafe.crm.services.interfaces.debt;


import com.cafe.crm.dto.DebtDTO;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DebtService {

	Debt save(Debt debt);

	void saveAll(List<Debt> debts);

	void delete(Debt debt);

	void delete(Long id);

	Debt get(Long id);

	List<Debt> getAll();

	List<Debt> findOtherDebtByVisibleIsTrueAndDateBetween(LocalDate from, LocalDate to);

	List<Debt> findCashBoxDebtByVisibleIsTrueAndDateBetween(LocalDate from, LocalDate to);

	List<Debt> findOtherDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	List<Debt> findCashBoxDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	List<Debt> findByCalculateId(Long calculateId);

	List<DebtDTO> transformDebtsWithOutShiftAndCalc(List<Debt> debts);

	Debt repayDebt(Long id);

	Debt addDebtOnLastShift(Debt debt);

	List<Debt> findGivenDebtsByShift(Shift shifts);

	List<Debt> findRepaidDebtsByShift(Shift shifts);

	List<Debt> findGivenDebtsByShifts(Collection<Shift> shifts);

	List<Debt> findRepaidDebtsByShifts(Collection<Shift> shifts);
}
