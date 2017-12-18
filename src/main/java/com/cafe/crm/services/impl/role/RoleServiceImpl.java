package com.cafe.crm.services.impl.role;

import com.cafe.crm.exceptions.user.RoleDataException;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.role.RoleRepository;
import com.cafe.crm.services.interfaces.role.RoleService;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;
	private UserService userService;

	@Autowired
	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Autowired
	public void setUserService(UserService userService){
		this.userService = userService;
	}

	@Override
	public void save(Role role) {
		roleRepository.saveAndFlush(role);
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findByNameIsNot("SUPERVISOR");
	}

	@Override
	public Set<Role> findByIdIn(Long[] ids) {
		return roleRepository.findByIdIn(ids);
	}

	@Override
	public Role find(String name) {
		return roleRepository.findByName(name);
	}

	@Override
	public Role find(Long id) {
		return roleRepository.findOne(id);
	}

	@Override
	public List<Role> findAllWithSupervisor(){
		return roleRepository.findAll();
	}

	@Override
	public void update(Role role) {
		checkForNotNew(role);
		checkForUniqueName(role);
		roleRepository.saveAndFlush(role);
	}

	@Override
	public Role findByName(String name) {
		return roleRepository.findByName(name);
	}

	private void checkForNotNew(Role role) {
		if (role.getId() == null) {
			throw new RoleDataException("Не был передан Id пользователя!");
		}
	}

	private void checkForUniqueName(Role role) {
		Role roleInDatabase = findByName(role.getName());
		if ((roleInDatabase != null) && !roleInDatabase.getId().equals(role.getId())) {
			throw new RoleDataException("Должность с таким названием уже существует!");
		}
	}

	@Override
	public void delete(Long id) {
		Role role = roleRepository.findOne(id);
		List<User> users = userService.findByRoleIdWithAnyEnabledStatus(id);
		for (User user : users) {
			user.getRoles().remove(role);
			Set<Role> newUserRoleList = user.getRoles();
			user.setRoles(newUserRoleList);
			userService.save(user);
		}
		roleRepository.delete(id);
	}
}
