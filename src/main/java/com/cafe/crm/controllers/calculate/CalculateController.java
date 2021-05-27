package com.cafe.crm.controllers.calculate;

import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.discount.Discount;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateControllerService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.discount.DiscountService;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.services.interfaces.vk.VkService;
import com.cafe.crm.utils.SecurityUtils;
import com.cafe.crm.utils.Target;
import com.cafe.crm.utils.TimeManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequestMapping("/manager")
public class CalculateController {
    @Autowired
    private TimeManager timeManager;

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

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateController.class);

	@Autowired
	public CalculateController(ProductService productService, ClientService clientService, CategoriesService categoriesService,
							   CalculateService calculateService, BoardService boardService,
							   DiscountService discountService, CalculateControllerService calculateControllerService,
							   ChecklistService checklistService, VkService vkService, ConfirmTokenService confirmTokenService) {
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

//	@RequestMapping(value = {"/pause"}, method = RequestMethod.POST)
//	public String pause(@RequestParam("clientId") Long clientId) {
//		Calculate calculate = calculateService.findByClientId(clientId);
//		Client client = calculateControllerService.pauseClient(clientId);
//
//		logger.info("Клиент с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
//				" был поставлен на паузу\n" +
//				"Счёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");
//
//		return "redirect:/manager";
//	}

//	@RequestMapping(value = {"/unpause"}, method = RequestMethod.POST)
//	public String unpause(@RequestParam("clientId") Long clientId) {
//		Calculate calculate = calculateService.findByClientId(clientId);
//		Client client = calculateControllerService.unpauseClient(clientId);
//
//		logger.info("Клиент с описанием: \"" + client.getDescription() + "\" и id: " + client.getId() +
//				" был снят с паузы" +
//				"Счёт с id: " + calculate.getId() + " и описанием: \"" + calculate.getDescription() + "\"");
//
//		return "redirect:/manager";
//	}

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
        LocalTime timeNow = timeManager.getTime().withSecond(0).withNano(0);
		return calculateControllerService.calculatePrice(timeNow);
	}



	@RequestMapping(value = {"/calculate-price-on-calculate"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> calculatePriceOnCalculate(@RequestParam("calculateId") Long calculateId) {
        LocalTime timeNow = timeManager.getTime().withSecond(0).withNano(0);
		return calculateControllerService.calculatePrice(timeNow);
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
											  @RequestParam("calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculateControllerService.closeClient(clientsId, calculateId);
		double allPrice = 0;

		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}

		StringBuilder deletedClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны клиенты с id и описанием:\n");

		deletedClientsInfo.append("Уплаченая сумма: ").append(allPrice);
		logger.info(deletedClientsInfo.toString());
		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-client-without-precheck"}, method = RequestMethod.POST)
	public ResponseEntity<String> closeClient(@RequestParam(name = "password") String password,
											  @RequestParam(name = "clientsId[]", required = false) long[] clientsId,
											  @RequestParam("calculateId") Long calculateId) {
		if (password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}
		if (!confirmTokenService.confirm(password, Target.CLOSE_CLIENT_WITHOUT_PRECHECK)) {
			throw new PasswordException("Пароль не действителен!");
		}

		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculateControllerService.closeClient(clientsId, calculateId);
		double allPrice = 0;

		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}

		StringBuilder deletedClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				"\" и id: " + calculateId + " были рассчитаны клиенты с id и описанием:\n");

		deletedClientsInfo.append("Уплаченая сумма: ").append(allPrice);
		logger.info(deletedClientsInfo.toString());

		return ResponseEntity.ok("Клиенты рассчитаны!");
	}

	@RequestMapping(value = {"/close-new-sum-client"}, method = RequestMethod.POST)
	public ResponseEntity closeNewSumClient(@RequestParam(name = "newAmount") Double modifiedAmount,
											@RequestParam(name = "password") String password,
											@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
											@RequestParam("calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);

		double currentPrice = 0;
		for (Client client : clients) {
			currentPrice += client.getAllPrice();
		}

		calculateControllerService.closeNewSumClient(modifiedAmount, password, clientsId, calculateId);

		StringBuilder closeClientsInfo = new StringBuilder("На счёте с описанием: \"" + calculate.getDescription() +
				" и id: " + calculateId + "\" были рассчитаны клиенты с изменением итоговой суммы " +
				currentPrice + " -> " + modifiedAmount + ". id и описание клиентов: \n");

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
	public ResponseEntity closeClientDebt(@RequestParam(name = "clientsId[]") long[] clientsId,
										  @RequestParam("calculateId") Long calculateId,
										  @RequestParam("debtorName") String debtorName,
										  @RequestParam(value = "paidAmount", required = false) Double paidAmount) {

		if (paidAmount == null) {
			throw new ClientDataException("Укажите уплаченную сумму!");
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

	@RequestMapping(value = {"/precheck"}, method = RequestMethod.POST)
	public ResponseEntity<List<String>> preCheck(@RequestParam(name = "clientsId[]", required = false) long[] clientsId) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		List<String> recipient = new ArrayList<>();
		StringBuilder totalAmount = new StringBuilder();
		int allPrice = 0;
		for (Client client : clients) {
			allPrice += client.getAllPrice();
		}
		totalAmount.append(allPrice);
		recipient.add(mainPreCheck(clients));
		recipient.add(totalAmount.toString());
		return ResponseEntity.ok(recipient);
	}

	@ExceptionHandler(value = {DebtDataException.class, ClientDataException.class})
	public ResponseEntity<?> handleUserUpdateException(RuntimeException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = PasswordException.class)
	public ResponseEntity<?> handleTransferException(PasswordException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@RequestMapping(value = "/send-close-client-pass", method = RequestMethod.POST)
	public ResponseEntity sendDeleteDebtPass(@RequestParam(name = "calculateId") Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);
		String prefix = "Одноразовый пароль для подтверждения расчета клиента \"" + calculate.getDescription() + " ";
		vkService.sendConfirmToken(prefix, Target.CLOSE_CLIENT_WITHOUT_PRECHECK);
		return ResponseEntity.ok("Пароль послан");
	}

	@RequestMapping(value = {"/precheck-with-new-sum"}, method = RequestMethod.POST)
	public ResponseEntity<List<String>> preCheckWithNewSum(@RequestParam(name = "clientsId[]", required = false) long[] clientsId,
														   @RequestParam(name = "newAmount") int newAmount) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		StringBuilder totalAmount = new StringBuilder();
		List<String> recipient = new ArrayList<>();
		totalAmount.append(newAmount);
		recipient.add(mainPreCheck(clients));
		recipient.add(totalAmount.toString());
		return ResponseEntity.ok(recipient);
	}

	private String mainPreCheck(List<Client> clients) {
		StringBuilder mainPreCheck = new StringBuilder();
		int hour = 0;
		int minute = 0;
		int passedHour = 0;
		int passedMinute = 0;
		int hourForOut = 0;
		int minuteForOut = 0;
		int numClient = 1;
		for (Client client : clients) {
			hour = client.getTimeStart().getHour();
			minute = client.getTimeStart().getMinute();
			passedHour = client.getPassedTime().getHour();
			passedMinute = client.getPassedTime().getMinute();
			hourForOut = client.getTimeStart().plusHours(passedHour).getHour();
			minuteForOut = client.getTimeStart().plusMinutes(passedMinute).getMinute();
			mainPreCheck.append(numClient + ".");
			if (client.getDescription().equals("")) {
				mainPreCheck.append("Гость ");
			} else if (client.getDescription().length() > 6) {
			    mainPreCheck.append(client.getDescription() + "\n");
            } else {
				mainPreCheck.append(client.getDescription() + " ");
			}
			mainPreCheck.append((hour < 10 ? "0" + hour : hour) + ":" +
					(minute < 10 ? "0" + minute : minute));
			mainPreCheck.append("-" + (hourForOut < 10 ? "0" + hourForOut : hourForOut)
					+ ":" + (minuteForOut < 10 ? "0" + minuteForOut : minuteForOut));
			if (client.getDescription().length() > 6) {
                mainPreCheck.append("         " + client.getPriceTime().intValue() + "\n");
            } else {
                mainPreCheck.append(" " + client.getPriceTime().intValue() + "\n");
            }

			if (!client.getProductOnPrice().isEmpty()) {
				for (Map.Entry<Long, Double> productOnPrice : client.getProductOnPrice().entrySet()) {
					mainPreCheck
							.append(productService.findOne(productOnPrice.getKey()).getName());
					int j = 20 - productService.findOne(productOnPrice.getKey()).getName().length();
					for (int i = j; i > 0; i--) {
						mainPreCheck.append(" ");
					}
					mainPreCheck.append(productOnPrice.getValue().intValue())
							.append("  ")
							.append("\n");
				}
			}
			mainPreCheck.append("\n");
			numClient++;
		}
		return mainPreCheck.toString();
	}
}


