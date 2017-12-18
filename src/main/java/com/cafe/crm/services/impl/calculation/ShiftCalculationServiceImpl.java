package com.cafe.crm.services.impl.calculation;


import com.cafe.crm.dto.*;
import com.cafe.crm.exceptions.NoStatData;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.note.Note;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.debt.DebtRepository;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.note.NoteService;
import com.cafe.crm.services.interfaces.receipt.ReceiptService;
import com.cafe.crm.services.interfaces.salary.UserSalaryDetailService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.yc.easytransformer.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftCalculationServiceImpl implements ShiftCalculationService {
	private final CostService costService;
	private final ShiftService shiftService;
	private final Transformer transformer;
	private final NoteService noteService;
	private final ProductService productService;
	private final UserSalaryDetailService userSalaryDetailService;
	private final DebtService debtService;
	@Autowired
	private ReceiptService receiptService;

	@Autowired
	public ShiftCalculationServiceImpl(CostService costService, ShiftService shiftService, Transformer transformer,
									   NoteService noteService, ProductService productService,
									   UserSalaryDetailService userSalaryDetailService, DebtService debtService) {
		this.costService = costService;
		this.shiftService = shiftService;
		this.transformer = transformer;
		this.noteService = noteService;
		this.productService = productService;
		this.userSalaryDetailService = userSalaryDetailService;
		this.debtService = debtService;
	}

	@Override
	public double getTotalCashBox(Set<Shift> allShiftsBetween) {
		if (!allShiftsBetween.isEmpty()) {
			Shift lastShift = new Shift();

			long maxId = 0;
			for (Shift shift : allShiftsBetween) {
				long shiftId = shift.getId();
				if (maxId == 0) {
					maxId = shiftId;
				}
				if (shiftId >= maxId) {
					maxId = shiftId;
					lastShift = shift;
				}
			}
			return lastShift.getCashBox() + lastShift.getBankCashBox();
		} else {
			return 0D;
		}
	}

	@Override
	public UserSalaryDetail getUserSalaryDetail(User user, int percent, int bonus, Shift shift) {
		int salary = user.getShiftSalary() + bonus + percent;
		int shiftSalary = user.getShiftSalary();
		int shiftAmount = user.getShifts().size();
		return new UserSalaryDetail(user, salary, shiftSalary, shiftAmount, bonus, shift);
	}


	@Override
	public TotalStatisticView createTotalStatisticView(LocalDate from, LocalDate to) {
		Set<Shift> shifts = shiftService.findByDates(from, to);
		double profit = 0D;
		double totalShiftSalary = 0D;
		double otherCosts = 0D;
		List<UserDTO> users = getUserDTOList(shifts, from, to);
		Set<Calculate> allCalculate = new HashSet<>();
		Map<Client, ClientDetails> clientsOnDetails = new HashMap<>();
		List<Cost> otherCost = new ArrayList<>();
		List<Debt> givenDebts = new ArrayList<>();
		List<Debt> repaidDebt = new ArrayList<>();
		List<Receipt> receiptAmount = new ArrayList<>();
		if (shifts == null) {
			return new TotalStatisticView(profit, totalShiftSalary, otherCosts, users, clientsOnDetails, otherCost,
					givenDebts, repaidDebt);
		}
		for (Shift shift : shifts) {
			allCalculate.addAll(shift.getCalculates());
			otherCost.addAll(costService.findByShiftId(shift.getId()));
			givenDebts.addAll(shift.getGivenDebts());
			repaidDebt.addAll(shift.getRepaidDebts());
			receiptAmount.addAll(receiptService.findByShiftId(shift.getId()));
		}
		clientsOnDetails = getClientsOnDetails(allCalculate);
		givenDebts.removeAll(repaidDebt);
		for (UserDTO user : users) {
			totalShiftSalary += user.getSalary();
		}
		for (Cost cost : otherCost) {
			otherCosts += cost.getPrice() * cost.getQuantity();
		}
		for (Map.Entry<Client, ClientDetails> entry : clientsOnDetails.entrySet()) {
			profit += entry.getValue().getAllDirtyPrice();
		}
		for (Receipt receipt : receiptAmount){
			profit += receipt.getReceiptAmount();
		}
		for (Debt repDebt : repaidDebt) {
			if (repDebt.getDate().isBefore(from)) {
				profit += repDebt.getDebtAmount();
			}
		}
		for (Debt givDebt : givenDebts) {
			profit -= givDebt.getDebtAmount();
		}
		return new TotalStatisticView(profit, totalShiftSalary, otherCosts, users, clientsOnDetails, otherCost,
				givenDebts, repaidDebt);
	}

	private List<UserDTO> getUserDTOList(Set<Shift> shifts, LocalDate from, LocalDate to) {
		List<UserDTO> userDTOList = new ArrayList<>();
		Set<User> userSet = new HashSet<>();
		for (Shift shift : shifts) {
			userSet.addAll(shift.getUsers());
		}
		for (User user : userSet) {
			List<UserSalaryDetail> details = userSalaryDetailService.findByUserIdAndShiftDateBetween(user.getId(), from, to);
			if (details.size() == 0){
				throw new NoStatData("There is no shift resources to calculate statistical resources");
			}
			UserSalaryDetail lastDetail = details.get(details.size() - 1);
			int salary = 0;
			int bonus = 0;
			int shiftAmount = lastDetail.getShiftAmount();
			int shiftSalary = lastDetail.getShiftSalary();

			for (UserSalaryDetail detail : details) {
				salary += detail.getSalary();
				bonus += detail.getBonus();
			}

			UserDTO userDTO = transformer.transform(user, UserDTO.class);
			userDTO.setSalary(salary);
			userDTO.setShiftSalary(shiftSalary);
			userDTO.setShiftAmount(shiftAmount);
			userDTO.setBonus(bonus);
			userDTOList.add(userDTO);
		}
		return userDTOList;
	}

	@Override
	public List<Client> getClients(Shift shift) {
		Set<Client> allClients = shift.getClients();
		List<Client> stateClient = new ArrayList<>(allClients.size());
		for (Client client : allClients) {
			if (!client.isDeleteState()) {
				stateClient.add(client);
			}
		}
		return stateClient;
	}

	private List<Client> getClients(Calculate calculate) {
		List<Client> allClients = calculate.getClient();
		List<Client> stateClient = new ArrayList<>(allClients.size());
		for (Client client : allClients) {
			if (!client.isDeleteState()) {
				stateClient.add(client);
			}
		}
		return stateClient;
	}

	@Override
	public Map<Client, ClientDetails> getClientsOnDetails (Set<Calculate> allCalculate) {
		Map<Client, ClientDetails> clientsOnDetails = new HashMap<>();
		List<Client> clients = new ArrayList<>();
		for (Calculate calculate : allCalculate) {
			clients.addAll(calculate.getClient());
		}
		for (Client client : clients) {
			if (!client.isDeleteState()) {
				ClientDetails details = getClientDetails(client);
				clientsOnDetails.put(client, details);
			}
		}
		return clientsOnDetails;
	}

	private ClientDetails getClientDetails(Client client) {
		double allDirtyPrice;
		double dirtyPriceMenu = 0D;
		double otherPriceMenu = 0D;
		List<LayerProduct> dirtyProduct = new ArrayList<>();
		List<LayerProduct> otherProduct = new ArrayList<>();
		for (LayerProduct product : client.getLayerProducts()) {
			if (product.isDirtyProfit()) {
				dirtyPriceMenu += product.getCost() / product.getClients().size();
				dirtyProduct.add(product);
			} else {
				otherPriceMenu += product.getCost() / product.getClients().size();
				otherProduct.add(product);
			}
		}
		allDirtyPrice = client.getPriceTime() + Math.round(dirtyPriceMenu) - client.getPayWithCard();
		return new ClientDetails(allDirtyPrice, Math.round(otherPriceMenu), Math.round(dirtyPriceMenu),
				dirtyProduct, otherProduct);
	}

	private List<String> getDirtyMenu(Calculate calculate) {
		List<Client> clients = calculate.getClient();
		Set<LayerProduct> dirtyMenu = new HashSet<>();
		List<LayerProduct> products = new ArrayList<>();
		for (Client client : clients) {
			if (!client.isDeleteState()) {
				products.addAll(client.getLayerProducts());
			}
		}
		for (LayerProduct product : products) {
			if (product.isDirtyProfit()) {
				dirtyMenu.add(product);
			}
		}
		return getContent(dirtyMenu);
	}

	private List<String> getOtherMenu(Calculate calculate) {
		List<Client> clients = calculate.getClient();
		Set<LayerProduct> otherMenu = new HashSet<>();
		List<LayerProduct> products = new ArrayList<>();
		for (Client client : clients) {
			if (!client.isDeleteState()) {
				products.addAll(client.getLayerProducts());
			}
		}
		for (LayerProduct product : products) {
			if (!product.isDirtyProfit()) {
				otherMenu.add(product);
			}
		}
		return getContent(otherMenu);
	}

	@Override
	public List<CalculateDTO> getCalculates(Shift shift) {
		List<Calculate> sortedList = new ArrayList<>(shift.getCalculates());
		sortedList.sort(Comparator.comparing(Calculate::getId));

		List<CalculateDTO> calculates = new ArrayList<>();

		for (Calculate calculate : sortedList) {
			if (!isCalcDeleted(calculate)) {
				CalculateDTO calcDto = transformer.transform(calculate, CalculateDTO.class);
				calcDto.setClient(getClients(calculate));
				calcDto.setDirtyOrder(getDirtyMenu(calculate));
				calcDto.setOtherOrder(getOtherMenu(calculate));
				calculates.add(calcDto);
			}
		}
		return calculates;
	}

	private boolean isCalcDeleted(Calculate calculate) {
		return calculate.getClient().stream().allMatch(Client::isDeleteState);
	}

	private List<String> getContent(Set<LayerProduct> products) {
		List<String> contentList = new ArrayList<>();
		List<String> checkReduplication = new ArrayList<>();
		if (!products.isEmpty()) {
			for (LayerProduct product : products) {
				String name = product.getName();
				if (!checkReduplication.contains(name)) {
					checkReduplication.add(name);
					StringBuilder content = new StringBuilder();
					long productNum = products.stream().filter(p -> p.getName().equals(name)).count();
					content.append(name).append("(").append(productNum).append(")");
					contentList.add(content.toString());
				}
			}
		}
		return contentList;
	}

	@Override
	public DetailStatisticView createDetailStatisticView(Shift shift) {
		LocalDate shiftDate = shift.getShiftDate();
		double cashBox = shift.getCashBox() + shift.getBankCashBox();
		double allPrice = getAllPrice(shift);
		int clientsNumber = getClients(shift).size();
		List<UserDTO> usersOnShift = getUserDTOList(shift);
		Set<UserSalaryDetail> salaryDetails = shift.getUserSalaryDetail();
		Set<CalculateDTO> allCalculate = new HashSet<>();
		List<Cost> otherCost = costService.findByShiftId(shift.getId());
		List<Receipt> receiptAmount = receiptService.findByShiftId(shift.getId());
		double allSalaryCost = 0D;
		double allOtherCost = 0D;
		double repaidDebts = 0D;
		double givenDebts = 0D;
		double receiptsSum = 0D;

		for (Calculate calculate : shift.getCalculates()) {
			if (!isCalcDeleted(calculate)) {
				CalculateDTO calcDto = transformer.transform(calculate, CalculateDTO.class);
				calcDto.setClient(calculate.getClient().stream().filter(u -> !u.isDeleteState()).collect(Collectors.toList()));
				allCalculate.add(calcDto);
			}
		}
		for (UserSalaryDetail detail : salaryDetails) {
			allSalaryCost += detail.getSalary();
		}
		for (Cost otherGood : otherCost) {
			allOtherCost += otherGood.getPrice() * otherGood.getQuantity();
		}
		for (Debt debt : shift.getGivenDebts()) {
			givenDebts += debt.getDebtAmount();
		}
		for (Debt debt : shift.getRepaidDebts()) {
			repaidDebts += debt.getDebtAmount();
		}
		for (Receipt receipt : receiptAmount){
			receiptsSum +=receipt.getReceiptAmount();
		}

		return new DetailStatisticView(shiftDate, cashBox, allPrice, clientsNumber,
				usersOnShift, salaryDetails, allCalculate, allSalaryCost, allOtherCost, otherCost,
				repaidDebts, givenDebts, receiptsSum);
	}

	private List<UserDTO> getUserDTOList(Shift shift) {
		List<UserDTO> userDTOList = new ArrayList<>();
		for (User user : shift.getUsers()) {
			UserSalaryDetail shiftUserDetails = userSalaryDetailService.findFirstByUserIdAndShiftId(user.getId(), shift.getId());
			UserDTO userDTO = transformer.transform(user, UserDTO.class);
			if (shiftUserDetails != null) {
				userDTO.setSalary(shiftUserDetails.getSalary());
				userDTO.setShiftSalary(shiftUserDetails.getShiftSalary());
				userDTO.setShiftAmount(shiftUserDetails.getShiftAmount());
				userDTO.setBonus(shiftUserDetails.getBonus());
			}
			userDTOList.add(userDTO);
		}
		return userDTOList;
	}

	@Override
	public ShiftView createShiftView(Shift shift) {
		List<UserDTO> usersOnShift = transformer.transform(shift.getUsers(), UserDTO.class);
		List<Client> clients = getClients(shift);
		List<Calculate> activeCalculate = new ArrayList<>();
		Set<Calculate> allCalculate = new HashSet<>();
		List<Note> enabledNotes = noteService.findAllByEnableIsTrue();
		Double cashBox = shift.getCashBox();
		Double bankCashBox = shift.getBankCashBox();
		Double totalCashBox;
		int usersTotalShiftSalary = 0;
		Double card = 0D;
		Double allPrice = getAllPrice(shift);

		for (Calculate calculate : shift.getCalculates()) {
			if (!isCalcDeleted(calculate)) {
				allCalculate.add(calculate);
				if (calculate.isState()) {
					activeCalculate.add(calculate);
				}
			}
		}

		Set<LayerProduct> layerProducts = new HashSet<>();

		for (Client client : clients) {
			layerProducts.addAll(client.getLayerProducts());
		}

		Map<Long, Integer> staffPercentBonusesMap = calcStaffPercentBonusesAndGetMap(layerProducts, usersOnShift);


		LocalDate shiftDate = shift.getShiftDate();
		List<Cost> costWithoutUsersSalaries = costService.findByShiftId(shift.getId());
		double otherCosts = 0d;

		for (Cost cost : costWithoutUsersSalaries) {
			otherCosts += (cost.getPrice() * cost.getQuantity());
		}

		for (UserDTO user : usersOnShift) {
			int amountOfPositionsPercent = user.getPositions().stream().filter(PositionDTO::isPositionUsePercentOfSales).mapToInt(PositionDTO::getPercentageOfSales).sum();
			user.setShiftSalary((int) (user.getShiftSalary() + (allPrice * amountOfPositionsPercent) / 100));
			usersTotalShiftSalary += user.getShiftSalary();
		}

		totalCashBox = (cashBox + bankCashBox + allPrice) - (usersTotalShiftSalary + otherCosts);

		return new ShiftView(usersOnShift, clients, activeCalculate, allCalculate,
				cashBox, totalCashBox, usersTotalShiftSalary, card, allPrice, shiftDate, otherCosts, bankCashBox, enabledNotes, staffPercentBonusesMap);
	}

	public double getAllPrice(Shift shift) {
		List<Client> clients = getClients(shift);
		List<Receipt> receiptAmount = receiptService.findByShiftId(shift.getId());
		double allPrice = 0D;
		for (Client client : clients) {
			if (!client.isDeleteState()) {
				allPrice += getAllDirtyPrice(client);
			}
		}
		for (Debt debt : shift.getRepaidDebts()) {
			if (!debt.getShift().getId().equals(shift.getId())) {
				allPrice += debt.getDebtAmount();
			}
		}
		for (Debt debt : shift.getGivenDebts()) {
			allPrice -= debt.getDebtAmount();
		}
		for (Receipt receipt : receiptAmount){
			allPrice +=receipt.getReceiptAmount();
		}
		return allPrice;
	}

	private Double getAllDirtyPrice(Client client) {
		Double dirtyPriceMenu = 0D;
		for (LayerProduct product : client.getLayerProducts()) {
			if (product.isDirtyProfit())
				dirtyPriceMenu += product.getCost() / product.getClients().size();
		}
		return client.getPriceTime() + Math.round(dirtyPriceMenu) - client.getPayWithCard();
	}

	private Map<Long, Integer> calcStaffPercentBonusesAndGetMap(Set<LayerProduct> layerProducts, List<UserDTO> staff) {
		Map<Long, Integer> staffPercentBonusesMap = new HashMap<>();
		Map<PositionDTO, Integer> shiftPercents = new HashMap<>();
		Integer count;

		for (UserDTO user : staff) {
			List<PositionDTO> userPositions = user.getPositions();
			for (PositionDTO positionDTO : userPositions){
				count = shiftPercents.get(positionDTO);
				shiftPercents.put(positionDTO, count == null ? 1 : count + 1);
			}
		}

		for (LayerProduct layerProduct : layerProducts) {

			Long productId = layerProduct.getProductId();
			Product product = productService.findOne(productId);
			Map<Position, Integer> staffPercent = product.getStaffPercent();

			for (UserDTO user : staff) {
				List<PositionDTO> userPositions = user.getPositions();

				for (PositionDTO positionDTO : userPositions) {

					Integer percent = staffPercent.get(transformer.transform(positionDTO, Position.class));
					int shiftPercent = 1;
					if (percent != null) {
						if (shiftPercents.containsKey(positionDTO)){
							shiftPercent = shiftPercents.get(positionDTO);
						}
						int bonus = (int) (layerProduct.getCost() * percent / 100 / shiftPercent);
						user.setShiftSalary(bonus + user.getShiftSalary());
						Integer saveBonus = staffPercentBonusesMap.get(user.getId());
						if (saveBonus == null) {
							staffPercentBonusesMap.put(user.getId(), bonus);
						} else {
							staffPercentBonusesMap.put(user.getId(), bonus + saveBonus);
						}
					}
				}

			}
		}
		return staffPercentBonusesMap;
	}

	@Override
	public void transferFromBankToCashBox(Double transfer) {
		Shift lastShift = shiftService.getLast();
		Double bankCashBox = lastShift.getBankCashBox() + transfer;
		Double cashBox = lastShift.getCashBox() - transfer;

		lastShift.setCashBox(cashBox);
		lastShift.setBankCashBox(bankCashBox);
		shiftService.saveAndFlush(lastShift);
	}

	@Override
	public void transferFromCashBoxToBank(Double transfer) {
		Shift lastShift = shiftService.getLast();
		Double bankCashBox = lastShift.getBankCashBox() - transfer;
		Double cashBox = lastShift.getCashBox() + transfer;

		lastShift.setCashBox(cashBox);
		lastShift.setBankCashBox(bankCashBox);
		shiftService.saveAndFlush(lastShift);
	}

}
