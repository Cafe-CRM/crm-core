package com.cafe.crm.services.impl.vk;


import com.cafe.crm.configs.property.VkProperties;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.note.NoteRecord;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.template.Template;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.email.EmailService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.salary.UserSalaryDetailService;
import com.cafe.crm.services.interfaces.template.TemplateService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.Target;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Служба для взаимодействия с Vk API.<br/>
 *
 * Первоначально нужно зарегестрировать свое приложение по это ссылке (@link https://vk.com/editapp?act=create) .<br/><br/>
 *
 * Перед выполнением запросов к API необходимо получить ключ доступа access_token.<br/>
 * Необходимо перенаправить браузер пользователя по адресу https://oauth.vk.com/authorize.<br/>
 * Пример запроса:<br/>
 * (@link https://oauth.vk.com/authorize?client_id=1&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=messages,offline&response_type=token&v=5.68&state=123456}
 * Где, client_id - идентификатор вашего приложения,
 * 		display - тип отображения страницы авторизации,
 * 		redirect_uri - адрес, на который будет переадресован пользователь после прохождения авторизации,
 * 			по умолчанию: https://oauth.vk.com/blank.html,
 * 		scope - битовая маска настроек доступа приложения(для не ограниченного по времени токена указать offline),
 * 		response_type - тип ответа, который необходимо получить,
 * 		v - версия API,
 * 		state - произвольная строка, которая будет возвращена вместе с результатом авторизации.
 * 	Для возможности отправлять сообщения в scope должен быть параметр messages.<br/><br/>
 *
 * После успешного входа на сайт пользователю будет предложено авторизовать приложение,
 * разрешив доступ к необходимым настройкам, запрошенным при помощи параметра scope.
 * После успешной авторизации приложения браузер пользователя будет перенаправлен по адресу redirect_uri,
 * указанному при открытии диалога авторизации.
 * При этом ключ доступа к API access_token и другие параметры будут переданы в URL-фрагменте ссылки.<br/><br/>
 *
 * 	В свойствах приложения (application.yml или ему подбное) должны обязательно присутствовать:
 * 		chat-id - id конференции,
 * 		access-token - токен доступа,
 * 		api-version - версия Vk Api.
 * 	<br/>Чтоб взять chat-id , заходим в нужный чат, url будет иметь вид vk.com/im?sel=c5. Наш chat_id будет 5 (! не с5).<br/>
 * 	Пример : <br/> <img src="../../../../../../../resources/doc-files/chat-id.png" alt="chat-id">
 */
@Service
public class VkServiceImpl implements VkService {

	private static final String DAILY_REPORT_URL = "https://api.vk.com/method/messages.send?random_id={random_id}&chat_id={chat_id}&message={message}&access_token={access_token}&v={v}";
	private static final String EMAIL_RECIPIENT_ROLE_IN_CASE_ERROR = "BOSS";
	private static final int ERROR_CODE_INVALID_TOKEN = 5;

	@Value("${property.name.vk}")
	private String vkPropertyName;

	private final TemplateService templateService;
	private final RestTemplate restTemplate;
	private final EmailService emailService;
	private final UserService userService;
	private final ProductService productService;
	private final PropertyService propertyService;
	private final ConfirmTokenService tokenService;
	private final UserSalaryDetailService userSalaryDetailService;
	private final CostService costService;
	private final DebtService debtService;

	@Autowired
	public VkServiceImpl(TemplateService templateService, RestTemplate restTemplate, EmailService emailService,
						 UserService userService, PropertyService propertyService,  ProductService productService,
						 UserSalaryDetailService userSalaryDetailService, ConfirmTokenService tokenService,
						 CostService costService, DebtService debtService) {
		this.templateService = templateService;
		this.restTemplate = restTemplate;
		this.emailService = emailService;
		this.userService = userService;
		this.productService = productService;
		this.propertyService = propertyService;
		this.tokenService = tokenService;
		this.userSalaryDetailService = userSalaryDetailService;
		this.costService = costService;
		this.debtService = debtService;
	}

	@Override
	public String sendDailyReportToConference(Shift shift) {
		VkProperties vkProperties = getVkPropertiesFromDB();
		String message = getReportMessage(shift);
		Map<String, String> variables = new HashMap<>();
        variables.put("random_id", String.valueOf(new Random().nextInt(32)));
		variables.put("chat_id", vkProperties.getServiceChatId());
		variables.put("message", message);
		variables.put("access_token", vkProperties.getAccessToken());
		variables.put("v", vkProperties.getApiVersion());
		ResponseEntity<String> response = restTemplate.postForEntity(DAILY_REPORT_URL, null, String.class, variables);
		checkForInvalidToken(response);

		return message;
	}

	@Override
	public String getReportMessage(Shift shift) {
		VkProperties vkProperties = getVkPropertiesFromDB();
		if (vkProperties == null) {
			throw new NullPointerException("Не удалось получить vk properties из базы");
		}
		Template messageTemplate = templateService.findByName(vkProperties.getMessageName());
		if (messageTemplate == null) {
			throw new NullPointerException("Не удалось получить шаблон сообщения vk из базы");
		}
		return formatMessage(shift, new String(messageTemplate.getContent(), Charset.forName("UTF-8")));
	}

	@Override
	public void sendConfirmToken(String prefix, Target target) {
		VkProperties vkProperties = getVkPropertiesFromDB();
		if (vkProperties == null) {
			throw new NullPointerException("Не удалось получить vk properties из базы");
		}
		Template messageTemplate = templateService.findByName(vkProperties.getMessageName());
		if (messageTemplate == null) {
			return;
		}
		String message = prefix + tokenService.createAndGetToken(target);
		System.out.println(message);
		Map<String, String> variables = new HashMap<>();
		variables.put("random_id", String.valueOf(new Random().nextInt(32)));
		variables.put("chat_id", vkProperties.getAdminChatId());
		variables.put("message", message);
		variables.put("access_token", vkProperties.getAccessToken());
		variables.put("v", vkProperties.getApiVersion());
		ResponseEntity<String> response = restTemplate.postForEntity(DAILY_REPORT_URL, null, String.class, variables);
		checkForInvalidToken(response);
	}

	private void checkForInvalidToken(ResponseEntity<String> response) {
		JSONObject jsonObject = new JSONObject(response.getBody());
		if (jsonObject.has("error")) {
			int code = jsonObject.getJSONObject("error").getInt("error_code");
			if (code == ERROR_CODE_INVALID_TOKEN) {
				List<User> users = userService.findByRoleName(EMAIL_RECIPIENT_ROLE_IN_CASE_ERROR);
				if (!users.isEmpty()) {
					emailService.sendInvalidTokenNotification(users);
				}
			}
		}
	}

	private String formatMessage(Shift shift, String raw) {
		Object[] params = new Object[17];
		DecimalFormat df = new DecimalFormat("#.##");

		StringBuilder salaryCosts = new StringBuilder();
		StringBuilder otherCosts = new StringBuilder();
		List<Client> clients = shift.getClients().stream().filter(c -> !c.isDeleteState()).collect(Collectors.toList());
		List<Debt> givenDebts = debtService.findGivenDebtsByShift(shift);
		double profit = shift.getProfit();
		double cashBoxDebtAmount = givenDebts.stream().filter(Debt::isCashBoxDebt).mapToDouble(Debt::getDebtAmount).sum();
		double totalCosts = formatCostsAndGetOtherCosts(shift, otherCosts) + formatCostsAndGetSalariesCost(shift, salaryCosts);
		double shortage = profit - cashBoxDebtAmount - totalCosts - shift.getCashBox() - shift.getBankCashBox();

		params[0] = shortage <= 0d ? "" : "НЕДОСТАЧА!";
		params[1] = getDayOfWeek(shift.getShiftDate());
		params[2] = getDate(shift.getShiftDate());
		params[3] = profit;
		params[4] = df.format(shift.getAlteredCashAmount());
		params[5] = getAmountOfClients(clients);
		params[6] = clients.size();
		params[7] = getProdInfo(shift);
		params[8] = getAdmins(shift);
		params[9] = salaryCosts.toString();
		params[10] = otherCosts.toString();
		params[11] = df.format(totalCosts);
		params[12] = df.format(shift.getCashBox());
		params[13] = df.format(shift.getBankCashBox());
		params[14] = df.format(shift.getBankCashBox() + shift.getCashBox());
		params[15] = getComment(shift.getComment());
		params[16] = getNotes(shift.getNoteRecords());

		return MessageFormat.format(raw, params);
	}

	private String getProdInfo(Shift shift) {
		StringBuilder sb = new StringBuilder();

		Set<Client> clients = shift.getClients();
		List<LayerProduct> products = new ArrayList<>();
		Set<LayerProduct> accProd = new HashSet<>();
		Map<String, Integer> countMap = new HashMap<>();
		Map<String, Double> amountMap = new HashMap<>();
		Set<String> categoryName = new HashSet<>();

		for (Client client : clients) {
			if (!client.isDeleteState()) {
				products.addAll(client.getLayerProducts());
			}
		}
		for (LayerProduct product : products) {
			if (product.isAccountability()) {
				accProd.add(product);
			}
		}

		if (!accProd.isEmpty()) {
			sb.append("\nПродано:\n");
		} else {
			return sb.toString();
		}

		for (LayerProduct product : accProd) {
			double amount = product.getCost();
			String name = productService.findOne(product.getProductId()).getCategory().getName();
			if (countMap.containsKey(name)) {
				int prev = countMap.get(name);
				int now = prev + 1;
				countMap.put(name, now);
			} else {
				countMap.put(name, 1);
			}
			if (amountMap.containsKey(name)) {
				double prev = amountMap.get(name);
				double now = prev + amount;
				amountMap.put(name, now);
			} else {
				amountMap.put(name, amount);
			}
			categoryName.add(name);
		}
		for (String name : categoryName) {
			sb.append(name)
					.append("(")
					.append(countMap.get(name))
					.append(")")
					.append(" на сумму: ")
					.append(amountMap.get(name))
					.append("р")
					.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}

	private String getAdmins(Shift shift) {
		StringBuilder sb = new StringBuilder();

		List<User> users = shift.getUsers();
		for (User user : users) {
			boolean isAdmin = user.getRoles().stream()
					.anyMatch(r -> r.getName().equals("BOSS") || r.getName().equals("MANAGER"));
			if (isAdmin) {
				sb.append(user.getFirstName()).append(" ").append(user.getLastName()).append(System.getProperty("line.separator"));
			}
		}

		return sb.toString();
	}

	private String getProfit(Shift shift, List<Debt> givenDebts) {
		DecimalFormat df = new DecimalFormat("#.##");

		double profit = shift.getProfit();
		double otherDebtAmount = 0D;
		double cashBoxDebtAmount = 0D;

		if (givenDebts.isEmpty()) {
			return df.format(profit);
		}
		for (Debt debt : givenDebts) {
			if (!debt.isCashBoxDebt()) {
				otherDebtAmount += debt.getDebtAmount();
			} else {
				cashBoxDebtAmount += debt.getDebtAmount();
			}
		}

		return df.format(profit) + "(Сумма прочих долгов: " + df.format(otherDebtAmount)
				+ "; Долгов из кассы: " + df.format(cashBoxDebtAmount) +")";
	}

	private String getComment(String comment) {
		return StringUtils.isEmpty(comment) ? "" : "\nКомментарий :\n" + comment + "\n";
	}

	private double formatCostsAndGetSalariesCost(Shift shift, StringBuilder salaries) {
		DecimalFormat df = new DecimalFormat("#.##");
		double salaryCost = 0d;

		List<UserSalaryDetail> salaryDetails = userSalaryDetailService.findPaidDetailsByShiftId(shift.getId());

		for (UserSalaryDetail detail : salaryDetails) {
			salaries
					.append(detail.getUser().getFirstName())
					.append(" ")
					.append(detail.getUser().getLastName())
					.append(" - ").append(df.format(detail.getPaidSalary() + detail.getPaidBonus()))
					.append(System.getProperty("line.separator"));
			salaryCost += detail.getPaidSalary() + detail.getPaidBonus();
		}

		if (salaries.length() > 0) {
			salaries.deleteCharAt(salaries.length() - 1);
		} else {
			salaries.append("Отсутствует!");
		}
		return salaryCost;
	}

	private double formatCostsAndGetOtherCosts(Shift shift, StringBuilder otherCosts) {
		List<Cost> costs = costService.findOtherCostByShiftId(shift);

		DecimalFormat df = new DecimalFormat("#.##");
		double otherCost = 0d;
		boolean needGiveNameToOtherCosts = true;
		for (Cost cost : costs) {
			otherCost += cost.getPrice() * cost.getQuantity();
			if (needGiveNameToOtherCosts) {
				otherCosts.append("\nПрочие расходы:\n");
			}
			needGiveNameToOtherCosts = false;
			otherCosts
				.append(cost.getName())
				.append(" - ").append(df.format(cost.getPrice() * cost.getQuantity()))
				.append(System.getProperty("line.separator"));
		}
		if (otherCosts.length() > 0) {
			otherCosts.deleteCharAt(otherCosts.length() - 1);
		}
		return otherCost;
	}

	private String getAmountOfClients(Collection<? extends Client> clients) {
		long amountOfClientsFrom12To16 = clients.stream().filter(client -> isTimeBetween(client.getTimeStart().toLocalTime(), LocalTime.of(12, 00), LocalTime.of(16, 00))).count();
		long amountOfClientsFrom16To18 = clients.stream().filter(client -> isTimeBetween(client.getTimeStart().toLocalTime(), LocalTime.of(16, 00), LocalTime.of(18, 00))).count();
		long amountOfClientsFrom18To20 = clients.stream().filter(client -> isTimeBetween(client.getTimeStart().toLocalTime(), LocalTime.of(18, 00), LocalTime.of(20, 00))).count();
		long amountOfClientsFrom20To22 = clients.stream().filter(client -> isTimeBetween(client.getTimeStart().toLocalTime(), LocalTime.of(20, 00), LocalTime.of(22, 00))).count();
		long amountOfClientsFrom22To00 = clients.stream().filter(client -> {
			LocalTime timeStart = client.getTimeStart().toLocalTime();
			return timeStart.isAfter(LocalTime.of(22, 00)) && isBeforeMidnight(timeStart);
		}).count();
		long amountOfClientsFrom00To06 = clients.stream().filter(client -> isTimeBetween(client.getTimeStart().toLocalTime(), LocalTime.of(00, 00), LocalTime.of(06, 00))).count();
		StringBuilder sb = new StringBuilder();
		sb.append("12-16 x ").append(amountOfClientsFrom12To16).append(System.getProperty("line.separator"));
		sb.append("16-18 x ").append(amountOfClientsFrom16To18).append(System.getProperty("line.separator"));
		sb.append("18-20 x ").append(amountOfClientsFrom18To20).append(System.getProperty("line.separator"));
		sb.append("20-22 x ").append(amountOfClientsFrom20To22).append(System.getProperty("line.separator"));
		sb.append("22-00 x ").append(amountOfClientsFrom22To00).append(System.getProperty("line.separator"));
		sb.append("00-06 x ").append(amountOfClientsFrom00To06);
		return sb.toString();
	}

	private String getDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return date.format(formatter);
	}

	private String getDayOfWeek(LocalDate date) {
		String dayOfWeek = DateTimeFormatter.ofPattern("EEEE").withLocale(new Locale("ru")).format(date);
		dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
		return dayOfWeek;
	}

	private boolean isTimeBetween(LocalTime timeStart, LocalTime after, LocalTime before) {
		return timeStart.isAfter(after) && timeStart.isBefore(before);
	}

	private boolean isBeforeMidnight(LocalTime timeStart) {
		int cmp = Integer.compare(timeStart.getHour(), 24);
		if (cmp == 0) {
			cmp = Integer.compare(timeStart.getMinute(), 0);
			if (cmp == 0) {
				cmp = Integer.compare(timeStart.getSecond(), 0);
				if (cmp == 0) {
					cmp = Integer.compare(timeStart.getNano(), 0);
				}
			}
		}
		return cmp < 0;
	}

	private String getNotes(List<NoteRecord> noteRecords) {
		if ((noteRecords == null) || (noteRecords.size() == 0)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (NoteRecord noteRecord : noteRecords) {
			sb.append(noteRecord.getName()).append(" : ").append(noteRecord.getValue()).append(System.getProperty("line.separator"));
		}
		return "\nЗаметки :\n" + sb.toString();
	}

	private VkProperties getVkPropertiesFromDB() {
		ObjectMapper mapper = new ObjectMapper();
		Property property = propertyService.findByName(vkPropertyName);
		VkProperties vkProperties = null;
		try {
			vkProperties = mapper.readValue(property.getValue(), VkProperties.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vkProperties;
	}
}
