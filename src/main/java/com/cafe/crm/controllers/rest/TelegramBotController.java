package com.cafe.crm.controllers.rest;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.menu.CategoriesService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.JsonField;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.cafe.crm.utils.PatternMatcherHandler.matchEmail;
import static com.cafe.crm.utils.PatternMatcherHandler.matchPhone;

@RestController
public class TelegramBotController {

	private final BoardService boardService;
	private final UserService userService;
	private final ClientService clientService;
	private final ShiftService shiftService;
	private final CategoriesService categoriesService;
	private final MenuCalculateControllerService menuCalculateService;
	private final CalculateService calculateService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

	@Autowired
	public TelegramBotController(BoardService boardService, UserService userService, ClientService clientService,
								 ShiftService shiftService, CategoriesService categoriesService,
								 MenuCalculateControllerService menuCalculateService, CalculateService calculateService) {
		this.boardService = boardService;
		this.userService = userService;
		this.clientService = clientService;
		this.shiftService = shiftService;
		this.categoriesService = categoriesService;
		this.menuCalculateService = menuCalculateService;
		this.calculateService = calculateService;
	}

	@RequestMapping(value = "/manager/rest/Table", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(JsonField.Board.class)
	public List<com.cafe.crm.models.board.Board> getListOpenTables(@RequestParam(value = "username") String username) {
		boolean isAdmin = false;
		List<User> shiftUsers = shiftService.getLast().getUsers();
		User user = userService.findByUsername(username);

		if (shiftUsers.contains(user)) {
			return boardService.getAllOpen();
		}

		Set<Role> roles = user.getRoles();
		for (Role role : roles) {
			if (role.getName().equals("BOSS")) {
				isAdmin = true;
			}
		}
		if (isAdmin) {
			return boardService.getAllOpen();
		}

		return null;
	}

	@RequestMapping(value = "/manager/rest/clientsNumber", method = RequestMethod.GET)
	@ResponseBody
	public Integer getIdLastClient() {
		return Integer.parseInt(clientService.getLast().getId().toString());
	}

	@RequestMapping(value = "/authenticationTelegramBotUsers", method = RequestMethod.POST)
	@ResponseBody
	public User checkAuthCredentials(@RequestParam(value = "username") String username,
	                                        @RequestParam(value = "password") String password) {
		User userInDB;
		if (matchEmail(username) || matchPhone(username)) {
			userInDB = userService.findByUsername(username);
		} else {
			return new User();
		}
		if (userService.isValidPassword(userInDB.getEmail(), password)) {
			return userInDB;
		} else {
			return new User();
		}
	}

	@RequestMapping(value = "/manager/rest/calculateList", method = RequestMethod.GET)
	@ResponseBody
	public Set<Calculate> getCalculateList() {
		return shiftService.getLast().getCalculates();
	}

	@RequestMapping(value = "/manager/rest/categoryWithProducts", method = RequestMethod.GET)
	@ResponseBody
	public List<Category> getCategoriesList () {
		return categoriesService.findAll();
	}

	@RequestMapping(value = {"/manager/rest/create-layer-product"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProduct(@RequestParam("calculateId") long calculateId,
										   @RequestParam("clientsId") long[] clientsId,
										   @RequestParam("productId") long productId) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		LayerProduct product = menuCalculateService.createLayerProduct(calculateId, clientsId, productId);

		logger.info(getProductAddingMessage(calculate, clients, product).toString());

		return product;
	}

	@RequestMapping(value = {"/manager/rest/create-layer-product-with-floating-price"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProduct(@RequestParam("calculateId") long calculateId,
										   @RequestParam("clientsId") long[] clientsId,
										   @RequestParam("productId") long productId,
										   @RequestParam("productPrice") double productPrice) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		LayerProduct product = menuCalculateService.createLayerProductWithFloatingPrice(calculateId, clientsId, productId, productPrice);

		StringBuilder message = getProductAddingMessage(calculate, clients, product);
		message.append("\nСтоимость продукта: ").append(productPrice);
		logger.info(message.toString());

		return product;
	}

	private StringBuilder getProductAddingMessage(Calculate calculate, List<Client> clients, LayerProduct product) {
		StringBuilder addProductOnClients = new StringBuilder("Продукт с названием: \"" + product.getName()
				+ "\", описанием: \"" + product.getDescription() + "\" был добавлен в заказ клиентам с id и описанием:\n");

		for (Client client : clients) {
			addProductOnClients.append(client.getId())
					.append(" - \"")
					.append(client.getDescription())
					.append("\"\n");
		}

		addProductOnClients.append("Счёт с описанием: \"")
				.append(calculate.getDescription())
				.append("\" и id: ")
				.append(calculate.getId());

		return addProductOnClients;
	}
}
