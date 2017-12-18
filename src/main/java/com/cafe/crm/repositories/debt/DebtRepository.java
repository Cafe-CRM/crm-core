package com.cafe.crm.repositories.debt;


import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

	List<Debt> findByVisibleIsTrueAndDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	List<Debt> findByDebtorAndDateBetweenAndCompanyId(String debtor, LocalDate from, LocalDate to, Long companyId);

	List<Debt> findByCompanyId(Long companyId);
}
