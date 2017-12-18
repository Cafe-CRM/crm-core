package com.cafe.crm.services.interfaces.login;

import com.cafe.crm.models.login.LoginData;


public interface LoginDataService {
	void saveData(LoginData loginData);
	void incrementErrorCountAndSave(String remoteAddress);
	LoginData findByRemoteAddress(String remoteAddress);
	void delete(String remoteAddress);
}
