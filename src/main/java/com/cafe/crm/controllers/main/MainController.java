package com.cafe.crm.controllers.main;

import com.cafe.crm.models.login.LoginData;
import com.cafe.crm.services.interfaces.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

	@Autowired
	private LoginDataService loginDataService;

	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String redirectToLoginPage() {
		return "redirect:/login";
	}

	@RequestMapping(value = {"/login"}, method = RequestMethod.GET)
	public String showLoginPage(HttpServletRequest request, Model model) {
		LoginData loginData = loginDataService.findByRemoteAddress(request.getRemoteAddr());
		if (loginData != null && loginData.getErrorCount() > 2) {
			model.addAttribute("recaptcha", true);
		}
		return "login";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String showHomePage() {
		return "home/home";
	}
}
