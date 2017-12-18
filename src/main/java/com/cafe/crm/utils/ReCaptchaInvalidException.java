package com.cafe.crm.utils;


import org.springframework.security.authentication.BadCredentialsException;

import javax.naming.AuthenticationException;

public class ReCaptchaInvalidException extends BadCredentialsException {
	public ReCaptchaInvalidException(String message) {
		super(message);
	}
}
