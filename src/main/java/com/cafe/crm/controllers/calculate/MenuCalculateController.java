package com.cafe.crm.controllers.calculate;

import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import org.apache.commons.lang3.StringUtils;
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

	@Autowired
	public MenuCalculateController(CalculateService calculateService, ClientService clientService, MenuCalculateControllerService menuCalculateService) {
		this.calculateService = calculateService;
		this.clientService = clientService;
		this.menuCalculateService = menuCalculateService;
	}

	@RequestMapping(value = {"/create-layer-product"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProduct(@RequestParam("calculateId") long calculateId,
										   @RequestParam("clientsId") long[] clientsId,
										   @RequestParam("productId") long productId) {
		return menuCalculateService.createLayerProduct(calculateId, clientsId, productId);
	}

	@RequestMapping(value = {"/create-layer-product-floating-price"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct createLayerProductWithFloatingPrice(@RequestParam("calculateId") long calculateId,
															@RequestParam("clientsId") long[] clientsId,
															@RequestParam("productId") long productId,
															@RequestParam("productPrice") double productPrice) {
		return menuCalculateService.createLayerProductWithFloatingPrice(calculateId, clientsId, productId, productPrice);
	}

	@RequestMapping(value = {"/add-client-on-layer-product"}, method = RequestMethod.POST)
	@ResponseBody
	public LayerProduct addClientOnLayerProduct(@RequestParam("calculateId") long calculateId,
												@RequestParam("clientsId") long[] clientsId,
												@RequestParam("productId") long layerProductId) {
		return menuCalculateService.addClientOnLayerProduct(calculateId, clientsId, layerProductId);
	}

	@RequestMapping(value = {"/delete-product-with-client"}, method = RequestMethod.POST)
	@ResponseBody
	public List<Client> deleteProductOnClient(@RequestParam("calculateId") long calculateId,
											  @RequestParam(name = "clientsId", required = false) long[] clientsId,
											  @RequestParam("productId") long layerProductId) {
		return menuCalculateService.deleteProductOnClient(calculateId, clientsId, layerProductId);
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
		editCalculate.setDescription(description);
		calculateService.save(editCalculate);
		return ResponseEntity.ok("Описание стола изменено");
	}

	@ExceptionHandler(value = ClientDataException.class)
	public ResponseEntity<?> handleTransferException(ClientDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
