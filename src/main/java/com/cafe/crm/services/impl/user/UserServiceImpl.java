package com.cafe.crm.services.impl.user;


import com.cafe.crm.dto.ExtraUserData;
import com.cafe.crm.exceptions.user.UserDataException;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.user.UserRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.cafe.crm.services.interfaces.role.RoleService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private RoleService roleService;
	private final PasswordEncoder passwordEncoder;
	private PositionService positionService;
	private final CompanyService companyService;
	private CompanyIdCache companyIdCache;
	private final CacheManager cacheManager;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Value("${user.default-password}")
	private String defaultPassword;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService, CacheManager cacheManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.companyService = companyService;
		this.cacheManager = cacheManager;
	}

	@Autowired
	public void setCompanyIdCache(CompanyIdCache companyIdCache) {
		this.companyIdCache = companyIdCache;
	}

	@Autowired
	public void setPositionService(PositionService positionService) {
		this.positionService = positionService;
	}

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	private void setCompany(User user) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		user.setCompany(company);
	}

	@Override
	public void save(User user) {
		setCompany(user);
		userRepository.saveAndFlush(user);
	}

	@Override
	public void save(User user, String positionsIds, String rolesIds, String isDefaultPassword) {
		cacheManager.getCache("user").evict(user.getEmail());
		checkForUniqueEmailAndPhone(user);
		setPositionsToUser(user, positionsIds);
		setRolesToUser(user, rolesIds);
		setPasswordToUser(user, isDefaultPassword);
		setCompany(user);
		userRepository.saveAndFlush(user);
		cacheManager.getCache("user").put(user.getEmail(), user);
	}

	@Override
	public void saveNewUser(User user) {
		checkForUniqueEmailAndPhone(user);
		companyService.save(user.getCompany());
		userRepository.saveAndFlush(user);
		cacheManager.getCache("user").clear();
	}

	@Override
	public List<User> findAll() {
		return userRepository.findByEnabledIsTrueAndCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmailAndEnabledIsTrueAndCompanyId(email, companyIdCache.getCompanyId());
	}

	@Override
	public User findByPhone(String phone) {
		return userRepository.findByPhoneAndEnabledIsTrueAndCompanyId(phone, companyIdCache.getCompanyId());
	}

	@Override
	public List<User> findByPositionIdAndOrderByLastName(Long positionId) {
		return userRepository.findByPositionsIdAndEnabledIsTrueAndCompanyId(positionId, companyIdCache.getCompanyId());
	}

	@Override
	public List<User> findByRoleIdAndOrderByLastName(Long roleId) {
		return userRepository.findByRolesIdAndEnabledIsTrue(roleId);
	}

	@Override
	public List<User> findByPositionIdWithAnyEnabledStatus(Long positionId) {
		return userRepository.findByPositionsIdAndCompanyId(positionId, companyIdCache.getCompanyId());
	}

	@Override
	public List<User> findByRoleIdWithAnyEnabledStatus(Long roleId) {
		return userRepository.findByRolesIdAndEnabledIsTrue(roleId);
	}

	@Override
	public List<User> findByRoleName(String roleName) {
		return userRepository.findByRolesNameAndCompanyId(roleName, companyIdCache.getCompanyId());
	}

	@Override
	public Map<Position, List<User>> findAndSortUserByPosition() {
		List<Position> allPositions = positionService.findAll();
		Map<Position, List<User>> usersByPositions = new HashMap<>();
		for (Position position : allPositions) {
			List<User> users = findByPositionIdAndOrderByLastName(position.getId());
			usersByPositions.put(position, users);
		}
		return usersByPositions;
	}

	@Override
	public Map<Role, List<User>> findAndSortUserByRoleWithSupervisor() {
		List<Role> allRoles = roleService.findAllWithSupervisor();
		Map<Role, List<User>> usersByRoles = new HashMap<>();
		for (Role role : allRoles) {
			List<User> users = findByRoleIdAndOrderByLastName(role.getId());
			usersByRoles.put(role, users);
		}
		return usersByRoles;
	}

	@Override
	public void update(User user, ExtraUserData extraUserData) {
		if (!isAuthenticationPassed(extraUserData.getBossPassword(), extraUserData.isBossPasswordRequired())) {
			throw new UserDataException("Неверный пароль для подтверждения изменений");
		}
		checkForNotNew(user);
		checkForUniqueEmailAndPhone(user);
		if (isValidPasswordsData(user, extraUserData.getOldPassword(), extraUserData.getNewPassword(),
				extraUserData.getRepeatedPassword())) {
			user.setPassword(passwordEncoder.encode(extraUserData.getNewPassword()));
		}
		setCompanyById(user, extraUserData.getCompanyId());
		setPositionsToUser(user, extraUserData.getPositionsIds());
		setRolesToUser(user, extraUserData.getRolesIds());
		setDataFromDatabaseToUser(user);
		updateSessionRegistryAndCache(user);
		userRepository.saveAndFlush(user);
	}

	private boolean isAuthenticationPassed(String bossPassword, boolean authRequired) {
		String bossName = SecurityContextHolder.getContext().getAuthentication().getName();
		User bossUser = findByUsername(bossName);
		String bossDbPassword = bossUser.getPassword();
		return (!authRequired || (passwordEncoder.matches(bossPassword, bossDbPassword)));
	}

	private void setCompanyById(User user, String companyId) {
		Long longCompanyId = Long.parseLong(companyId);
		Company company = companyService.findOne(longCompanyId);
		user.setCompany(company);

	}

	private void updateSessionRegistryAndCache(User updatedUser) {
		User storedUser = findById(updatedUser.getId());

		cacheManager.getCache("user").evict(storedUser.getEmail());
		cacheManager.getCache("user").put(updatedUser.getEmail(), updatedUser);

		String userEmailFromSession;
		if (criticalUserDataUpdated(updatedUser, storedUser)) {
			for (Object principal : sessionRegistry.getAllPrincipals()) {
				userEmailFromSession = ((UserDetails) principal).getUsername();
				if (storedUser.getEmail().equals(userEmailFromSession)) {
					sessionRegistry.getAllSessions(principal, false).forEach(SessionInformation::expireNow);
				}
			}
		}
	}

	private boolean criticalUserDataUpdated(User updatedUser, User storedUser) {

		List<Position> storedUserPositions = storedUser.getPositions();
		List<Position> updatedUserPositions = updatedUser.getPositions();
		Set<Role> storedUserRoles = storedUser.getRoles();
		Set<Role> updatedUserRoles = updatedUser.getRoles();

		boolean positionsEqual = listsEqual(storedUserPositions, updatedUserPositions);
		boolean rolesEqual = setsEqual(storedUserRoles, updatedUserRoles);
		boolean passwordsEqual = storedUser.getPassword().equals(updatedUser.getPassword());
		boolean phonesEqual = storedUser.getPhone().equals(updatedUser.getPhone());
		boolean emailsEqual = storedUser.getEmail().equals(updatedUser.getEmail());

		return (!positionsEqual || !rolesEqual || !passwordsEqual || !phonesEqual || !emailsEqual);
	}

	private <T> boolean listsEqual(List<T> list1, List<T> list2) {
		return (isEmpty(list1) && isEmpty(list2)) || list1 != null && list2 != null && isEqualCollection(list1, list2);
	}

	private <T> boolean setsEqual(Set<T> set1, Set<T> set2) {
		return (isEmpty(set1) && isEmpty(set2)) || set1 != null && set2 != null && isEqualCollection(set1, set2);
	}

	@Override
	public List<User> findByEmailOrPhoneAndCompanyId(String email, String phone, Long companyId) {
		return userRepository.findByEmailOrPhoneAndCompanyId(email, phone, companyId);
	}

	@Override
	public List<User> findByEmailOrPhone(String email, String phone) {
		return userRepository.findByEmailOrPhone(email, phone);
	}

	@Override
	public void delete(Long id) {
		User user = userRepository.findOne(id);
		if (user != null) {
			user.setEnabled(false);
			userRepository.saveAndFlush(user);
		}
	}

	@Override
	public List<User> findByIdIn(long[] ids) {
		return userRepository.findByIdInAndCompanyId(ids, companyIdCache.getCompanyId());
	}

	@Override
	public void changePassword(String username, String oldPassword, String newPassword, String repeatedPassword) {
		User user = findByEmail(username);
		if (user == null) {
			throw new UserDataException("Не удалось найти пользователя с username = " + username);
		}
		if (isValidPasswordsData(user, oldPassword, newPassword, repeatedPassword)) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.saveAndFlush(user);
		}

		cacheManager.getCache("user").put(username, user);
		String userEmailFromSession;
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			userEmailFromSession = ((UserDetails) principal).getUsername();
			if (username.equals(userEmailFromSession)) {
				sessionRegistry.getAllSessions(principal, false).forEach(SessionInformation::expireNow);
			}
		}
	}

	public boolean isValidPassword(String email, String oldPassword) {
		User userInDataBase = findByUsername(email);
		if (isBlank(oldPassword)) {
			return false;
		}
		if (userInDataBase == null) {
			throw new UserDataException("Пользователь с таким e-mail не найден в базе!");
		} else {
			if (passwordEncoder.matches(oldPassword, userInDataBase.getPassword())) {
				return true;
			} else {
				throw new UserDataException("Старый пароль неверный!");
			}
		}
	}

	private void checkForNotNew(User user) {
		if (user.getId() == null) {
			throw new UserDataException("Не был передан Id пользователя!");
		}
	}

	private void checkForUniqueEmailAndPhone(User user) {
		List<User> usersInDatabase = findByEmailOrPhone(user.getEmail(), user.getPhone());

		for (User userInDb : usersInDatabase) {
			if (user.getId() == null) {
				if (userInDb.getEmail().equalsIgnoreCase(user.getEmail())) {
					throw new UserDataException("Пользователь с таким e-mail существует!");
				} else if (userInDb.getPhone().equalsIgnoreCase(user.getPhone())) {
					throw new UserDataException("Пользователь с таким телефоном существует!");
				}
			} else if (!userInDb.getId().equals(user.getId())) {
				throw new UserDataException("Пользователь с таким e-mail или телефоном существует!");
			}

		}
	}

	@Override
	public List<User> findAllFromAllCompanies() {
		return userRepository.findByEnabledIsTrue();
	}

	@Override
	@Cacheable("user")
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	private boolean isValidPasswordsData(User user, String oldPassword, String newPassword, String repeatedPassword) {
		if (isBlank(oldPassword) || isBlank(newPassword) || isBlank(repeatedPassword)) {
			return false;
		}
		if (!newPassword.equals(repeatedPassword)) {
			throw new UserDataException("Новый пароль и повтор не совпадают!");
		}
		User userInDatabase = userRepository.findOne(user.getId());
		if (!passwordEncoder.matches(oldPassword, userInDatabase.getPassword())
				&& !(oldPassword.equals(userInDatabase.getPassword()))) {
			throw new UserDataException("Старый пароль не верный!");
		}
		return true;
	}

	private Long[] parseIds(String ids) {
		if (isBlank(ids)) {
			return null;
		}
		Long[] longIds;
		try {
			String[] stringIds = ids.split("\\,");
			int idsAmount = stringIds.length;
			longIds = new Long[idsAmount];
			for (int i = 0; i < idsAmount; i++) {
				longIds[i] = Long.valueOf(stringIds[i]);
			}
		} catch (Exception e) {
			throw new UserDataException("Некоррекный формат переданных Id");
		}
		return longIds;
	}

	private void setPositionsToUser(User user, String positionsIds) {
		Long[] longPositionsIds = parseIds(positionsIds);
		if (longPositionsIds != null) {
			List<Position> positions = positionService.findByIdIn(longPositionsIds);
			user.setPositions(positions);
		}
	}

	private void setRolesToUser(User user, String rolesIds) {
		Long[] longRolesIds = parseIds(rolesIds);
		if (longRolesIds != null) {
			Set<Role> roles = roleService.findByIdIn(longRolesIds);
			user.setRoles(roles);
		}
	}

	private void setPasswordToUser(User user, String isDefaultPassword) {
		Boolean isDefault = Boolean.valueOf(isDefaultPassword);
		String password;
		password = isDefault ? passwordEncoder.encode(defaultPassword) : passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
	}

	private void setDataFromDatabaseToUser(User user) {
		User userInDatabase = userRepository.findOne(user.getId());
		if (userInDatabase != null) {
			user.setShifts(userInDatabase.getShifts());
			user.setSalary(userInDatabase.getSalary());
			user.setBonus(userInDatabase.getBonus());
			user.setEnabled(userInDatabase.isEnabled());
			user.setActivated(userInDatabase.isActivated());
		}
	}
}
