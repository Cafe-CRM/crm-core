package com.cafe.crm.utils;

import com.amazonaws.services.identitymanagement.model.UserDetail;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.user.UserRepository;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityUtils {

	private static final String BOSS_PATH = "/boss/menu";
	private static final String MANAGER_PATH = "/manager/shift/";
	private static final String DEFAULT_PATH = "/home";
	private static final String SUPERVISOR_PATH = "/supervisor";

	public static String getStartPath(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals("BOSS")) {
				return BOSS_PATH;
			}
			if (authority.getAuthority().equals("MANAGER")) {
				return MANAGER_PATH;
			} if (authority.getAuthority().equals("SUPERVISOR")) {
				return SUPERVISOR_PATH;
			}
		}
		return DEFAULT_PATH;
	}

	public static List<String> getRoles(Authentication authentication) {
		if (Objects.isNull(authentication)) {
			return Collections.emptyList();
		}
		List<String> roles = new ArrayList<>();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			String role = authority.getAuthority();
			roles.add(role);
		}
		return roles;
	}
	public static boolean hasRole(String... roles) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<String> existedRoles = SecurityUtils.getRoles(authentication);
		for (String role : roles) {
			if (existedRoles.contains(role)) {
				return true;
			}
		}
		return false;
	}
}
