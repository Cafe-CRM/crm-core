package com.cafe.crm.services.impl.calculate;

import ch.qos.logback.classic.Logger;
import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.controllers.card.CardProfileController;
import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.*;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.*;
import com.cafe.crm.services.interfaces.card.CardService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.email.EmailService;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.utils.Target;
import com.cafe.crm.utils.TimeManager;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalculateControllerServiceImpl implements CalculateControllerService {

	private final ClientService clientService;
	private final CalculateService calculateService;
	private final DebtService debtService;
	private final BoardService boardService;
	private final CardService cardService;
	private final CalculatePriceService calculatePriceService;
	private final EmailService emailService;
	private final TimeManager timeManager;
	private final PropertyService propertyService;
	private final ShiftService shiftService;
	private final TimerOfPauseService timerOfPauseService;
	private final PriceNameProperties priceNameProperties;
	private final ProductService productService;
	private final IngredientsService ingredientsService;
	private final ConfirmTokenService confirmTokenService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateControllerServiceImpl.class);

	@Autowired
	public CalculateControllerServiceImpl(DebtService debtService,
										  CalculatePriceService calculatePriceService,
										  EmailService emailService,
										  TimeManager timeManager,
										  ClientService clientService,
										  CalculateService calculateService,
										  BoardService boardService,
										  PropertyService propertyService,
										  ShiftService shiftService,
										  CardService cardService,
										  TimerOfPauseService timerOfPauseService,
										  PriceNameProperties priceNameProperties,
										  ProductService productService,
										  IngredientsService ingredientsService,
										  ConfirmTokenService confirmTokenService) {
		this.debtService = debtService;
		this.calculatePriceService = calculatePriceService;
		this.emailService = emailService;
		this.timeManager = timeManager;
		this.clientService = clientService;
		this.calculateService = calculateService;
		this.boardService = boardService;
		this.propertyService = propertyService;
		this.shiftService = shiftService;
		this.cardService = cardService;
		this.timerOfPauseService = timerOfPauseService;
		this.priceNameProperties = priceNameProperties;
		this.productService = productService;
		this.ingredientsService = ingredientsService;
		this.confirmTokenService = confirmTokenService;
	}

	@Override
	public void createCalculate(Long id, Long number, String description) {
		Board board = boardService.getOne(id);
		Calculate calculate = new Calculate();
		List<Client> list = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			Client client = new Client();
			client.setTimeStart(timeManager.getDateTime());
			list.add(client);
		}
		clientService.saveAll(list);
		calculate.setDescription(description);
		calculate.setBoard(board);
		calculate.setClient(list);
		shiftService.getLast().getCalculates().add(calculate);
		shiftService.getLast().getClients().addAll(list);
		calculateService.save(calculate);
	}

	@Override
	public void createCalculateWithCard(Long id, Long number, String description, Long idCard) {
		Card card = cardService.getOne(idCard);
		List<Card> cards = new ArrayList<>();
		cards.add(card);

		Board board = boardService.getOne(id);
		Calculate calculate = new Calculate();
		List<Client> list = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			Client client = new Client();
			client.setTimeStart(timeManager.getDateTime());
			list.add(client);
		}
		clientService.saveAll(list);
		calculate.setCards(cards);
		calculate.setDescription(description);
		calculate.setBoard(board);
		calculate.setClient(list);
		shiftService.getLast().getCalculates().add(calculate);
		shiftService.getLast().getClients().addAll(list);
		calculateService.save(calculate);
	}

	@Override
	public void refreshBoard(Long idC, Long idB) {
		Board board = boardService.getOne(idB);
		Calculate calculate = calculateService.getOne(idC);
		calculate.setBoard(board);
		calculateService.save(calculate);
	}

	@Override
	public void addClient(Long id, Long number, String description) {
		Calculate calculate = calculateService.getOne(id);
		List<Client> list = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			Client client = new Client();
			client.setTimeStart(timeManager.getDateTime());
			client.setDescription(description);
			list.add(client);
		}
		clientService.saveAll(list);
		List<Client> list1 = calculate.getClient();
		list1.addAll(list);
		calculate.setClient(list1);
		shiftService.getLast().getClients().addAll(list);
		calculateService.save(calculate);
	}

	@Override
	public List<Client> calculatePrice() {
		List<Calculate> calculates = calculateService.getAllOpen();
		List<Client> clients = new ArrayList<>();
		for (Calculate calculate : calculates) {
			for (Client client : calculate.getClient()) {
				if (client.isPausedIndex()) {
					calculatePriceService.calculatePriceTimeIfWasPause(client);
				} else {
					calculatePriceService.calculatePriceTime(client);
				}
				calculatePriceService.addDiscountOnPriceTime(client);
				calculatePriceService.getAllPrice(client);
				clients.add(client);
			}
		}
		clientService.saveAll(clients);
		return clients;
	}

	@Override
	public List<Client> calculatePrice(Long calculateId) {
		Calculate calculate = calculateService.getAllOpenOnCalculate(calculateId);
		List<Client> clients = calculate.getClient();
		for (Client client : clients) {
			if (client.isPausedIndex()) {
				calculatePriceService.calculatePriceTimeIfWasPause(client);
			} else {
				calculatePriceService.calculatePriceTime(client);

			}
			calculatePriceService.addDiscountOnPriceTime(client);
			calculatePriceService.getAllPrice(client);
		}
		clientService.saveAll(clients);
		return clients;
	}

	@Override
	public List<Client> outputClients(long[] clientsId) {
		if (clientsId == null) {
			return null;
		}
		List<Client> clients = clientService.findByIdIn(clientsId);
		for (Client client : clients) {
			calculatePriceService.payWithCardAndCache(client);
		}
		clientService.saveAll(clients);
		return clients;
	}

	@Override
	public List<Client> closeClient(long[] clientsId, Long calculateId) {

		if (clientsId == null) {
			throw new ClientDataException("Ошибка передачи клиентских ID");
		}

		List<Client> listClient = clientService.findByIdIn(clientsId);

		Map<Long, Double> balanceBeforeDeduction = new HashMap<>();

		clientService.findCardByClientIdIn(clientsId)
				.forEach(card -> balanceBeforeDeduction.put(card.getId(), card.getBalance()));

		sendBalanceInfoAfterDeduction(listClient, balanceBeforeDeduction);

		return closeClientList(listClient, calculateId);
	}

	@Override
	public List<Client> closeClientList(List<Client> listClient, Long calculateId) {
		List<Card> listCard = new ArrayList<>();
		for (Client client : listClient) {
			if (client.isPause()) {
				throw new ClientDataException("На форме расчёта присутствуют клиетны на паузе!");
			}
			client.setState(false);
			Card clientCard = client.getCard();
			if (clientCard == null) {
				continue;
			}

			setBalanceAndSaveInvitedCard(clientCard);

			clientCard.setVisitDate(timeManager.getDate());
			cardService.save(clientCard);

			clientCard.setBalance(clientCard.getBalance() - client.getPayWithCard());
			listCard.add(clientCard);
		}

		cardService.saveAll(listCard);
		List<Client> clients = clientService.saveAll(listClient);

		findLeastOneOpenClientAndCloseCalculation(calculateId);

		return clients;
	}

	@Override
	public List<Client> closeNewSumClient(Double modifiedAmount, String password, long[] clientsId, Long calculateId) {
		if (clientsId == null) {
			throw new ClientDataException("Ошибка передачи клиентских ID");
		}

		if (modifiedAmount == null || password.equals("")) {
			throw new PasswordException("Поле суммы и пароля не могут быть пустыми!");
		}

		if (!confirmTokenService.confirm(password, Target.RECALCULATE)) {
			throw new PasswordException("Пароль не действителен!");
		}

		double allPrice = 0;
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> listClient = clientService.findByIdIn(clientsId);

		for (Client client : listClient) {
			allPrice += client.getAllPrice();
		}

		if (modifiedAmount < 0) {
			throw new ClientDataException("Нельзя указывать отрицательную сумму!");
		} else if (modifiedAmount > allPrice){
			double difference = modifiedAmount - allPrice;
			double profitRecalculation = calculate.getProfitRecalculation();
			calculate.setProfitRecalculation(difference + profitRecalculation);
		} else {
			double difference = allPrice - modifiedAmount;
			double lossRecalculation = calculate.getLossRecalculation();
			calculate.setLossRecalculation(difference + lossRecalculation);
		}

		calculateService.save(calculate);
		return closeClient(clientsId, calculateId);
	}

	@Override
	public Calculate closeAndRecalculate(Double modifiedAmount, String password, Long calculateId) {
		if (modifiedAmount == null || password.equals("")) {
			throw new PasswordException("Поле суммы и пароля не могут быть пустыми!");
		}

		if (!confirmTokenService.confirm(password, Target.RECALCULATE)) {
			throw new PasswordException("Пароль не действителен!");
		}

		double allPrice = 0;
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> listClient = calculate.getClient();

		for (Client client : listClient) {
			if (!client.isDeleteState()) {
				client.setPause(false);
				allPrice += client.getAllPrice();
			}
		}

		double profitRecalculation = calculate.getProfitRecalculation();
		double lossRecalculation= calculate.getLossRecalculation();
		allPrice += profitRecalculation;
		allPrice -= lossRecalculation;

		if (modifiedAmount < 0) {
			throw new ClientDataException("Нельзя указывать отрицательную сумму!");
		} else if (modifiedAmount > allPrice){
			double difference = modifiedAmount - allPrice;
			calculate.setProfitRecalculation(profitRecalculation + difference);
		} else {
			double difference = allPrice - modifiedAmount;
			calculate.setLossRecalculation(lossRecalculation + difference);
		}

		Calculate savedCalculate = calculateService.save(calculate);
		listClient = listClient.stream().filter(Client::isState).collect(Collectors.toList());
		closeClientList(listClient, calculateId);
		return savedCalculate;
	}

	@Override
	public void recalculate(Double modifiedAmount, String password, Long calculateId) {
		if (modifiedAmount == null || password.equals("")) {
			throw new PasswordException("Поле суммы и пароля не могут быть пустыми!");
		}

		if (!confirmTokenService.confirm(password, Target.RECALCULATE)) {
			throw new PasswordException("Пароль не действителен!");
		}

		double allPrice = 0;
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> listClient = calculate.getClient();

		double profitRecalculation = calculate.getProfitRecalculation();
		double lossRecalculation= calculate.getLossRecalculation();
		allPrice += profitRecalculation;
		allPrice -= lossRecalculation;

		for (Client client : listClient) {
			if (!client.isDeleteState()) {
				allPrice += client.getAllPrice();
			}
		}

		if (modifiedAmount < 0) {
			throw new ClientDataException("Нельзя указывать отрицательную сумму!");
		} else if (modifiedAmount > allPrice){
			double difference = modifiedAmount - allPrice;
			calculate.setProfitRecalculation(profitRecalculation + difference);
		} else {
			double difference = allPrice - modifiedAmount;
			calculate.setLossRecalculation(lossRecalculation + difference);
		}

		calculateService.save(calculate);
	}

	private void findLeastOneOpenClientAndCloseCalculation(Long calculateId) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculate.getClient();
		for (Client client : clients) {
			if (client.isState() && !client.isDeleteState()) {
				return;
			}
		}
		calculate.setState(false);
		calculateService.save(calculate);
	}

	private void setBalanceAndSaveInvitedCard(Card clientCard) {
		if (clientCard.getWhoInvitedMe() != null && clientCard.getVisitDate() == null) {
			Card invitedCard = cardService.getOne(clientCard.getWhoInvitedMe());
			Property refBonusProperty = propertyService.findByName(priceNameProperties.getRefBonus());
			Double invitedCardBalance = invitedCard.getBalance();
			Double refBonus = Double.valueOf(refBonusProperty.getValue());
			invitedCard.setBalance(invitedCardBalance + refBonus);
			cardService.save(invitedCard);
		}
	}

	@Override
	public List<Client> closeClientDebt(String debtorName, long[] clientsId, Long calculateId, Double paidAmount) {
		if (clientsId == null) {
			throw new DebtDataException("Ошибка передачи Id клиентов!");
		}

		List<Client> clients = clientService.findByIdIn(clientsId);
		Shift lastShift = shiftService.getLast();
		Calculate calculate = calculateService.getOne(calculateId);
		double totalDebtAmount = 0.0D;

		if (clients != null && !clients.isEmpty()) {
			for (Client client : clients) {
				totalDebtAmount += client.getAllPrice();
				client.setState(false);
			}

			if (paidAmount < 0) {
				throw new DebtDataException("Нельзя указывать отрицательную сумму!");
			} else if (paidAmount > totalDebtAmount) {
				throw new DebtDataException("Вы возвращаете сумму большую чем долг");
			} else if (paidAmount != 0 && paidAmount < totalDebtAmount) {
				totalDebtAmount = totalDebtAmount - paidAmount;
			} else if (paidAmount == totalDebtAmount) {
				throw new DebtDataException("Сумма долга равна уплаченной сумме!");
			}

			Debt debt = new Debt();
			debt.setDate(lastShift.getShiftDate());
			debt.setDebtor(debtorName);
			debt.setDebtAmount(totalDebtAmount);
			debt.setShift(lastShift);
			debt.setCalculate(calculate);
			lastShift.addGivenDebtToList(debt);
			calculate.addGivenDebtToSet(debt);
			debtService.save(debt);

			findLeastOneOpenClientAndCloseCalculation(calculateId);
		} else {
			throw new DebtDataException("Не найдено ни одного клиента в базе!");
		}

		return clients;
	}

	@Override
	public void deleteClients(long[] clientsId, Long calculateId) {
		if (clientsId == null) {
			return;
		}
		List<Client> clients = clientService.findByIdIn(clientsId);
		Set<LayerProduct> products = new HashSet<>();

		for (Client client : clients) {
			products.addAll(client.getLayerProducts());
			client.setDeleteState(true);
			client.setState(false);
		}

		checkIngredients(products);
		clientService.saveAll(clients);
		boolean flag = false;
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients1 = calculate.getClient();
		for (Client client : clients1) {
			if (client.isState() && !client.isDeleteState()) {
				flag = true;
				break;
			}
		}

		for (Client client : clients1) {

			if (client.isState() && !client.isDeleteState()) {

				double menuPrice = 0D;
				Map<Long, Double> productMap = new HashMap<>();

				for (LayerProduct product : client.getLayerProducts()) {

					if (!product.getClients().isEmpty()) {

						double oneClientShare = product.getCost() / product.getClients().size();

						long deletedClients = product.getClients().stream()
								.filter(Client::isDeleteState)
								.count();

						long remainingClients = product.getClients().stream()
								.filter(c -> c.isState() && !c.isDeleteState())
								.count();

						double mapStoredPrice = productMap.get(product.getProductId()) != null ? productMap.get(product.getProductId()) : 0.0;

						if (remainingClients != product.getClients().size()) {
							double addPrice = deletedClients * oneClientShare + (oneClientShare / remainingClients);
							menuPrice += addPrice;
							productMap.put(product.getProductId(), Math.round((mapStoredPrice + addPrice) * 100) / 100.00);
						} else {
							menuPrice += oneClientShare;
							productMap.put(product.getProductId(), Math.round((mapStoredPrice + oneClientShare) * 100) / 100.00);
						}
					}
				}
				client.setPriceMenu(Math.round(menuPrice * 100) / 100.00);
				client.setProductOnPrice(productMap);
			}
		}

		clientService.saveAll(clients1);
		if (!flag) {
			calculate.setState(false);
			calculateService.save(calculate);
		}
	}

	@Override
	public void deleteCalculate(String password, Long calculateId) {
		if (password.equals("")) {
			throw new PasswordException("Заполните поле пароля перед отправкой!");
		}
		if (!confirmTokenService.confirm(password, Target.DELETE_CALC)) {
			throw new PasswordException("Пароль не действителен!");
		}

		Calculate calculate = calculateService.getOne(calculateId);

		List<Client> clients = calculate.getClient();
		List<Debt> debts = debtService.findByCalculateId(calculateId);
		Set<LayerProduct> products = new HashSet<>();
		logger.info("Удаление стола с описанием: " + calculate.getDescription() + " и id: " + calculate.getId());

		for (Client client : clients) {
			if (client.isState() || !client.isDeleteState()) {
				client.setState(false);
				client.setDeleteState(true);
				products.addAll(client.getLayerProducts());
			}
		}

		for (Debt debt : debts) {
			debt.setVisible(false);
		}

		checkIngredients(products);
		clientService.saveAll(clients);
		debtService.saveAll(debts);
		calculate.setState(false);
		calculateService.save(calculate);
	}

	private void checkIngredients(Set<LayerProduct> products) {
		Map<Ingredients, Double> deletedIng = new HashMap<>();
		for (LayerProduct layerProduct : products) {
			Product product = (productService.findOne(layerProduct.getProductId()));
			Map<Ingredients, Double> receipt = product.getRecipe();
			if (!receipt.isEmpty()) {
				boolean isDeleted = layerProduct.getClients().stream().noneMatch(Client::isState);
				if (isDeleted) {
					receipt.forEach((k, v) -> deletedIng.merge(k, v, (v1, v2) -> v1 + v2));
				}
			}
		}
		ingredientsService.retrieveIngredientAmount(deletedIng);
	}

	@Override
	public Long addCardOnClient(Long calculateId, Long clientId, Long cardId) {
		Client client = clientService.getOne(clientId);
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = calculate.getClient();
		boolean flag = false;
		if (cardId == -1) {
			client.setDiscountWithCard(0L);
			client.setCard(null);
		} else {
			Card card = cardService.getOne(cardId);
			for (Client cl : clients) {
				if (cl.getCard() == card) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				client.setDiscountWithCard(card.getDiscount());
			}
			client.setCard(card);
		}
		clientService.save(client);
		return client.getDiscountWithCard();
	}

	private void sendBalanceInfoAfterDeduction(List<Client> clients, Map<Long, Double> mapOfBalanceBeforeDeduction) {
		Map<Long, Client> uniqueClientsForCard = new HashMap<>();
		clients
				.stream()
				.filter(client -> client.getCard() != null && client.getCard().getEmail() != null && client.getPayWithCard() > 0.0d)
				.forEach(client -> uniqueClientsForCard.put(client.getCard().getId(), client));

		uniqueClientsForCard.values().forEach(client -> {
			Double balanceAfterDeduction = client.getCard().getBalance();
			Double balanceBeforeDeduction = mapOfBalanceBeforeDeduction.get(client.getCard().getId());
			Double deductionAmount = balanceBeforeDeduction - balanceAfterDeduction;
			emailService.sendBalanceInfoAfterDeduction(balanceAfterDeduction, deductionAmount, client.getCard().getEmail());
		});
	}

	@Override
	public Client pauseClient(Long clientId) {
		Client client = clientService.getOne(clientId);
		TimerOfPause timer = timerOfPauseService.findTimerOfPauseByIdOfClient(clientId);
		if (timer == null) { // if this first pause on this calc
			timer = new TimerOfPause();
			timer.setIdOfClient(clientId);
			timer.setStartTime(timeManager.getDateTimeWithoutSeconds());
			client.setPause(true);
			client.setPausedIndex(true);
		} else {
			timer.setStartTime(timeManager.getDateTimeWithoutSeconds());      // if this second or more pause on this calc
			client.setPause(true);
		}
		timerOfPauseService.save(timer);
		return clientService.save(client);
	}

	@Override
	public Client unpauseClient(Long clientId) {
		Client client = clientService.getOne(clientId);
		TimerOfPause timer = timerOfPauseService.findTimerOfPauseByIdOfClient(clientId);
		Long timeOfPastPauses = timer.getWholeTimePause();
		timer.setEndTime(timeManager.getDateTimeWithoutSeconds());
		long fullPauseTime = ChronoUnit.MINUTES.between(timer.getStartTime(), timer.getEndTime());
		if (timeOfPastPauses != null) {
			fullPauseTime += timeOfPastPauses;
		}
		timer.setWholeTimePause(fullPauseTime);
		client.setPause(false);

		timerOfPauseService.save(timer);
		return clientService.save(client);
	}

	@Override
	public String getClientsAndDesc() {
		List<Calculate> calculates = calculateService.getAllOpen();
		int amount = 0;
		for (Calculate calculate : calculates) {
			amount += calculate.getClient().stream()
					.filter(Client::isState)
					.count();
		}

		if (amount >= 10 && amount <= 20) {
			return "Сейчас в заведении " + amount + " гостей";
		}

		int lastDigit = amount % 10;
		String text;

		if(lastDigit == 1) {
			text = "Сейчас в заведении " + amount + " гость";
		} else if(lastDigit >= 2 && lastDigit <= 4) {
			text = "Сейчас в заведении " + amount + " гостя";
		} else {
			text = "Сейчас в заведении " + amount + " гостей";
		}

		return text;
	}
}
