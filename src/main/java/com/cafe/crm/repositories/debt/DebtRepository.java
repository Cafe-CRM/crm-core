package com.cafe.crm.repositories.debt;


import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

	//Все долги компании
	List<Debt> findByCompanyId(Long companyId);

	//Все долги со счёта
	List<Debt> findByCalculateId(Long calculateId);

	//Все долги из кассы по датам
	List<Debt> findByDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(LocalDate from, LocalDate to, Long companyId);

	//Все обычные долги по датам
	List<Debt> findByDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(LocalDate from, LocalDate to, Long companyId);

	//Все долги по датам
	List<Debt> findByDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	//Все долги по должнику и датам
	List<Debt> findByDebtorAndDateBetweenAndCompanyId(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все обычные долги по должнику и датам
	List<Debt> findByDebtorAndDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все долги из кассы по должнику и датам
	List<Debt> findByDebtorAndDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все взятые долги на смене
	List<Debt> findByGivenShiftAndCompanyId(Shift givenShift, Long companyId);

	//Все возвращённые долги на смене
	List<Debt> findByRepaidShiftAndCompanyId(Shift repairedShift, Long companyId);

	//Все взятые долги за смены
	List<Debt> findByGivenShiftInAndCompanyId(Collection<Shift> shifts, Long companyId);

	//Все возвращённые долги за смены
	List<Debt> findByRepaidShiftInAndCompanyId(Collection<Shift> shifts, Long companyId);
}
