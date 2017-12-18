package com.cafe.crm.security.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomLogoutHandler extends SimpleUrlLogoutSuccessHandler {

	private CacheManager cacheManager;

	public CustomLogoutHandler(CacheManager cacheManager){
		this.cacheManager = cacheManager;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		String username = authentication.getName();
		cacheManager.getCache("user").evict(username);

		super.onLogoutSuccess(request, response, authentication);
	}
}
