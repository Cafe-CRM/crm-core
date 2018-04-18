package com.cafe.crm.services.impl.cost;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.cost.CostRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class CostServiceImpl implements CostService {

	private final CostRepository costRepository;
	private final CostCategoryService costCategoryService;
	private ShiftService shiftService;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public CostServiceImpl(CostRepository goodsRepository, CostCategoryService goodsCategoryService, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.costRepository = goodsRepository;
		this.costCategoryService = goodsCategoryService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompanyId(Cost cost) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		cost.setCompany(company);
	}

	@Autowired
	public void setShiftService(ShiftService shiftService) {
		this.shiftService = shiftService;
	}

	@Override
	public void save(Cost cost) {
		setCompanyId(cost);
		String categoryName = cost.getCategory().getName().trim();
		CostCategory categoryInDb = costCategoryService.find(categoryName);
		if (categoryInDb == null) {
			cost.getCategory().setName(categoryName);
			costCategoryService.save(cost.getCategory());
		} else {
			cost.setCategory(categoryInDb);
		}
		if (cost.getShift() == null) {
			Shift shift = shiftService.getLast();
			if (shift.isOpen()) {
				cost.setShift(shift);
			}
		}
		costRepository.save(cost);
	}

	@Override
	public void update(Cost cost) {
		if (cost.getId() != null && cost.getShift() == null) {
			Cost costInDb = costRepository.findOne(cost.getId());
			cost.setShift(costInDb.getShift());
		}
		save(cost);
	}

	@Override
	public void delete(Long id) {
		costRepository.delete(id);
	}

	@Override
	public void offVisibleStatus(Long id) {
		Cost cost = costRepository.getOne(id);
		cost.setVisible(false);
		costRepository.save(cost);
	}

	@Override
	public void offVisibleStatus(long[] ids) {
		List<Cost> costs = costRepository.findByIdIn(ids);
		costs.forEach(goods -> goods.setVisible(false));
		costRepository.save(costs);
	}

	@Override
	public List<Cost> findOtherCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findAllCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findAllCostByNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(name, categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findAllCostByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(name, categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByDateBetween(LocalDate from, LocalDate to) {
		return costRepository.findByVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findAllCostByDateBetween(LocalDate from, LocalDate to) {
		return costRepository.findByVisibleIsTrueAndDateBetweenAndCompanyId(from, to, companyIdCache.getCompanyId());
	}

	@Override
	public Set<Cost> findOtherCostByNameStartingWith(String startName) {
		return costRepository.findByNameStartingWithAndCompanyIdAndCategoryIsSalaryCostFalse(startName, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByDateAndVisibleTrue(LocalDate date) {
		return costRepository.findByDateAndVisibleTrueAndCompanyIdAndCategoryIsSalaryCostFalse(date, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByDateAndCategoryNameAndVisibleTrue(LocalDate date, String name) {
		return costRepository.findByDateAndCategoryNameAndVisibleTrueAndCompanyIdAndCategoryIsSalaryCostFalse(date, name, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByShiftIdAndCategoryNameNot(Long shiftId, String name) {
		return costRepository.findByShiftIdAndCategoryNameNotAndVisibleIsTrueAndCategoryIsSalaryCostFalse(shiftId, name);
	}

	@Override
	public List<Cost> findOtherCostByShiftId(Long shiftId) {
		return costRepository.findByShiftIdAndVisibleIsTrueAndCategoryIsSalaryCostFalse(shiftId);
	}

	@Override
	public List<Cost> findOtherCostByCategoryName(String name) {
		return costRepository.findByCategoryNameAndVisibleIsTrueAndCompanyIdAndCategoryIsSalaryCostFalse(name, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findSalaryCostAtShift(Long shiftId) {
		return costRepository.findByShiftIdAndCategoryIsSalaryCostTrue(shiftId);
	}

	@Override
	public List<Cost> findSalaryCostByDateBetween(LocalDate from, LocalDate to) {
		return costRepository.findByDateBetweenAndCategoryIsSalaryCostTrue(from, to);
	}


}
