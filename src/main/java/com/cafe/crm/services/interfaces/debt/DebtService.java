package com.cafe.crm.services.interfaces.debt;


import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DebtService {

	Debt save(Debt debt);

	void saveAll(Set<Debt> debts);

	void delete(Debt debt);

	void delete(Long id);

	Debt get(Long id);

	List<Debt> getAll();

	List<Debt> findByVisibleIsTrueAndDateBetween(LocalDate from, LocalDate to);

	void offVisibleStatus(Debt debt);

	List<Debt> findByDebtorAndDateBetween(String debtor, LocalDate from, LocalDate to);

	Debt repayDebt(Long id);
}
