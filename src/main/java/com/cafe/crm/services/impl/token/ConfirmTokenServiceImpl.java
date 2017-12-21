package com.cafe.crm.services.impl.token;


import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.token.ConfirmToken;
import com.cafe.crm.repositories.token.ConfirmTokenRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.utils.CompanyIdCache;
import com.cafe.crm.utils.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;


@Service
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
	private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final SecureRandom rnd = new SecureRandom();
	private final ConfirmTokenRepository repository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public ConfirmTokenServiceImpl(ConfirmTokenRepository confirmTokenRepository, CompanyService companyService,
								   CompanyIdCache companyIdCache, PasswordEncoder passwordEncoder) {
		this.repository = confirmTokenRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String createAndGetToken(Target target) {
		StringBuilder sb = new StringBuilder( 4 );
		for( int i = 0; i < 4; i++ ) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		String token = sb.toString();
		Date start = new Date();
		ConfirmToken confirmToken = new ConfirmToken(passwordEncoder.encode(token), start, target);
		save(confirmToken);
		return token;
	}


	@Override
	public boolean confirm(String token, Target target) {
		ConfirmToken confirmToken = repository.findByCompanyId(companyIdCache.getCompanyId());
		if (confirmToken == null) {
			return false;
		}
		if (isExpired(confirmToken)) {
			return false;
		}
		if (!confirmToken.getTarget().equals(target)) {
			return false;
		}
		String sourceToken = confirmToken.getToken();
		delete(confirmToken);

		return passwordEncoder.matches(token, sourceToken);
	}

	private boolean isExpired(ConfirmToken token) {
		if (token == null) {
			return true;
		}
		Date now = new Date();
		long diff = (now.getTime() - token.getStartTime().getTime()) / 60000;
		return diff > 10;
	}

	private void save(ConfirmToken confirmToken) {
		ConfirmToken existTokens = repository.findByCompanyId(companyIdCache.getCompanyId());
		if (existTokens != null) {
			delete(existTokens);
		}
		setCompany(confirmToken);
		repository.saveAndFlush(confirmToken);
	}

	private void delete(ConfirmToken confirmToken) {
		ConfirmToken existTokens = repository.findByCompanyId(companyIdCache.getCompanyId());
		if (existTokens != null) {
			repository.delete(confirmToken);
		}
	}

	private void setCompany(ConfirmToken confirmToken) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		confirmToken.setCompany(company);
	}
}
