package com.cafe.crm.services.impl.calculation;


import com.cafe.crm.dto.*;
import com.cafe.crm.exceptions.NoStatData;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.cost.CostCategory;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.note.Note;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.cost.CostCategoryService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.note.NoteService;
import com.cafe.crm.services.interfaces.receipt.ReceiptService;
import com.cafe.crm.services.interfaces.salary.UserSalaryDetailService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
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
	private final CostCategoryService costCategoryService;
	private final UserService userService;

	@Autowired
	private ReceiptService receiptService;

	@Autowired
	public ShiftCalculationServiceImpl(CostService costService, ShiftService shiftService, Transformer transformer,
									   NoteService noteService, ProductService productService,
									   UserSalaryDetailService userSalaryDetailService,
									   CostCategoryService costCategoryService, UserService userService) {
		this.costService = costService;
		this.shiftService = shiftService;
		this.transformer = transformer;
		this.noteService = noteService;
		this.productService = productService;
		this.userSalaryDetailService = userSalaryDetailService;
		this.costCategoryService = costCategoryService;
		this.userService = userService;
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
		int totalSalary = user.getTotalSalary();
		int totalBonus = user.getTotalBonus();
		int salaryBalance = user.getSalaryBalance();
		int bonusBalance = user.getBonusBalance();

		int shiftSalary = user.getShiftSalary();
		int shiftAmount = user.getShifts().size();
		return new UserSalaryDetail(user, totalSalary, totalBonus, salaryBalance, bonusBalance, shiftSalary, shiftAmount, shift, false);
	}

	private UserSalaryDetail getPaidUserSalaryDetail(User user, Shift shift) {
		int totalSalary = user.getTotalSalary();
		int totalBonus = user.getTotalBonus();
		int salaryBalance = user.getSalaryBalance();
		int bonusBalance = user.getBonusBalance();
		int shiftSalary = user.getShiftSalary();
		int shiftAmount = user.getShifts().size();
		return new UserSalaryDetail(user, totalSalary, totalBonus, 0, 0, shiftSalary,
				shiftAmount, salaryBalance, bonusBalance, shift, true);
	}


	@Override
	public TotalStatisticView createTotalStatisticView(LocalDate from, LocalDate to) {
		Set<Shift> shifts = shiftService.findByDates(from, to);
		double profit = 0D;
		double alteredCashAmount = 0D;
		double totalShiftSalary = 0D;
		double otherCostsPrice = 0D;
		double profitRecalculate = 0D;
		double lossRecalculate = 0D;
		List<UserDTO> users = getUserDTOList(shifts, from, to);
		Set<Calculate> allCalculate = new HashSet<>();
		Map<Client, ClientDetails> clientsOnDetails = new HashMap<>();
		List<Cost> otherCost = new ArrayList<>();
		List<Debt> givenDebts = new ArrayList<>();
		List<Debt> repaidDebt = new ArrayList<>();
		List<Receipt> receiptAmount = new ArrayList<>();
		if (shifts == null) {
			return new TotalStatisticView(profit, alteredCashAmount, totalShiftSalary, otherCostsPrice, profitRecalculate,
					lossRecalculate, users, clientsOnDetails, otherCost, givenDebts, repaidDebt);
		}
		List<Cost> salaryCost = costService.findSalaryCostByDateBetween(from, to);
		otherCost = costService.findOtherCostByDateBetween(from, to);

		for (Shift shift : shifts) {
			allCalculate.addAll(shift.getCalculates());
			givenDebts.addAll(shift.getGivenDebts());
			repaidDebt.addAll(shift.getRepaidDebts());
			receiptAmount.addAll(receiptService.findByShiftId(shift.getId()));
			alteredCashAmount += shift.getAlteredCashAmount();
		}
		clientsOnDetails = getClientsOnDetails(allCalculate);
		givenDebts.removeAll(repaidDebt);

		for (Cost cost : otherCost) {
			otherCostsPrice += cost.getPrice() * cost.getQuantity();
		}

		for (Cost cost : salaryCost) {
			totalShiftSalary += cost.getPrice();
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
		for (Calculate calculate : allCalculate) {
			if (!isCalcDeleted(calculate)) {
				profitRecalculate += calculate.getProfitRecalculation();
				lossRecalculate += calculate.getLossRecalculation();
			}
		}
		profit += profitRecalculate;
		profit -= lossRecalculate;
		profit = Math.round(profit * 100) / 100.00;
		alteredCashAmount = Math.round(alteredCashAmount * 100) / 100.00;

		return new TotalStatisticView(profit, alteredCashAmount, totalShiftSalary, otherCostsPrice, profitRecalculate,
				lossRecalculate, users, clientsOnDetails, otherCost, givenDebts, repaidDebt);
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
			int totalSalary = 0;
			int totalBonus = 0;
			int salaryBalance = 0;
			int bonusBalance = 0;
			int shiftAmount = lastDetail.getShiftAmount();
			int shiftSalary = lastDetail.getShiftSalary();

			for (UserSalaryDetail detail : details) {
				totalSalary += detail.getTotalSalary();
				totalBonus += detail.getTotalBonus();
				salaryBalance += detail.getSalaryBalance();
				bonusBalance += detail.getBonusBalance();
			}

			UserDTO userDTO = transformer.transform(user, UserDTO.class);
			userDTO.setTotalSalary(totalSalary);
			userDTO.setTotalBonus(totalBonus);
			userDTO.setSalaryBalance(salaryBalance);
			userDTO.setBonusBalance(bonusBalance);
			userDTO.setShiftSalary(shiftSalary);
			userDTO.setShiftAmount(shiftAmount);;
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

		for (Map.Entry<Long, Double> productSet : client.getProductOnPrice().entrySet()) {
			Product product = productService.findOne(productSet.getKey());
			if (product == null) {
				dirtyPriceMenu += client.getLayerProducts().stream()
						.filter(p -> Objects.equals(p.getProductId(), productSet.getKey()))
						.mapToDouble(p -> p.getCost() / p.getClients().size())
						.reduce((price1, price2) -> price1 + price2).orElse(0D);
			} else if (product.getCategory().isDirtyProfit()) {
				dirtyPriceMenu += productSet.getValue();
			} else {
				otherPriceMenu += productSet.getValue();
			}
		}

		for (LayerProduct product : client.getLayerProducts()) {
			if (product.isDirtyProfit()) {
				dirtyProduct.add(product);
			} else {
				otherProduct.add(product);
			}
		}

		allDirtyPrice = client.getPriceTime() + (Math.round(dirtyPriceMenu * 100) / 100.00) - client.getPayWithCard();
		return new ClientDetails(allDirtyPrice, (Math.round(otherPriceMenu * 100) / 100.00),
				(Math.round(dirtyPriceMenu * 100) / 100.00),
				dirtyProduct, otherProduct);
	}

	private List<ProductStat> getDirtyMenu(Calculate calculate) {
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

	private List<ProductStat> getOtherMenu(Calculate calculate) {
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
			double allPrice = calculate.getClient().stream()
					.filter(c -> !c.isDeleteState())
					.mapToDouble(Client::getAllPrice)
					.sum();
			allPrice += calculate.getProfitRecalculation();
			allPrice -= calculate.getLossRecalculation();
			if (!isCalcDeleted(calculate)) {
				CalculateDTO calcDto = transformer.transform(calculate, CalculateDTO.class);
				calcDto.setClient(getClients(calculate));
				calcDto.setDirtyOrder(getDirtyMenu(calculate));
				calcDto.setOtherOrder(getOtherMenu(calculate));
				calcDto.setAllPrice(allPrice);
				calculates.add(calcDto);
			}
		}
		return calculates;
	}

	private boolean isCalcDeleted(Calculate calculate) {
		return calculate.getClient().stream().allMatch(Client::isDeleteState);
	}

	private List<ProductStat> getContent(Set<LayerProduct> products) {
		List<ProductStat> contentList = new ArrayList<>();
		List<Double> checkReduplication = new ArrayList<>();
		if (!products.isEmpty()) {
			for (LayerProduct product : products) {
				String name = product.getName();
				double cost = product.getCost();
				double code = name.hashCode() + cost;
				if (!checkReduplication.contains(code)) {
					Product prod = productService.findOne(product.getProductId());
					checkReduplication.add(code);
					long productNum = products.stream()
							.filter(p -> p.getName().equals(name) && Double.compare(p.getCost(), cost) == 0)
							.count();
					if (prod == null) {
						contentList.add(new ProductStat(name, productNum, cost, true));
					} else {
						contentList.add(new ProductStat(name, productNum, cost, prod.isDeleted()));
					}
				}
			}
		}
		return contentList;
	}

	@Override
	public DetailStatisticView createDetailStatisticView(Shift shift) {
		LocalDate shiftDate = shift.getShiftDate();
		double cashBox = shift.getCashBox() + shift.getBankCashBox();
		double alteredCashAmount = shift.getAlteredCashAmount();
		double allPrice = getAllPrice(shift);
		int clientsNumber = getClients(shift).size();
		List<UserDTO> usersOnShift = getUserDTOList(shift);
		Set<UserSalaryDetail> salaryDetails = shift.getUserSalaryDetail();
		Set<UserSalaryDetail> paidDetails = new HashSet<>();
		Set<UserSalaryDetail> balanceDetails = new HashSet<>();
		Set<CalculateDTO> allCalculate = new HashSet<>();
		List<Cost> otherCost = costService.findOtherCostByShiftId(shift.getId());
		List<Cost> salaryCost = costService.findSalaryCostAtShift(shift.getId());
		List<Receipt> receiptAmount = receiptService.findByShiftId(shift.getId());
		double allSalaryCost = 0D;
		double allOtherCost = 0D;
		double repaidDebts = 0D;
		double givenDebts = 0D;
		double receiptsSum = 0D;

		for (Calculate calculate : shift.getCalculates()) {
			if (!isCalcDeleted(calculate)) {
				double dirtyPriceMenu = 0D;
				double otherPriceMenu = 0D;

				CalculateDTO calcDto = transformer.transform(calculate, CalculateDTO.class);
				List<Client> clients = calculate.getClient().stream().filter(c -> !c.isDeleteState()).collect(Collectors.toList());
				Set<LayerProduct> products = new HashSet<>();

				for (Client client : clients) {
					products.addAll(client.getLayerProducts());
				}
				for (LayerProduct product : products) {
					if (product.isDirtyProfit()) {
						dirtyPriceMenu += product.getCost();
					} else {
						otherPriceMenu += product.getCost();
					}
				}

				calcDto.setClient(clients);
				calcDto.setAllDirtyPriceMenu(dirtyPriceMenu);
				calcDto.setAllOtherPriceMenu(otherPriceMenu);
				calcDto.setDirtyOrder(getDirtyMenu(calculate));
				calcDto.setOtherOrder(getOtherMenu(calculate));
				allCalculate.add(calcDto);
			}
		}

		for (UserSalaryDetail detail : salaryDetails) {
			if (detail.isPaidDetail()) {
				paidDetails.add(detail);
			} else {
				balanceDetails.add(detail);
			}
		}

		for (Cost cost : otherCost) {
			allOtherCost += cost.getPrice() * cost.getQuantity();
		}

		for (Cost cost : salaryCost) {
			allSalaryCost += cost.getPrice();
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

		return new DetailStatisticView(shiftDate, cashBox, alteredCashAmount, allPrice, clientsNumber,
				usersOnShift, paidDetails, balanceDetails, allCalculate, allSalaryCost, allOtherCost, otherCost,
				repaidDebts, givenDebts, receiptsSum);
	}

	private List<UserDTO> getUserDTOList(Shift shift) {
		List<UserDTO> userDTOList = new ArrayList<>();
		for (User user : shift.getUsers()) {
			UserSalaryDetail shiftUserDetails = userSalaryDetailService.findFirstUnpaidByUserIdAndShiftId(user.getId(), shift.getId());
			UserDTO userDTO = transformer.transform(user, UserDTO.class);
			if (shiftUserDetails != null) {
				userDTO.setTotalSalary(shiftUserDetails.getTotalSalary());
				userDTO.setTotalBonus(shiftUserDetails.getTotalBonus());
				userDTO.setBonusBalance(shiftUserDetails.getBonusBalance());
				userDTO.setSalaryBalance(shiftUserDetails.getSalaryBalance());
				userDTO.setShiftSalary(shiftUserDetails.getShiftSalary());
				userDTO.setShiftAmount(shiftUserDetails.getShiftAmount());
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
		List<Cost> costWithoutUsersSalaries = costService.findOtherCostByShiftId(shift.getId());
		List<Cost> salaryCosts = costService.findSalaryCostAtShift(shift.getId());

		double otherCosts = 0d;

		for (Cost cost : salaryCosts) {
			usersTotalShiftSalary += cost.getPrice();
		}

		for (Cost cost : costWithoutUsersSalaries) {
			otherCosts += (cost.getPrice() * cost.getQuantity());
		}

		for (UserDTO user : usersOnShift) {
			int amountOfPositionsPercent = user.getPositions().stream().filter(PositionDTO::isPositionUsePercentOfSales).mapToInt(PositionDTO::getPercentageOfSales).sum();
			user.setShiftSalary((int) (user.getShiftSalary() + (allPrice * amountOfPositionsPercent) / 100));
		}

		totalCashBox = cashBox + bankCashBox + allPrice - otherCosts - usersTotalShiftSalary;

		return new ShiftView(usersOnShift, clients, activeCalculate, allCalculate,
				cashBox, totalCashBox, usersTotalShiftSalary, card, allPrice, shiftDate, otherCosts, bankCashBox, enabledNotes, staffPercentBonusesMap);
	}

	@Override
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
		for (Calculate calculate : shift.getCalculates()) {
			if (!isCalcDeleted(calculate)) {
				allPrice += calculate.getProfitRecalculation();
				allPrice -= calculate.getLossRecalculation();
			}
		}
		return allPrice;
	}

	@Override
	public void paySalary(List<User> salaryUsers) {
		double totalCost = 0;
		Shift lastShift = shiftService.getLast();
		List<UserSalaryDetail> userSalaryDetails = new ArrayList<>();

		for (User user : salaryUsers) {
			int salary = user.getSalaryBalance() + user.getTotalSalary();
			int bonus = user.getBonusBalance() + user.getTotalBonus();

			totalCost += user.getSalaryBalance() + user.getBonusBalance();

			user.setTotalSalary(salary);
			user.setTotalBonus(bonus);
			UserSalaryDetail paidDetail = getPaidUserSalaryDetail(user, lastShift);
			user.setSalaryBalance(0);
			user.setBonusBalance(0);

			user.addSalaryDetail(paidDetail);
		}

		CostCategory salaryCategory = costCategoryService.getSalaryCategory();
		LocalDate lastDate = shiftService.getLastShiftDate();
		Cost cost = new Cost(salaryCategory.getName(), totalCost, 1.0, salaryCategory, lastDate);

		userSalaryDetailService.save(userSalaryDetails);
		costService.save(cost);
		userService.save(salaryUsers);

	}

	private Double getAllDirtyPrice(Client client) {
		Double dirtyPriceMenu = 0D;

		for (Map.Entry<Long, Double> productSet : client.getProductOnPrice().entrySet()) {
			Product product = productService.findOne(productSet.getKey());
			if (product == null) {
				dirtyPriceMenu += client.getLayerProducts().stream()
						.filter(p -> Objects.equals(p.getProductId(), productSet.getKey()))
						.mapToDouble(p -> p.getCost() / p.getClients().size())
						.reduce((price1, price2) -> price1 + price2).orElse(0D);
			} else if (product.getCategory().isDirtyProfit()) {
				dirtyPriceMenu += productSet.getValue();
			}
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

			if (product != null) {

				Map<Position, Integer> staffPercent = product.getStaffPercent();

				for (UserDTO user : staff) {
					List<PositionDTO> userPositions = user.getPositions();

					for (PositionDTO positionDTO : userPositions) {

						Integer percent = staffPercent.get(transformer.transform(positionDTO, Position.class));
						int shiftPercent = 1;
						if (percent != null) {
							if (shiftPercents.containsKey(positionDTO)) {
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
