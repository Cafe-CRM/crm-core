package com.cafe.crm.security.handlers;

import com.cafe.crm.models.login.LoginData;
import com.cafe.crm.services.interfaces.login.LoginDataService;
import com.cafe.crm.utils.ReCaptchaInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Autowired
	private LoginDataService loginDataService;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(exception);
		redirectStrategy.sendRedirect(request, response, targetUrl);
		loginDataService.incrementErrorCountAndSave(request.getRemoteAddr());
	}

	private String determineTargetUrl(Exception exception) {
		if (exception.getClass().equals(ReCaptchaInvalidException.class)) {
			return "/login?error=2";
		} else {
			return "/login?error=1";
		}
	}
}
