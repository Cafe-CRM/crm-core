package com.cafe.crm.services.interfaces.token;


import com.cafe.crm.utils.Target;

import java.util.zip.DataFormatException;

public interface ConfirmTokenService {
	public String createAndGetToken(Target target);

	public boolean confirm(String token, Target target);
}
