package com.cafe.crm.controllers.calculate;

import com.cafe.crm.exceptions.check.CheckException;
import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.discount.Discount;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateControllerService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.check.PrintCheckService;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.discount.DiscountService;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.SecurityUtils;
import com.cafe.crm.utils.Target;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequestMapping("/manager")
public class CalculateController {

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	private final ClientService clientService;
	private final CalculateControllerService calculateControllerService;
	private final CalculateService calculateService;
	private final BoardService boardService;
	private final ProductService productService;
	private final DiscountService discountService;
	private final CategoriesService categoriesService;
	private final ChecklistService checklistService;
	private final VkService vkService;
	private final ConfirmTokenService confirmTokenService;
	private final MenuCalculateControllerService menuCalculateControllerService;
	private final PrintCheckService printCheckService;
	private final PropertyService propertyService;

	@Value("${property.name.check.propertyName}")
	private String checkPropertyName;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateController.class);

	@Autowired
	public CalculateController(ProductService productService, ClientService clientService, CategoriesService categoriesService,
							   CalculateService calculateService, BoardService boardService,
							   DiscountService discountService, CalculateControllerService calculateControllerService,
							   ChecklistService checklistService, VkService vkService, ConfirmTokenService confirmTokenService,
							   MenuCalculateControllerService menuCalculateControllerService, PrintCheckService printCheckService,
							   PropertyService propertyService) {
		this.productService = productService;
		this.clientService = clientService;
		this.categoriesService = categoriesService;
		this.calculateService = calculateService;
		this.boardService = boardService;
		this.discountService = discountService;
		this.calculateControllerService = calculateControllerService;
		this.checklistService = checklistService;
		this.vkService = vkService;
		this.confirmTokenService = confirmTokenService;
		this.menuCalculateControllerService = menuCalculateControllerService;
		this.printCheckService = printCheckService;
		this.propertyService = propertyService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView manager() {
		ModelAndView modelAndView = new ModelAndView("client/clients");
		modelAndView.addObject("listMenu", categoriesService.sortProductListAndGetAllCategories());
		modelAndView.addObject("listProduct", productService.findAllOrderByRatingDesc());
		modelAndView.addObject("listCalculate", calculateService.getAllOpen());    //todo проблема здесь
		modelAndView.addObject("listBoard", boardService.getAllOpen());
		modelAndView.addObject("listDiscounts", discountService.getAllOpen());
		modelAndView.addObject("stateClients", calculateControllerService.getClientsAndDesc());
		modelAndView.addObject("closeChecklist", checklistService.getAllForCloseShift());
		modelAndView.addObject("bossFunctional", SecurityUtils.hasRole("BOSS", "MANAGER"));
		return modelAndView;
	}

	@RequestMapping(value = {"/pause"}, method = RequestMethod.POST)
	public String pause(@RequestParam("clientId") Long clientId) {
		Calculate calculate = calculateService.findByClientId(clientId);
		Client client = calculateControllerService.pauseClient(clientId);

		logger.info("Клиент с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
				" был поставлен на паузу\n" +
				"Счёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");

		return "redirect:/manager";
	}

	@RequestMapping(value = {"/unpause"}, method = RequestMethod.POST)
	public String unpause(@RequestParam("clientId") Long clientId) {
		Calculate calculate = calculateService.findByClientId(clientId);
		Client client = calculateControllerService.unpauseClient(clientId);

		logger.info("Клиент с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
				" был снят с паузы" +
				"Счёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");

		return "redirect:/manager";
	}

	@RequestMapping(value = "/edit-client-time-start", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editClientTimeStart(@RequestParam("clientId") Long clientId,
												 @RequestParam("hours") int hours,
												 @RequestParam("minutes") int minutes) {
		Calculate calculate = calculateService.findByClientId(clientId);
		Client client = clientService.getOne(clientId);
		String logHouts = (hours == 0) ? "00" : String.valueOf(hours);
		String logMinutes = (minutes == 0) ? "00" : String.valueOf(minutes);

		logger.info("Клиену с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
				" было изменено время посадки: \n" +
				client.getTimeStart().format(formatter) + " -> " + logHouts + ":" + logMinutes +
				"\nСчёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");

		boolean successfully = clientService.updateClientTime(clientId, hours, minutes);
		return successfully ? ResponseEntity.ok("ok") : ResponseEntity.badRequest().body("bad");
	}

	@RequestMapping(value = {"/add-calculate"}, method = RequestMethod.POST)
	public ResponseEntity createCalculate(@RequestParam("boardId") Long id,
										  @RequestParam("number") Double number,
										  @RequestParam(name = "description", required = false) String description) {
		if (StringUtils.isBlank(description)) {
			throw new ClientDataException("Описание стола не может быть пустым!");
		}
		if (id == null) {
			throw new ClientDataException("Выберите стол");
		}
		Calculate calculate = calculateControllerService.createCalculate(id, number.longValue(), description);

		logger.info("Счёт с описанием: \"" + description + "\" и id: " + calculate.getId() + " был добавлен на смену");

		return ResponseEntity.ok("стол добавлен");
	}

	@RequestMapping(value = {"/card/add-card-on-client"}, method = RequestMethod.POST)
	@ResponseBody
	public Long addCardOnClient(@RequestParam("calculateId") Long calculateId,
								@RequestParam("clientId") Long clientId,
								@RequestParam("cardId") Long cardId) {
		return calculateControllerService.addCardOnClient(calculateId, clientId, cardId);
	}

	@RequestMapping(value = {"/refresh-board"}, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void refreshBoard(@RequestParam("boardId") Long idB,
							 @RequestParam("calculateId") Long idC) {
		Calculate calculate = calculateService.getOne(idC);
		Board prevBoard = calculate.getBoard();
		Board nexBoard = boardService.getOne(idB);

		logger.info("Счёту с описанием: \"" + calculate.getDescription() + "\" и id: " + idC +
				" был изменён стол: " + prevBoard.getName() + " -> " + nexBoard.getName());

		calculateControllerService.refreshBoard(idC, idB);
	}

	@RequestMapping(value = {"/add-client"}, method = RequestMethod.POST)
	public String addClient(@RequestParam("calculateId") Long id,
							@RequestParam("number") Double number,
							@RequestParam("description") String description) {
		Calculate calculate = calculateService.getOne(id);
		calculateControllerService.addClient(id, number.longValue(), description);

		logger.info(number + " клиент(ов) с описанием \"" + description + "\" были добавлены на счёт с опсианием: \"" +
				calculate.getDescription() + "\" и id: " + calculate.getId());

		return "redirect:/manager";
	}

	@RequestMapping(value = {"/update-fields-client"}, method = RequestMethod.POST)
	@ResponseBody
	public String updateClientFields(@RequestParam("clientId") Long clientId,
									 @RequestParam("discountId") Long discountId,
									 @RequestParam(name = "payWithCard", required = false) Double payWithCard,
									 @RequestParam("description") String description) {
		Client client = clientService.getOne(clientId);
		Discount discount = discountService.getOne(discountId);
		Calculate calculate = calculateService.findByClientId(clientId);
		Double oldPayWithCard = client.getPayWithCard();
		Long oldDiscount = client.getDiscount();
		String oldDescription = client.getDescription();
		Double newPayWithCard = (payWithCard != null) ? payWithCard : client.getPayWithCard();
		Long newDiscount = (discount != null) ? discount.getDiscount() : client.getDiscount();

		if ((!oldPayWithCard.equals(newPayWithCard) || !oldDescription.equalsIgnoreCase(description))
				|| !oldDiscount.equals(newDiscount)) {

			logger.info("Клиент с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
					" был изменён: \n" +
					"Оплата картой: " + oldPayWithCard + " -> " + newPayWithCard + "\n" +
					"Скидка: " + oldDiscount + "% -> " + newDiscount + "%\n" +
					"Описание: " + oldDescription + " -> " + description + "\n" +
					"Счёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");
		}

		if (discountId == -1) {
			client.setDiscount(0L);
			client.setDiscountObj(null);
		} else {
			client.setDiscount(newDiscount);
			client.setDiscountObj(discount);
		}

		client.setPayWithCard(newPayWithCard);
		client.setDescription(description);
		clientService.save(client);
		return description;
	}

	@RequestMapping(value = {"/calculate-price"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> calculatePrice() {
		return calculateControllerService.calculatePrice();
	}

	@RequestMapping(value = {"/calculate-price-on-calculate"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> calculatePriceOnCalculate(@RequestParam("calculateId") Long calculateId) {
		return calculateControllerService.calculatePrice(calculateId);
	}

	@RequestMapping(value = {"/delete-clients"}, method = RequestMethod.POST)
	public String deleteClients(@RequestParam(name = "clientsId", required = false) long[] clientsId,
								@RequestParam(name = "password") String password,
								@RequestParam("calculateId") Long calculateId) {

		if (clientsId != null) {
			List<Client> clients = clientService.findByIdIn(clientsId);
			Calculate calculate = calculateService.getOne(calculateId);

			StringBuilder deletedClientsInfo = new StringBuilder("Со счёта с описанием: \"" + calculate.getDescription() +
					"\" и id: " + calculateId + " были удалены клиенты с id и описанием:\n");

			for (Client client : clients) {
				deletedClientsInfo.append(client.getId())
						.append(" - ")
						.append(client.getDescription())
						.append("\n");
			}

			logger.info(deletedClientsInfo.toString());
		}

		calculateControllerService.deleteClients(clientsId, calculateId, password);

		return "redirect:/manager";
	}

	@RequestMapping(value = {"/delete-calculate"}, method = RequestMethod.POST)
	public ResponseEntity deleteCalculate(@RequestParam(name = "password") String password,
										  @RequestParam("calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);
		calculateControllerService.deleteCalculate(password, calculateId);

		logger.info("Удалён счёт с описанием: \"" + calculate.getDescription() + "\" и id: " + calculateId);

		return ResponseEntity.ok("Счёт успешно удалён!");
	}

	@RequestMapping(value = {"/output-clients"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> outputClients(@RequestParam(name = "clientsId", required = false) long[] clientsId) {
		return calculateControllerService.outputClients(clientsId);
	}

	@RequestMapping(value = {"/close-client"}, method = RequestMethod.POST)
	public ResponseEntity<String> closeClient(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
											  @RequestParam(name = "calculateId", required = false) Long calculateId) {

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (propertyService.findByName(checkPropertyName).getValue().equals("true")) {
			printCheckService.printCheck(clientsId);
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculateControllerService.closeClient(clientsId, calculateId);
		double allPrice = 0;

		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны клиенты с id и описанием:\n");

		for (Client client : clients) {
			closeClientsInfo.append(client.getId())
					.append(" - ")
					.append(client.getDescription())
					.append("\n");
		}

		closeClientsInfo.append("Уплаченая сумма: ").append(allPrice);
		logger.info(closeClientsInfo.toString());


		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-new-sum-client"}, method = RequestMethod.POST)
	public ResponseEntity closeNewSumClient(@RequestParam(name = "newAmount", required = false) Double newAmount,
											@RequestParam(name = "password", required = false) String password,
											@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
											@RequestParam(name = "calculateId", required = false) Long calculateId) {

		if (newAmount == null) {
			throw new ClientDataException("Укажите новую сумму!");
		}

		if (password == null) {
			throw new ClientDataException("Укажите пароль!");
		}

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}


		if (propertyService.findByName(checkPropertyName).getValue().equals("true")) {
			printCheckService.printCheck(clientsId);
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);

		double currentPrice = 0;
		for (Client client : clients) {
			currentPrice += client.getAllPrice();
		}

		calculateControllerService.closeNewSumClient(newAmount, password, clientsId, calculateId);

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				" и id: " + calculateId + "\" были рассчитаны клиенты с изменением итоговой суммы " +
				currentPrice + " -> " + newAmount + ". id и описание клиентов: \n");

		for (Client client : clients) {
			closeClientsInfo.append(client.getId())
					.append(" - ")
					.append(client.getDescription())
					.append("\n");
		}

		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-and-recalculate"}, method = RequestMethod.POST)
	public ResponseEntity closeAndRecalculate(@RequestParam(name = "newAmount") Double modifiedAmount,
											  @RequestParam(name = "password") String password,
											  @RequestParam("calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);

		double currentPrice = 0;
		for (Client client : calculate.getClient()) {
			currentPrice += client.getAllPrice();
		}
		currentPrice -= calculate.getLossRecalculation();
		currentPrice += calculate.getProfitRecalculation();

		calculateControllerService.closeAndRecalculate(modifiedAmount, password, calculateId);

		logger.info("Счёт с описанием: \"" + calculate.getDescription() + "\" и id: " + calculateId +
				" был закрыт с перерасчётом: " + currentPrice + " -> " + modifiedAmount);

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/recalculate"}, method = RequestMethod.POST)
	public ResponseEntity recalculate(@RequestParam(name = "newAmount") Double modifiedAmount,
									  @RequestParam(name = "password") String password,
									  @RequestParam("calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);

		double currentPrice = 0;
		for (Client client : calculate.getClient()) {
			currentPrice += client.getAllPrice();
		}
		currentPrice -= calculate.getLossRecalculation();
		currentPrice += calculate.getProfitRecalculation();

		calculateControllerService.recalculate(modifiedAmount, password, calculateId);

		logger.info("Счёт с описанием: \"" + calculate.getDescription() + "\" и id: " + calculateId +
				" перерасчитан: " + currentPrice + " -> " + modifiedAmount);

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-client-debt"}, method = RequestMethod.POST)
	public ResponseEntity closeClientDebt(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
										  @RequestParam(name = "calculateId", required = false) Long calculateId,
										  @RequestParam(name = "debtorName", required = false) String debtorName,
										  @RequestParam(value = "paidAmount", required = false) Double paidAmount) {


		if (debtorName == null) {
			throw new ClientDataException("Укажите имя должника!");
		}

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (paidAmount == null) {
			throw new ClientDataException("Укажите уплаченную сумму!");
		}

		if (propertyService.findByName(checkPropertyName).getValue().equals("true")) {
			printCheckService.printCheck(clientsId);
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculateControllerService.closeClientDebt(debtorName, clientsId, calculateId, paidAmount);

		double allPrice = 0;
		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}
		double debtAmount = allPrice - paidAmount;

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны в долг клиенты с id и описанием: \n");

		for (Client client : clients) {
			closeClientsInfo.append(client.getId())
					.append(" - ")
					.append(client.getDescription())
					.append("\n");
		}

		closeClientsInfo.append("Имя должника: \"").append(debtorName)
				.append("\". Уплаченная сумма: ")
				.append(paidAmount)
				.append(". Сумма долга: ")
				.append(debtAmount);

		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Долг добавлен!");
	}

	@RequestMapping(value = {"/send-modify-amount-pass-from-settings"}, method = RequestMethod.POST)
	public ResponseEntity sendModifyAmountPasswordSettings(@RequestParam(name = "calcId") Long calcId,
														   @RequestParam(name = "newAmount") Long newAmount) {
		Calculate calculate = calculateService.getOne(calcId);

		if (calculate == null) {
			throw new ClientDataException("Выбран несуществующий счёт!");
		}

		double allPrice = 0;

		List<Client> allClients = calculate.getClient();
		for (Client client : allClients) {
			allPrice += client.getAllPrice();
		}

		allPrice -= calculate.getLossRecalculation();
		allPrice += calculate.getProfitRecalculation();

		String prefix = "Одноразовый пароль для подтверждения изменения итоговой суммы заказа стола при закрытии \"" +
				calculate.getDescription() + "\" с " + allPrice + " на " + newAmount + " : ";

		vkService.sendConfirmToken(prefix, Target.RECALCULATE);
		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = {"/send-modify-amount-pass"}, method = RequestMethod.POST)
	public ResponseEntity sendModifyAmountPassword(@RequestParam(name = "calcId") Long calcId,
												   @RequestParam(name = "newAmount") Long newAmount,
												   @RequestParam(name = "clientsId[]", required = false) long[] clientsId) {
		Calculate calculate = calculateService.getOne(calcId);

		if (calculate == null) {
			throw new ClientDataException("Выбран несуществующий счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите расчитываемых клиентов!");
		}

		List<Client> allClients = calculate.getClient();
		long stateClientsAmount = allClients.stream().filter(Client::isState).count();
		List<Client> selectedClients = clientService.findByIdIn(clientsId);
		String prefix;

		double allPrice = 0;
		double selectedClientsPrice = 0;

		if (selectedClients.size() == stateClientsAmount) {

			for (Client client : allClients) {
				allPrice += client.getAllPrice();
			}

			for (Client client : selectedClients) {
				selectedClientsPrice += client.getAllPrice();
			}

			allPrice -= calculate.getLossRecalculation();
			allPrice += calculate.getProfitRecalculation();

			double difference = newAmount - selectedClientsPrice;
			double newPrice = allPrice + difference;

			prefix = "Одноразовый пароль для подтверждения изменения итоговой суммы заказа стола при закрытии \"" +
					calculate.getDescription() + "\" с " + allPrice + " на " + newPrice + " : ";

			vkService.sendConfirmToken(prefix, Target.RECALCULATE);
			return ResponseEntity.ok("Пароль послан");

		}

		for (Client client : selectedClients) {
			selectedClientsPrice += client.getAllPrice();
		}

		prefix = "Одноразовый пароль для подтверждения изменения итоговой суммы заказа клиентов с id: " +
				Arrays.toString(clientsId) + " на столе \"" +
				calculate.getDescription() + "\" с " + selectedClientsPrice + " на " + newAmount + " : ";

		vkService.sendConfirmToken(prefix, Target.RECALCULATE);
		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = {"/send-calculate-delete-pass"}, method = RequestMethod.POST)
	public ResponseEntity sendCalculateDeletePassword(@RequestParam(name = "calcId") Long calcId) {
		Calculate calculate = calculateService.getOne(calcId);

		if (calculate == null) {
			throw new ClientDataException("Выбран несуществующий счёт!");
		}

		double currentPrice = 0;
		for (Client client : calculate.getClient()) {
			currentPrice += client.getAllPrice();
		}
		currentPrice -= calculate.getLossRecalculation();
		currentPrice += calculate.getProfitRecalculation();

		String prefix = "Одноразовый пароль для подтверждения удаления стола \"" + calculate.getDescription() +
				"\" с текущей общей суммой " + currentPrice + "р: ";

		vkService.sendConfirmToken(prefix, Target.DELETE_CALC);
		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = {"/send-delete-client-pass"}, method = RequestMethod.POST)
	public ResponseEntity sendDeleteClientPassword(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
												   @RequestParam(name = "calcId") Long calcId) {

		Calculate calculate = calculateService.getOne(calcId);

		if (calculate == null) {
			throw new ClientDataException("Выбран несуществующий счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов на удаление!");
		}

		double selectedClientsPrice = 0;
		StringBuilder prefix = new StringBuilder("Одноразовый пароль для удаления клиентов со стола " + calculate.getDescription() + "\n");
		List<Client> selectedClients = clientService.findByIdIn(clientsId);

		for (Client client : selectedClients) {
			prefix.append("id: " + client.getId() + " - описание: " + client.getDescription() + "\n");
			selectedClientsPrice += client.getAllPrice();
		}

		prefix.append("Общей суммой заказа: " + selectedClientsPrice + "\n");

		vkService.sendConfirmToken(prefix.toString(), Target.DELETE_CLIENT);
		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = "/send-without-check-pass", method = RequestMethod.POST)
	public ResponseEntity sendWithoutCheckPass(@RequestParam(name = "calculateId", required = false) Long calculateId) {

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		Calculate calculate = calculateService.getOne(calculateId);
		String prefix = "Одноразовый пароль для подтверждения расчета клиентов на счете \"" + calculate.getDescription() + "\" без чека\n";

		vkService.sendConfirmToken(prefix, Target.WITHOUT_CHECK);

		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = "/close-client-without-check", method = RequestMethod.POST)
	public ResponseEntity closeClientWithoutCheck(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
												  @RequestParam(name = "calculateId", required = false) Long calculateId,
												  @RequestParam(name = "password", required = false) String password) {



		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (password == null || password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}

		if (!confirmTokenService.confirm(password, Target.WITHOUT_CHECK)) {
			throw new PasswordException("Пароль не действителен!");
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculateControllerService.closeClient(clientsId, calculateId);

		double allPrice = 0;

		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны клиенты без чека с id и описанием:\n");

		for (Client client : clients) {
			closeClientsInfo.append(client.getId())
					.append(" - ")
					.append(client.getDescription())
					.append("\n");
		}

		closeClientsInfo.append("Уплаченая сумма: ").append(allPrice);
		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/send-cost-price-menu-pass"}, method = RequestMethod.POST)
	public ResponseEntity sendCostPricePass(@RequestParam(name = "calculateId", required = false) Long calculateId,
											@RequestParam("newTotalCache") Long newTotalCache,
											@RequestParam(name = "costPriceClientsId[]", required = false) long[] costPriceClientsId,
											@RequestParam(name = "clientsId[]", required = false) long[] clientsId) {

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (costPriceClientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета по себестоимости!");
		}


		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> costPriceClients = clientService.findByIdIn(costPriceClientsId);
		List<Client> clients = clientService.findByIdIn(clientsId);

		long oldTotalCache = 0;

		for (Client client : clients) {
			oldTotalCache += client.getAllPrice();
		}

		StringBuilder prefix = new StringBuilder("Одноразовый пароль для расчета клиентов по себестоимости c:\n");

		for (Client client : costPriceClients) {
			prefix.append("id: ").append(client.getId()).append(" - описание: ").append(client.getDescription()).append("\n");
		}

		prefix.append("Со стола с описанием: \"").append(calculate.getDescription())
				.append(" и id: ").append(calculateId).append("\n")
				.append("Изменение общей суммы заказа с ").append(oldTotalCache)
				.append(" на ").append(newTotalCache).append("\n");

		vkService.sendConfirmToken(prefix.toString(), Target.WITH_COST_PRICE);

		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = {"/close-cost-price-client"}, method = RequestMethod.POST)
	public ResponseEntity closeCostPriceClient(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
											   @RequestParam(name = "calculateId", required = false) Long calculateId,
											   @RequestParam(name = "costPriceClientsId[]",required = false) long[] costPriceClientsId,
											   @RequestParam(name = "password", required = false) String password) {

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (costPriceClientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета по себестоимости!");
		}

		if (password == null || password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}

		if (!confirmTokenService.confirm(password, Target.WITH_COST_PRICE)) {
			throw new PasswordException("Неверный код!");
		}

		if (propertyService.findByName(checkPropertyName).getValue().equals("true")) {
			printCheckService.printCheck(clientsId);
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		List<Client> costPriceClients = clientService.findByIdIn(costPriceClientsId);

		double[] totalCache = calculateControllerService.getOldAndPriceCostTotal(clients, costPriceClients);

		double oldTotalCache = totalCache[0];
		double newTotalCache = totalCache[1];

		calculateControllerService.closeCostPriceClient(newTotalCache, clientsId, calculateId);

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				" и id: " + calculateId + "\" были рассчитаны клиенты c:\n");

		closeClientsInfo.append(costPriceClientsLogInfo(clients, costPriceClients));

		closeClientsInfo.append("Изменение общей суммы заказа с ").append(oldTotalCache)
				.append(" на ").append(newTotalCache).append("\n");

		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-cost-price-client-without-check"}, method = RequestMethod.POST)
	public ResponseEntity closeCostPriceClientWithoutCheck(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
														@RequestParam(value = "calculateId", required = false) Long calculateId,
														@RequestParam(name = "costPriceClientsId[]", required = false) long[] costPriceClientsId,
														@RequestParam(name = "password", required = false) String password) {

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (costPriceClientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета по себестоимости!");
		}

		if (password == null || password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}

		if (!confirmTokenService.confirm(password, Target.WITHOUT_CHECK)) {
			throw new PasswordException("Неверный код!");
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		List<Client> costPriceClients = clientService.findByIdIn(costPriceClientsId);

		double[] totalCache = calculateControllerService.getOldAndPriceCostTotal(clients, costPriceClients);

		double oldTotalCache = totalCache[0];
		double newTotalCache = totalCache[1];

		calculateControllerService.closeCostPriceClient(newTotalCache, clientsId, calculateId);

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				" и id: " + calculateId + "\" были рассчитаны клиенты без чека c:\n");

		closeClientsInfo.append(costPriceClientsLogInfo(clients, costPriceClients));

		closeClientsInfo.append("Изменение общей суммы заказа с ").append(oldTotalCache)
				.append(" на ").append(newTotalCache).append("\n");

		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-client-debt-with-cost-price"}, method = RequestMethod.POST)
	public ResponseEntity closeClientDebtWithCostPrice(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
										  @RequestParam(name = "calculateId", required = false) Long calculateId,
										  @RequestParam(name = "costPriceClientsId[]", required = false) long[] costPriceClientsId,
										  @RequestParam(name = "debtorName", required = false) String debtorName,
										  @RequestParam(name = "paidAmount", required = false) Double paidAmount) {

		if (paidAmount == null) {
			throw new ClientDataException("Укажите уплаченную сумму!");
		}

		if (debtorName == null || debtorName.equals("")) {
			throw new ClientDataException("Укажите имя должника!");
		}

		if (calculateId == null) {
			throw new ClientDataException("Выберите счёт!");
		}

		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (costPriceClientsId == null) {
			throw new ClientDataException("Выберите клиентов для расчета по себестоимости!");
		}

		if (propertyService.findByName(checkPropertyName).getValue().equals("true")) {
			printCheckService.printCheck(clientsId);
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		List<Client> costPriceClients = clientService.findByIdIn(costPriceClientsId);

		double[] totalCache = calculateControllerService.getOldAndPriceCostTotal(clients, costPriceClients);

		double oldTotalCache = totalCache[0];
		double newTotalCache = totalCache[1];

		calculateControllerService.closeClientDebtWithCostPrice(debtorName, clients, calculate, paidAmount, newTotalCache);


		double debtAmount = newTotalCache - paidAmount;

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны в долг клиенты с id и описанием: \n");

		closeClientsInfo.append(costPriceClientsLogInfo(clients, costPriceClients));

		closeClientsInfo.append("Изменение общей суммы заказа с ").append(oldTotalCache)
				.append(" на ").append(newTotalCache).append("\n");

		closeClientsInfo.append("Имя должника: \"").append(debtorName)
				.append("\". Уплаченная сумма: ")
				.append(paidAmount)
				.append(". Сумма долга: ")
				.append(debtAmount);

		logger.info(closeClientsInfo.toString());

		return ResponseEntity.ok("Долг добавлен!");
	}

	@RequestMapping(value = "/repeat-print-check", method = RequestMethod.POST)
	public ResponseEntity repeatPrintCheck(@RequestParam(name = "calculateId") Long calculateId) {
		if (calculateId == null) {
			throw new ClientDataException("Выберите клиентов для расчета!");
		}

		if (propertyService.findByName(checkPropertyName).getValue().equals("false")) {
			throw new CheckException("Печать чеков отключена в настройках!");
		}

		printCheckService.repeatPrintCheck(calculateId);

		logger.info("Был повторно напечатан чек на счете с id: " + calculateId);

		return ResponseEntity.ok("Чек напечатан!");
	}

	private String costPriceClientsLogInfo(List<Client> clients, List<Client> costPriceClients) {
		StringBuilder clientsInfo = new StringBuilder();

		for (Client client : clients) {
			clientsInfo.append("id: ").append(client.getId()).append(" - описание: ").append(client.getDescription());
			if (costPriceClients.contains(client)) {
				clientsInfo.append(" - по себестоимости");
			}
			clientsInfo.append("\n");
		}

		return clientsInfo.toString();
	}

	@ExceptionHandler(value = {DebtDataException.class, ClientDataException.class})
	public ResponseEntity<?> handleUserUpdateException(RuntimeException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = PasswordException.class)
	public ResponseEntity<?> handleTransferException(PasswordException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = CheckException.class)
	public ResponseEntity<?> handleCheckException(CheckException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}


