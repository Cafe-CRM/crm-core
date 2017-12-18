package com.cafe.crm.utils;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CompanyIdCache {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Long getCompanyId() {
		String username = getUsername();
		Company company = userService.findByUsername(username).getCompany();
		if (company == null) {
			throw new RuntimeException("У юзера нет компании!");
		}
		return company.getId();
	}

	private String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}

}