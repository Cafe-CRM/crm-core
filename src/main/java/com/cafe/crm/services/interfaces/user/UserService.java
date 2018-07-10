package com.cafe.crm.services.interfaces.user;


import com.cafe.crm.dto.ExtraUserData;
import com.cafe.crm.dto.UserLoggingDTO;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService {
	User save(User user);

	List<User> save(Collection<User> users);

	User save(User user, String positionsIds, String rolesIds, String isDefaultPassword);

	User saveNewUser(User user);

	List<User> findAll();

	User findById(Long id);

	User findByEmail(String email);

	User findByPhone(String phone);

	List<User> findByPositionIdAndOrderByLastName(Long positionId);

	List<User> findByPositionIdWithAnyEnabledStatus(Long positionId);

	List<User> findByPositionIdIsNotWithAnyEnabledStatus(Long positionId);

	List<User> findAllEnabledAdminOnShift(Shift shift);

	List<User> findAllEnabledNotAdminOnShift(Shift shift);

	List<User> findByRoleIdWithAnyEnabledStatus(Long roleId);

	List<User> findByRoleName(String roleName);

	Map<Position, List<User>> findAndSortUserByPosition();

	void update(User user, ExtraUserData extraUserData);

	List<User> findByEmailOrPhoneAndCompanyId(String email, String phone, Long companyId);

	List<User> findByEmailOrPhone(String email, String phone);

	void delete(Long id);

	List<User> findByIdIn(long[] ids);

	void changePassword(String username, String oldPassword, String newPassword, String repeatedPassword);

	boolean isValidPassword(String email, String oldPassword);

	User findByUsername(String username);

	List<User> findAllFromAllCompanies();

	Map<Role, List<User>> findAndSortUserByRoleWithSupervisor();

	List<User> findByRoleIdAndOrderByLastName(Long roleId);

	UserLoggingDTO transformUserToLogDTO(User user);

}