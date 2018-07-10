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
import org.springframework.context.annotation.Lazy;
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
	public CostServiceImpl(CostRepository goodsRepository,
						   CostCategoryService goodsCategoryService,
						   CompanyService companyService,
						   @Lazy ShiftService shiftService,
						   CompanyIdCache companyIdCache) {
		this.costRepository = goodsRepository;
		this.costCategoryService = goodsCategoryService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
		this.shiftService = shiftService;
	}

	private void setCompanyId(Cost cost) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		cost.setCompany(company);
	}

	/*@Autowired
	public void setShiftService(ShiftService shiftService) {
		this.shiftService = shiftService;
	}*/

	@Override
	public Cost save(Cost cost) {
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
		return costRepository.save(cost);
	}

	@Override
	public Cost getOne(Long costId) {
		return costRepository.getOne(costId);
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
	public List<Cost> offVisibleStatus(long[] ids) {
		List<Cost> costs = costRepository.findByIdIn(ids);
		costs.forEach(goods -> goods.setVisible(false));
		return costRepository.save(costs);
	}

	@Override
	public List<Cost> findAllCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(categoryName, from, to, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findAllCostByNameAndDateBetween(String categoryName, LocalDate from, LocalDate to) {
		return costRepository.findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(categoryName, from, to, companyIdCache.getCompanyId());
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
		//return costRepository.findAllBySelfDateBetween(from, to);
		//return null;
	}

	@Override
	public Set<Cost> findOtherCostByNameStartingWith(String startName) {
		return costRepository.findByNameStartingWithAndCompanyIdAndCategoryIsSalaryCostFalse(startName, companyIdCache.getCompanyId());
	}

	@Override
	public List<Cost> findOtherCostByShiftId(Shift shift) {
		return costRepository.findByShiftAndVisibleIsTrueAndCategoryIsSalaryCostFalse(shift);
	}

	@Override
	public List<Cost> findSalaryCostAtShift(Shift shift) {
		return costRepository.findByShiftAndCategoryIsSalaryCostTrue(shift);
	}

	@Override
	public List<Cost> findOtherCostAtShift(Shift shift) {
		return costRepository.findByShiftAndCategoryIsSalaryCostFalse(shift);
	}

	@Override
	public List<Cost> findSalaryCostByDateBetween(LocalDate from, LocalDate to) {
		return costRepository.findByDateBetweenAndCategoryIsSalaryCostTrue(from, to);
	}

	@Override
	public void deleteAllCostByShift(Shift shift) {
		costRepository.deleteAllByShiftAndCompanyId(shift, companyIdCache.getCompanyId());
	}

	@Override
	public void deleteAllCostByShiftIdIn(long[] ids) {
		costRepository.deleteAllByShiftIdInAndCompanyId(ids, companyIdCache.getCompanyId());
	}
}
