package com.cafe.crm.services.interfaces.debt;


import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;

import java.time.LocalDate;
import java.util.List;

public interface DebtService {

	void save(Debt debt);

	void delete(Debt debt);

	void delete(Long id);

	Debt get(Long id);

	List<Debt> getAll();

	List<Debt> findByVisibleIsTrueAndDateBetween(LocalDate from, LocalDate to);

	void offVisibleStatus(Debt debt);

	List<Debt> findByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	void repayDebt(Long id);
}
