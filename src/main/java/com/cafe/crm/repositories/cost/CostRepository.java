package com.cafe.crm.repositories.cost;

import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import com.cafe.crm.repositories.customRepository.DateableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostRepository extends JpaRepository<Cost, Long> {
	//Найти все расходы по названию категории в промежутке дат
	List<Cost> findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	//Найти все прочие расходы по названию категории
	List<Cost> findByCategoryNameAndVisibleIsTrueAndCompanyIdAndCategoryIsSalaryCostFalse(String name, Long companyId);

	//Найти все расходы по названию в промежутке дат
	List<Cost> findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	//Найти все расходы по названию, по названию категории в промежутке дат
	List<Cost> findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, String categoryName, LocalDate from, LocalDate to, Long companyId);

	//Найти все прочие расходы в промежутке дат
	List<Cost> findByVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(LocalDate from, LocalDate to, Long companyId);

	//Найти все расходы в промежутке дат
	List<Cost> findByVisibleIsTrueAndDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	//Найти все прочие расходы по началу названия todo Зачем оно нужно?
	Set<Cost> findByNameStartingWithAndCompanyIdAndCategoryIsSalaryCostFalse(String startName, Long companyId);

	//Найти все расходы по ids
	List<Cost> findByIdIn(long[] ids);

	//Найти все прочие расходы за смену
	List<Cost> findByShiftAndVisibleIsTrueAndCategoryIsSalaryCostFalse(Shift shift);

	//Найти все зарплатные расходы в промежутке дат
	List<Cost> findByDateBetweenAndCategoryIsSalaryCostTrue(LocalDate from, LocalDate to);

	//Найти все зарплатные расходы за смену
	List<Cost> findByShiftAndCategoryIsSalaryCostTrue(Shift shift);

	//Найти все прочие расходы за смену
	List<Cost> findByShiftAndCategoryIsSalaryCostFalse(Shift shift);

	//Удалить все расходы за смену
	void deleteAllByShiftAndCompanyId(Shift shift, Long companyId);

	//Удалить все расходы за смены
	void deleteAllByShiftIdInAndCompanyId(long[] ids, Long companyId);
}
