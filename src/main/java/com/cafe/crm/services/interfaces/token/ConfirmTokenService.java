package com.cafe.crm.services.interfaces.token;


import java.util.zip.DataFormatException;

public interface ConfirmTokenService {
	public String createAndGetToken();

	public boolean confirm(String token);
}
