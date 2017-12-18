package com.cafe.crm.services.impl.user;


import com.cafe.crm.dto.registration.UserRegistrationForm;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.role.RoleService;
import com.cafe.crm.services.interfaces.user.UserRegistrationService;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService{

	private final UserService userService;
	private final RoleService roleService;
	private final PasswordEncoder passwordEncoder;

	@Value("#{'${defaultUser.roles}'.split(',')}")
	private List<String> roleNames;

	@Autowired
	public UserRegistrationServiceImpl(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
	}


	@Override
	public void registerUser(UserRegistrationForm userRegistrationForm) {
		Company company = new Company();
		company.setName(userRegistrationForm.getCompanyName());
		User user = dtoToUser(userRegistrationForm);
		user.setCompany(company);
		userService.saveNewUser(user);
	}

	private User dtoToUser(UserRegistrationForm userRegistrationForm) {
		User userToDB = new User();
		userToDB.setEmail(userRegistrationForm.getEmail());
		userToDB.setPhone(userRegistrationForm.getPhone());
		userToDB.setFirstName(userRegistrationForm.getFirstName());
		userToDB.setLastName(userRegistrationForm.getLastName());
		userToDB.setPassword(passwordEncoder.encode(userRegistrationForm.getPassword()));
		userToDB.setRoles(getRolesForNewUser());
		return userToDB;
	}

	private Set<Role> getRolesForNewUser() {
		Set<Role> roles = new HashSet<>();
		for (String roleName : roleNames){
			roles.add(roleService.find(roleName));
		}
		return roles;
	}
}
