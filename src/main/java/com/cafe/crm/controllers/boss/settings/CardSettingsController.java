package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.configs.filters.CardFilter;
import com.cafe.crm.controllers.GlobalControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CardSettingsController {

	private final CardFilter cardFilter;
	private final GlobalControllerAdvice globalControllerAdvice;

	@Autowired
	public CardSettingsController(GlobalControllerAdvice globalControllerAdvice, CardFilter cardFilter) {
		this.globalControllerAdvice = globalControllerAdvice;
		this.cardFilter = cardFilter;
	}

	@RequestMapping(value = "/boss/settings/card-setting", method = RequestMethod.GET)
	public String showCardSettingPage() {
		return "settingPages/cardSettingPage";
	}

	@RequestMapping(value = "/boss/settings/card/changeStatus", method = RequestMethod.GET)
	public String changeCardEnableStatus() {
		String oldCardStatus = globalControllerAdvice.isCardEnable();
		String newCardStatus = getReverseStatus(oldCardStatus);
		globalControllerAdvice.setCardEnable(newCardStatus);
		cardFilter.setEnable(!Boolean.valueOf(newCardStatus));

		return "redirect:/boss/settings/card-setting";
	}

	private String getReverseStatus(String current) {
		return current.equals("true") ? "false" : "true";
	}

}
