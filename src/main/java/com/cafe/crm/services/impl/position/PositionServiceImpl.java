package com.cafe.crm.services.impl.position;

import com.cafe.crm.exceptions.user.PositionDataException;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.position.PositionRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

	private final PositionRepository positionRepository;
	private final UserService userService;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public PositionServiceImpl(PositionRepository positionRepository, UserService userService, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.positionRepository = positionRepository;
		this.userService = userService;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Position position) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		position.setCompany(company);
	}

	@Override
	public void save(Position position) {
		setCompany(position);
		checkForUniqueName(position);
		positionRepository.saveAndFlush(position);
	}

	@Override
	public List<Position> findAll() {
		return positionRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public List<Position> findAllWithEnabledPercent() {
		return positionRepository.findByIsPositionUsePercentOfSalesIsTrueAndCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public void update(Position position) {
		checkForNotNew(position);
		checkForUniqueName(position);
		setCompany(position);
		positionRepository.saveAndFlush(position);
	}

	@Override
	public void delete(Long id) {
		Position position = positionRepository.findOne(id);
		List<User> users = userService.findByPositionIdWithAnyEnabledStatus(id);
		for (User user : users) {
			user.getPositions().remove(position);
			List<Position> newUserPositionList = user.getPositions();
			user.setPositions(newUserPositionList);
			userService.save(user);
		}
		positionRepository.delete(id);
	}

	@Override
	public Position findByName(String name) {
		return positionRepository.findByNameAndCompanyId(name, companyIdCache.getCompanyId());
	}

	@Override
	public List<Position> findByIdIn(Long[] ids) {
		return positionRepository.findByIdIn(ids);
	}

	@Override
	public Position findById(Long id) {
		return positionRepository.findOne(id);
	}

	private void checkForNotNew(Position position) {
		if (position.getId() == null) {
			throw new PositionDataException("Не был передан Id пользователя!");
		}
	}

	private void checkForUniqueName(Position position) {
		Position positionInDatabase = findByName(position.getName());
		if ((positionInDatabase != null) && !positionInDatabase.getId().equals(position.getId())) {
			throw new PositionDataException("Должность с таким названием уже существует!");
		}
	}
}
