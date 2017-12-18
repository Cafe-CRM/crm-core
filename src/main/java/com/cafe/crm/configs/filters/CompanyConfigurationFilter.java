package com.cafe.crm.configs.filters;

import com.cafe.crm.services.interfaces.company.configuration.StepByStepConfiguration;
import com.cafe.crm.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class CompanyConfigurationFilter extends GenericFilterBean {

	private static final String CONFIGURATION_PATH = "/boss/company/configuration";
	private static final String ATTENTION_PATH = "/company/configuration/attention";

	private final List<String> filteredPaths = new ArrayList<>(Arrays.asList("/boss", "/manager"));
	private final List<String> allowedRolesForConfiguration = new ArrayList<>(Collections.singletonList("BOSS"));
	private final StepByStepConfiguration stepByStepConfiguration;

	@Autowired
	public CompanyConfigurationFilter(StepByStepConfiguration stepByStepConfiguration) {
		this.stepByStepConfiguration = stepByStepConfiguration;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getRequestURI();
		if (isConfigurationNeeded(requestURI)) {
			String redirectPath = getPathForRedirect();
			HttpServletResponse res = (HttpServletResponse) response;
			res.sendRedirect(redirectPath);
			return;
		}

		chain.doFilter(request, response);
	}

	private boolean isConfigurationNeeded(String requestURI) {
		return isSuitablePathForConfiguration(requestURI) && stepByStepConfiguration.hasNextStep();
	}

	private boolean isSuitablePathForConfiguration(String path) {
		for (String filteredPath : filteredPaths) {
			if (path.startsWith(filteredPath) && !path.equals(CONFIGURATION_PATH)) {
				return true;
			}
		}
		return false;
	}

	private String getPathForRedirect() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<String> principalRoles = SecurityUtils.getRoles(authentication);
		if (CollectionUtils.containsAny(principalRoles, allowedRolesForConfiguration)) {
			return CONFIGURATION_PATH;
		}
		return ATTENTION_PATH;
	}
}
