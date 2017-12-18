package com.cafe.crm.services.interfaces.recaptcha;


import com.cafe.crm.dto.ReCaptchaResponseDTO;

public interface ReCaptchaService {
	ReCaptchaResponseDTO verify(String recaptchaResponse);
}
