package com.cafe.crm.repositories.debt;


import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

	//Все долги компании
	List<Debt> findByDeletedIsFalseAndCompanyId(Long companyId);

	//Все долги со счёта
	List<Debt> findByDeletedIsFalseAndCalculateId(Long calculateId);

	//Все долги из кассы по датам
	List<Debt> findByDeletedIsFalseAndRepaidIsFalseAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(LocalDate from, LocalDate to, Long companyId);

	//Все не возвращённые обычные долги по датам
	List<Debt> findByDeletedIsFalseAndRepaidIsFalseAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(LocalDate from, LocalDate to, Long companyId);

	//Все долги по датам
	List<Debt> findByDeletedIsFalseAndGivenDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	//Все долги по должнику и датам
	List<Debt> findByDeletedIsFalseAndDebtorAndGivenDateBetweenAndCompanyId(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все обычные долги по должнику и датам
	List<Debt> findByDeletedIsFalseAndDebtorAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsFalse(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все долги из кассы по должнику и датам
	List<Debt> findByDeletedIsFalseAndDebtorAndGivenDateBetweenAndCompanyIdAndCashBoxDebtIsTrue(String debtor, LocalDate from, LocalDate to, Long companyId);

	//Все взятые долги на смене
	List<Debt> findByDeletedIsFalseAndGivenShiftAndCompanyId(Shift givenShift, Long companyId);

	//Все возвращённые долги на смене
	List<Debt> findByDeletedIsFalseAndRepaidShiftAndCompanyId(Shift repairedShift, Long companyId);

	//Все взятые долги за смены
	List<Debt> findByDeletedIsFalseAndGivenShiftInAndCompanyId(Iterable<? extends Shift> shifts, Long companyId);

	//Все возвращённые долги за смены
	List<Debt> findByDeletedIsFalseAndRepaidShiftInAndCompanyId(Iterable<? extends Shift> shifts, Long companyId);

	//Все обычные долги, взятые на этой смене, и удалённые на любых других
	List<Debt> findByDeletedIsTrueAndDeletedShiftIsNotAndGivenShiftAndCompanyIdAndCashBoxDebtIsFalse(Shift deletedShift, Shift givenShift, Long companyId);

	//Все обычные долги, взятые на этих сменах, и удалённые на любых других
	List<Debt> findByDeletedIsTrueAndDeletedShiftNotInAndGivenShiftInAndCompanyIdAndCashBoxDebtIsFalse(Iterable<? extends Shift> deletedShift, Iterable<? extends Shift> givenShift, Long companyId);

	//Все обычные долги, взятые на этой смене
	List<Debt> findByDeletedIsFalseAndGivenShiftAndCashBoxDebtIsFalseAndCompanyId(Shift givenShift, Long companyId);

	//Все долги из кассы, взятые на этой смене
	List<Debt> findByDeletedIsFalseAndGivenShiftAndCashBoxDebtIsTrueAndCompanyId(Shift givenShift, Long companyId);
}
