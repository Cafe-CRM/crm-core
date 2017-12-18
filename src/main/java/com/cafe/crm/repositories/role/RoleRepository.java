package com.cafe.crm.repositories.role;

import com.cafe.crm.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);

	Set<Role> findByIdIn(Long[] ids);

	List<Role> findAll();

	List<Role> findByNameIsNot(String name);
}
