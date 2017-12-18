package com.cafe.crm.services.impl.recaptcha;

import com.cafe.crm.dto.ReCaptchaResponseDTO;
import com.cafe.crm.services.interfaces.recaptcha.ReCaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class ReCaptchaServiceImpl implements ReCaptchaService {
	@Value("${reCaptcha.apiUrl}")
	private String reCaptchaApiUrl;
	@Value("${reCaptcha.secretKey}")
	private String secretKey;

	public ReCaptchaServiceImpl() {
	}

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public ReCaptchaResponseDTO verify(String recaptchaResponse) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secretKey);
		form.add("response", recaptchaResponse);

		return restTemplate.postForObject(reCaptchaApiUrl, form, ReCaptchaResponseDTO.class);
	}
}
