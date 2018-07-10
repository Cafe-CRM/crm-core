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

	void delete(Debt debt);

	void delete(Long id);

	void delete(Iterable<? extends Debt> debts);

	Debt get(Long id);

	List<Debt> getAll();

	List<Debt> findOtherDebtByRepaidIsFalseAndDateBetween(LocalDate from, LocalDate to);

	List<Debt> findCashBoxDebtByRepaidIsFalseAndDateBetween(LocalDate from, LocalDate to);

	List<Debt> findOtherDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	List<Debt> findCashBoxDebtByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	List<Debt> findGivenDebtThatHaveBeenRemovedOnAnotherShifts(Shift thatShift);

	List<Debt> findGivenDebtThatHaveBeenRemovedOnAnotherShifts(Iterable<? extends Shift> thatShifts);

	List<Debt> findByCalculateId(Long calculateId);

	List<DebtDTO> transformDebtsWithOutShiftAndCalc(List<Debt> debts);

	Debt repayDebt(Long id);

	Debt addDebtOnLastShift(Debt debt);

	List<Debt> findGivenDebtsByShift(Shift shifts);

	List<Debt> findRepaidDebtsByShift(Shift shifts);

    List<Debt> findRepaidDebtsByShiftIdIn(long[] ids);

	List<Debt> findGivenDebtsByShifts(Iterable<? extends Shift> shifts);

	List<Debt> findRepaidDebtsByShifts(Iterable<? extends Shift> shifts);

	List<Debt> findAllGivenOtherDebt(Shift shifts);

	List<Debt> findAllGivenCashBoxDebt(Shift shifts);

	List<Debt> findAllDeletedDebtsByShift(Shift shift);

    List<Debt> findAllDeletedDebtsByShiftIdIn(long[] ids);

	void deleteByGivenShift(Shift shift);

	void deleteByGivenShiftIdIn(long[] ids);
}
