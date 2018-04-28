package com.cafe.crm.controllers.calculate;

import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/manager")
public class MenuCalculateController {

	private final ClientService clientService;
	private final CalculateService calculateService;
	private final MenuCalculateControllerService menuCalculateService;
	private final LayerProductService layerProductService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(MenuCalculateController.class);

	@Autowired
	public MenuCalculateController(CalculateService calculateService, ClientService clientService,
								   MenuCalculateControllerService menuCalculateService, LayerProductService layerProductService) {
		this.calculateService = calculateService;
		this.clientService = clientService;
		this.menuCalculateService = menuCalculateService;
		this.layerProductService = layerProductService;
	}

	@RequestMapping(value = {"/create-layer-product"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProduct(@RequestParam("calculateId") long calculateId,
										   @RequestParam(name = "clientsId", required = false) long[] clientsId,
										   @RequestParam("productId") long productId) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		Calculate calculate = calculateService.getOne(calculateId);
		LayerProduct product = menuCalculateService.createLayerProduct(calculateId, clientsId, productId);

		logger.info(getProductAddingMessage(calculate, clients, product).toString());

		return product;
	}

	@RequestMapping(value = {"/create-layer-product-floating-price"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProductWithFloatingPrice(@RequestParam("calculateId") long calculateId,
															@RequestParam(name = "clientsId", required = false) long[] clientsId,
															@RequestParam("productId") long productId,
															@RequestParam("productPrice") double productPrice) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		Calculate calculate = calculateService.getOne(calculateId);
		LayerProduct product = menuCalculateService.createLayerProductWithFloatingPrice(calculateId, clientsId, productId, productPrice);

		StringBuilder message = getProductAddingMessage(calculate, clients, product);
		message.append("\nСтоимость продукта: ").append(productPrice);
		logger.info(message.toString());

		return product;
	}

	@RequestMapping(value = {"/add-client-on-layer-product"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct addClientOnLayerProduct(@RequestParam("calculateId") long calculateId,
												@RequestParam("clientsId") long[] clientsId,
												@RequestParam("productId") long layerProductId) {
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = clientService.findByIdIn(clientsId);
		LayerProduct product = menuCalculateService.addClientOnLayerProduct(calculateId, clientsId, layerProductId);

		logger.info(getProductAddingMessage(calculate, clients, product).toString());

		return menuCalculateService.addClientOnLayerProduct(calculateId, clientsId, layerProductId);
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

	@RequestMapping(value = {"/delete-product-with-client"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> deleteProductOnClient(@RequestParam("calculateId") long calculateId,
											  @RequestParam(name = "clientsId", required = false) long[] clientsId,
											  @RequestParam("productId") long layerProductId) {
		LayerProduct product = layerProductService.getOne(layerProductId);
		Calculate calculate = calculateService.getOne(calculateId);
		List<Client> clients = menuCalculateService.deleteProductOnClient(calculateId, clientsId, layerProductId);

		StringBuilder addProductOnClients = new StringBuilder("Продукт с названием: \"" + product.getName()
				+ "\", описанием: \"" + product.getDescription() + "\" был удалён с заказа клиентов с id и описанием:\n");

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

		logger.info(addProductOnClients.toString());

		return clients;
	}

	@RequestMapping(value = {"/get-products-on-calculate"}, method = RequestMethod.POST)
	@ResponseBody
	public List<LayerProduct> getProductOnCalculate(@RequestParam("calculateId") long calculateId) {
		return menuCalculateService.getProductOnCalculate(calculateId);
	}

	@RequestMapping(value = {"/get-layer-products-on-client"}, method = RequestMethod.POST)
	@ResponseBody
	public List<LayerProduct> getLayerProductsOnClient(@RequestParam("clientId") long clientId) {
		return clientService.getOne(clientId).getLayerProducts();
	}

	@RequestMapping(value = {"/get-open-clients-on-calculate"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> getOpenClientsOnCalculateAjax(@RequestParam("calculateId") long calculateId) {
		return calculateService.getAllOpenOnCalculate(calculateId).getClient();
	}

	@RequestMapping(value = {"/change-calculate-description"})
	public ResponseEntity changeCalculateDescription(@RequestParam("calculateId") long calculateId,
												   @RequestParam("description") String description) {
		if (!StringUtils.isNotBlank(description)) {
			throw new ClientDataException("Описание стола не может быть пустым!");
		}
		Calculate editCalculate = calculateService.getOne(calculateId);

		String existDescription = editCalculate.getDescription();

		editCalculate.setDescription(description);
		calculateService.save(editCalculate);

		logger.info("Описание счёта с id: " + editCalculate.getId() + " было изменено:\n\"" +
				existDescription + "\" -> \"" + description + "\"");

		return ResponseEntity.ok("Описание стола изменено");
	}

	@ExceptionHandler(value = ClientDataException.class)
	public ResponseEntity<?> handleTransferException(ClientDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
