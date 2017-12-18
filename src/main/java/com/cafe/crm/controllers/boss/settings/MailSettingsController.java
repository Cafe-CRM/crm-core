package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.configs.property.MailCustomSettings;
import com.cafe.crm.models.mail.MailSettings;
import com.cafe.crm.services.interfaces.mail.MailSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/boss/settings/mail-setting")
public class MailSettingsController {

	private final MailSettingsService mailSettingsService;
	private final MailCustomSettings customSettings;

	@Value("${mail.default-smtp}")
	private String defaultSmtp;

	@Autowired
	public MailSettingsController(MailSettingsService mailSettingsService, MailCustomSettings customSettings) {
		this.mailSettingsService = mailSettingsService;
		this.customSettings = customSettings;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showMailSettingsPage() {
		ModelAndView modelAndView = new ModelAndView("settingPages/mailSettingsPage");
		modelAndView.addObject("mailSettings", customSettings.getCustomSettings());
		modelAndView.addObject("listOfMailSettings", mailSettingsService.getAll());
		return modelAndView;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addMailSettings(
			@RequestParam(name = "settingsName") String settingsName,
			@RequestParam(name = "password") String password,
			@RequestParam(name = "email") String email) {

		MailSettings settingsInDB = mailSettingsService.findByEmail(email);

		if (settingsInDB == null) {
			MailSettings newSettings = new MailSettings(settingsName, email, password, defaultSmtp);

			mailSettingsService.save(newSettings);
			JavaMailSenderImpl senderImpl = customSettings.getCustomSettings();
			senderImpl.setUsername(email);
			senderImpl.setPassword(password);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/change-settings", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changeMailSettings(@RequestParam(name = "settingsId") Long id) {
		MailSettings settings = mailSettingsService.get(id);
		JavaMailSenderImpl senderImpl = customSettings.getCustomSettings();

		if (settings.getEmail().equalsIgnoreCase(senderImpl.getUsername())) {
			return ResponseEntity.badRequest().body("Эти настройки уже применены!");
		} else {
			senderImpl.setUsername(settings.getEmail());
			senderImpl.setPassword(settings.getPassword());
			return ResponseEntity.ok("Настройки успешно применены!");
		}
	}

	@RequestMapping(value = "/del-settings", method = RequestMethod.POST)
	public String delExistingSettings(@RequestParam(name = "settingsId") Long id) {

		mailSettingsService.delete(id);

		return "redirect:/boss/settings/mail-setting";
	}

}
