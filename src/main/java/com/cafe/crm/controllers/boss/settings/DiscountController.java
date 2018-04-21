package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.discount.Discount;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.discount.DiscountService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/boss/settings/discount-setting")
public class DiscountController {

	private final DiscountService discountService;
	private final ClientService clientService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(DiscountController.class);

	@Autowired
	public DiscountController(DiscountService discountService, ClientService clientService) {
		this.discountService = discountService;
		this.clientService = clientService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView discountSettingPage() {
		ModelAndView modelAndView = new ModelAndView("settingPages/discountSettingPage");
		modelAndView.addObject("discounts", discountService.getAllOpen());
		return modelAndView;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newDiscount(HttpServletRequest request, Discount discount) {
		Discount savedDiscount = discountService.save(discount);

		logger.info("Добавлена скидка с id " + savedDiscount.getId() + " описанием: \"" + savedDiscount.getDescription()
				+ "\" и процентом: " + savedDiscount.getDiscount() + "%");

		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteDiscount(@RequestParam("id") Long id, HttpServletRequest request) {
		String referrer = request.getHeader("Referer");
		Discount discount = discountService.getOne(id);
		List<Client> clientList = clientService.getAllOpen();

		boolean assignedToClient = false;
		for (Client client : clientList) {
			if (client.getDiscountObj() != null) {
				if (client.getDiscountObj().equals(discount)) {
					assignedToClient = true;
					break;
				}
			}

		}

		if (assignedToClient) {
			discount.setIsOpen(false);
			discountService.save(discount);
		} else {
			discountService.delete(discount);
		}

		logger.info("Удалена скидка с id " + discount.getId() + " описанием: \"" + discount.getDescription()
				+ "\" и процентом: " + discount.getDiscount() + "%");

		return "redirect:" + referrer;
	}
}
