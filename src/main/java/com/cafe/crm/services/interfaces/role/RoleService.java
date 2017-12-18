package com.cafe.crm.services.interfaces.role;

import com.cafe.crm.models.user.Role;

import java.util.List;
import java.util.Set;


public interface RoleService {

    void save(Role role);

	Role find(String name);

	Role find(Long id);

	List<Role> findAll();

	Set<Role> findByIdIn(Long[] ids);

	List<Role> findAllWithSupervisor();

	void update(Role role);

	Role findByName(String name);

	void delete(Long id);
}
