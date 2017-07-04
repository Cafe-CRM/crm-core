package com.cafe.crm.service_impl.email;


import com.cafe.crm.dao.boss.BossRepository;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.worker.Boss;
import com.cafe.crm.models.worker.Worker;
import com.cafe.crm.service_abstract.email.HtmlService;
import com.cafe.crm.service_abstract.shift.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Set;

@Component
public class HtmlServiceImpl implements HtmlService {

	private final TemplateEngine templateEngine;

	@Autowired
	private ShiftService shiftService;

	@Autowired
	private BossRepository bossRepository;

	@Value("${site.address}")
	private String siteAddress;

	@Autowired
	public HtmlServiceImpl(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public String getAdvertisingFromImage(String advertisingUrl, String view, String urlToLink, Long id, String token) {
		Context context = new Context();
		context.setVariable("advertisingUrl", advertisingUrl);
		context.setVariable("urlToLink", urlToLink);
		context.setVariable("number", id);
		context.setVariable("token", token);
		context.setVariable("siteAddress", siteAddress);
		return templateEngine.process(view, context);
	}

	public String getAdvertisingFromText(String advertisingText, String view, Long id, String token) {
		Context context = new Context();
		context.setVariable("advertisingText", advertisingText);
		context.setVariable("number", id);
		context.setVariable("token", token);
		context.setVariable("siteAddress", siteAddress);
		return templateEngine.process(view, context);
	}

	public String getAdvertisingForDisable(String view, Long id, String token) {
		Context context = new Context();
		context.setVariable("number", id);
		context.setVariable("token", token);
		context.setVariable("siteAddress", siteAddress);
		return templateEngine.process(view, context);
	}

	@Override
	public String getBalanceInfoAfterDebiting(Long newBalance, Long debited, String view) {
		Context context = new Context();
		context.setVariable("newBalance", newBalance);
		context.setVariable("debited", debited);
		return templateEngine.process(view, context);
	}

	@Override
	public String getCloseShiftFromText(String text, Double salaryShift, Double profitShift, Long cache, Long payWithCard, String view) {
		Set<Worker> allWorker = shiftService.getAllActiveWorkers();
		Set<Calculate> calculate = shiftService.getLast().getAllCalculate();
		List<Client> clients = shiftService.getLast().getClients();
		List<Boss> allBoss = bossRepository.getAllActiveBoss();
		Double shortage = salaryShift - (cache + payWithCard);
		Context context = new Context();
		context.setVariable("message", text);
		context.setVariable("workers", allWorker);
		context.setVariable("calculate", calculate.size());
		context.setVariable("clients", clients.size());
		context.setVariable("allBoss", allBoss);
		context.setVariable("cashBox", profitShift);
		context.setVariable("shortage", shortage);
		return templateEngine.process(view, context);
	}
}
