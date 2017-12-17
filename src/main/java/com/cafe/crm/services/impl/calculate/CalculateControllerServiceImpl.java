package com.cafe.crm.services.impl.calculate;

import ch.qos.logback.classic.Logger;
import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.exceptions.debt.DebtDataException;
import com.cafe.crm.exceptions.modifiedAmountException.ModifiedAmountException;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.*;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateControllerService;
import com.cafe.crm.services.interfaces.calculate.CalculatePriceService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.TimerOfPauseService;
import com.cafe.crm.services.interfaces.card.CardService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.email.EmailService;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.TimeManager;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Logger logger;
	private final TimerOfPauseService timerOfPauseService;
	private final PriceNameProperties priceNameProperties;
	private final ProductService productService;
	private final IngredientsService ingredientsService;
	private final PasswordEncoder encoder;

	@Autowired
	public CalculateControllerServiceImpl(DebtService debtService,
										  CalculatePriceService calculatePriceService,
										  EmailService emailService,
										  TimeManager timeManager,
										  ClientService clientService,
										  CalculateService calculateService,
										  @Qualifier(value = "logger") Logger logger,
										  BoardService boardService,
										  PropertyService propertyService,
										  ShiftService shiftService,
										  CardService cardService,
										  TimerOfPauseService timerOfPauseService,
										  PriceNameProperties priceNameProperties,
										  ProductService productService,
										  IngredientsService ingredientsService,
										  PasswordEncoder encoder) {
		this.debtService = debtService;
		this.calculatePriceService = calculatePriceService;
		this.emailService = emailService;
		this.timeManager = timeManager;
		this.clientService = clientService;
		this.calculateService = calculateService;
		this.logger = logger;
		this.boardService = boardService;
		this.propertyService = propertyService;
		this.shiftService = shiftService;
		this.cardService = cardService;
		this.timerOfPauseService = timerOfPauseService;
		this.priceNameProperties = priceNameProperties;
		this.productService = productService;
		this.ingredientsService = ingredientsService;
		this.encoder = encoder;
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
	public void closeClient(long[] clientsId, Long calculateId) {

		if (clientsId == null) {
			throw new ClientDataException("Ошибка передачи клиентских ID");
		}

		List<Client> listClient = clientService.findByIdIn(clientsId);
		List<Card> listCard = new ArrayList<>();
		Map<Long, Double> balanceBeforeDeduction = new HashMap<>();

		clientService.findCardByClientIdIn(clientsId)
				.forEach(card -> balanceBeforeDeduction.put(card.getId(), card.getBalance()));

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
		clientService.saveAll(listClient);

		findLeastOneOpenClientAndCloseCalculation(calculateId);
		sendBalanceInfoAfterDeduction(listClient, balanceBeforeDeduction);
	}

	@Override
	public void closeNewSumClient(Double modifiedAmount, String password, long[] clientsId, Long calculateId) {
		if (clientsId == null) {
			throw new ClientDataException("Ошибка передачи клиентских ID");
		}

		if (modifiedAmount == null || password.equals("")) {
			throw new ClientDataException("Поле суммы и пароля не могут быть пустыми!");
		}

		Property property = propertyService.findByName("masterKey");

		if (!encoder.matches(password, property.getValue())) {
			throw new ModifiedAmountException("Пароли не совпадают!");
		}

		double allPrice = 0;
		Shift lastShift = shiftService.getLast();
		List<Client> listClient = clientService.findByIdIn(clientsId);

		for (Client client : listClient) {
			allPrice += client.getAllPrice();
		}

		if (modifiedAmount < 0) {
			throw new ModifiedAmountException("Нельзя указывать отрицательную сумму!");
		} else if (modifiedAmount > allPrice){
			double difference = modifiedAmount - allPrice;
			lastShift.setProfitRecalculation(difference);
		} else {
			double difference = allPrice - modifiedAmount;
			lastShift.setLossRecalculation(difference);
		}

		shiftService.saveAndFlush(lastShift);
		closeClient(clientsId, calculateId);
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
	public void closeClientDebt(String debtorName, long[] clientsId, Long calculateId, Double paidAmount) {
		if (clientsId == null) {
			throw new DebtDataException("Ошибка передачи Id клиентов!");
		}

		List<Client> clients = clientService.findByIdIn(clientsId);
		Shift lastShift = shiftService.getLast();
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
			lastShift.addGivenDebtToList(debt);
			findLeastOneOpenClientAndCloseCalculation(calculateId);
			debtService.save(debt);
		} else {
			throw new DebtDataException("Не найдено ни одного клиента в базе!");
		}
	}

	@Override
	public void deleteClients(long[] clientsId, Long calculateId) {
		if (clientsId == null) {
			return;
		}
		List<Client> clients = clientService.findByIdIn(clientsId);
		List<LayerProduct> products = new ArrayList<>();
		for (Client client : clients) {
			products.addAll(client.getLayerProducts());
			client.setDeleteState(true);
			client.setState(false);
			logger.info("Удаление клиента c описанием:" + client.getDescription() + "и id: " + client.getId());
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
		if (!flag) {
			calculate.setState(false);
			calculateService.save(calculate);
		}
	}

	private void checkIngredients(List<LayerProduct> products) {
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
	public void pauseClient(Long clientId) {
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
		clientService.save(client);
	}

	@Override
	public void unpauseClient(Long clientId) {
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
		clientService.save(client);
	}
}
