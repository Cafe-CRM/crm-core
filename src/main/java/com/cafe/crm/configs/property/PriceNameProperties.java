package com.cafe.crm.configs.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;

@Controller
@ConfigurationProperties("property.name.price")
public class PriceNameProperties {

	private String firstHour;
	private String nextHours;
	private String refBonus;

	public String getFirstHour() {
		return firstHour;
	}

	public void setFirstHour(String firstHour) {
		this.firstHour = firstHour;
	}

	public String getNextHours() {
		return nextHours;
	}

	public void setNextHours(String nextHours) {
		this.nextHours = nextHours;
	}

	public String getRefBonus() {
		return refBonus;
	}

	public void setRefBonus(String refBonus) {
		this.refBonus = refBonus;
	}
}
