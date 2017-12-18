package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.configs.property.MailCustomSettings;
import com.cafe.crm.models.mail.MailSettings;
import com.cafe.crm.services.interfaces.mail.MailSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/company/configuration/step/mail")
public class MailStepController {

	private final MailSettingsService mailSettingsService;
	private final MailCustomSettings customSettings;

	@Value("${mail.default-smtp}")
	private String defaultSmtp;

	@Autowired
	public MailStepController(MailSettingsService mailSettingsService, MailCustomSettings customSettings) {
		this.mailSettingsService = mailSettingsService;
		this.customSettings = customSettings;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> saveMailSettings(@RequestParam(name = "settingsName") String settingsName,
											  @RequestParam(name = "password") String password,
											  @RequestParam(name = "email") String email) {
		if (!isValidMailData(settingsName, password, email)) {
			return ResponseEntity.badRequest().body("Переданы недопустимые данные!");
		}
		MailSettings newSettings = new MailSettings(settingsName, email, password, defaultSmtp);
		mailSettingsService.save(newSettings);
		JavaMailSenderImpl senderImpl = customSettings.getCustomSettings();
		senderImpl.setUsername(email);
		senderImpl.setPassword(password);
		return ResponseEntity.ok("");
	}

	private boolean isValidMailData(String settingsName, String password, String email) {
		return StringUtils.isNotBlank(settingsName) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(email);
	}
}
