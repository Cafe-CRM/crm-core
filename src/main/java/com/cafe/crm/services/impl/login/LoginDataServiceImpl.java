package com.cafe.crm.services.impl.login;

import com.cafe.crm.models.login.LoginData;
import com.cafe.crm.repositories.login.LoginDataRepository;
import com.cafe.crm.services.interfaces.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginDataServiceImpl implements LoginDataService {

	private final LoginDataRepository loginDataRepository;

	@Autowired
	public LoginDataServiceImpl(LoginDataRepository loginDataRepository) {
		this.loginDataRepository = loginDataRepository;
	}

	@Override
	public void saveData(LoginData loginData) {
		loginDataRepository.saveAndFlush(loginData);
	}

	@Override
	public void incrementErrorCountAndSave(String remoteAddress) {
		LoginData loginData = loginDataRepository.findByRemoteAddress(remoteAddress);
		if (loginData != null) {
			int newCount = loginData.getErrorCount() + 1;
			loginData.setErrorCount(newCount);
			loginData.setLastLoginDate(new Date());
			loginDataRepository.saveAndFlush(loginData);
		}
	}

	@Override
	public LoginData findByRemoteAddress(String remoteAddress) {
		return loginDataRepository.findByRemoteAddress(remoteAddress);
	}

	@Override
	public void delete(String remoteAddress) {
		LoginData loginData = loginDataRepository.findByRemoteAddress(remoteAddress);
		if (loginData != null)
			loginDataRepository.delete(loginData);

	}
}
