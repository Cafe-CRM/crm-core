package com.cafe.crm.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

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
}
