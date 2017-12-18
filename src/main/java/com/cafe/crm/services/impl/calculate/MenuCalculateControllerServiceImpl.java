package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MenuCalculateControllerServiceImpl implements MenuCalculateControllerService {

	private final LayerProductService layerProductService;
	private final ClientService clientService;
	private final ProductService productService;
	private final CalculateService calculateService;

	@Autowired
	public MenuCalculateControllerServiceImpl(ProductService productService, ClientService clientService, LayerProductService layerProductService, CalculateService calculateService) {
		this.productService = productService;
		this.clientService = clientService;
		this.layerProductService = layerProductService;
		this.calculateService = calculateService;
	}

	@Override
	public LayerProduct createLayerProduct(long calculateId, long[] clientsId, long productId) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		Product product = productService.findOne(productId);
		int oldRating = product.getRating();
		product.setRating(++oldRating);
		LayerProduct layerProduct = new LayerProduct();
		layerProduct.setProductId(productId);
		layerProduct.setCost(product.getCost());
		layerProduct.setName(product.getName());
		layerProduct.setDescription(product.getDescription());
		layerProduct.setClients(clients);
		if (!product.getCategory().isDirtyProfit()) {
			layerProduct.setDirtyProfit(false);
		}
		productService.reduceIngredientAmount(product);
		layerProductService.save(layerProduct);
		calculatePriceMenu(calculateId);
		return layerProduct;
	}

	@Override
	public LayerProduct createLayerProductWithFloatingPrice(long calculateId, long[] clientsId, long productId, double productPrice) {
		List<Client> clients = clientService.findByIdIn(clientsId);
		Product product = productService.findOne(productId);
		int oldRating = product.getRating();
		product.setRating(++oldRating);
		LayerProduct layerProduct = new LayerProduct();
		layerProduct.setProductId(productId);
		layerProduct.setCost(productPrice);
		layerProduct.setName(product.getName());
		layerProduct.setDescription(product.getDescription());
		layerProduct.setClients(clients);
		if (product.getCategory().isFloatingPrice()) {
			layerProduct.setFloatingPrice(true);
		}
		if (!product.getCategory().isDirtyProfit()) {
			layerProduct.setDirtyProfit(false);
		}
		layerProductService.save(layerProduct);
		calculatePriceMenu(calculateId);
		return layerProduct;
	}

	@Override
	public LayerProduct addClientOnLayerProduct(long calculateId, long[] clientsId, long layerProductId) {
		LayerProduct layerProduct = layerProductService.getOne(layerProductId);
		List<Client> clients = layerProduct.getClients();
		for (Client client : clients) {
			if (!client.isState()) {
				return layerProduct;
			}
		}
		clients.addAll(clientService.findByIdIn(clientsId));
		layerProduct.setClients(new ArrayList<Client>(new LinkedHashSet<Client>(clients)));
		// set на случай если продукт уже есть на клиенте, чтобы избежать дублирования
		layerProductService.save(layerProduct);
		calculatePriceMenu(calculateId);
		return layerProduct;
	}

	@Override
	public LayerProduct deleteProductOnClient(long calculateId, long[] clientsId, long layerProductId) {
		LayerProduct layerProduct = layerProductService.getOne(layerProductId);
		List<Client> clients = layerProduct.getClients();
		List<Client> forDelClients = clientService.findByIdIn(clientsId);
		clients.removeAll(forDelClients);
		if (layerProduct.getClients().isEmpty()) {
			layerProductService.delete(layerProduct);
		} else {
			layerProductService.save(layerProduct);
		}
		calculatePriceMenu(calculateId);
		layerProduct.setClients(forDelClients);//for json
		String name = layerProduct.getName();
		String description = layerProduct.getDescription();
		Double cost = !layerProduct.isFloatingPrice() ? layerProduct.getCost() : 0d;
		Product product = productService.findByNameAndDescriptionAndCost(name, description, cost);
		int oldRating = product.getRating();
		product.setRating(--oldRating);
		productService.saveAndFlush(product);
		return layerProduct;
	}

	@Override
	public Set<LayerProduct> getProductOnCalculate(long calculateId) {
		Calculate calculate = calculateService.getAllOpenOnCalculate(calculateId);
		List<Client> listClient = calculate.getClient();
		Set<LayerProduct> products = new LinkedHashSet<>();
		for (Client client : listClient) {
			products.addAll(client.getLayerProducts());
		}
		return products;
	}

	public void calculatePriceMenu(long calculateId) {
		Calculate calculate = calculateService.getAllOpenOnCalculate(calculateId);
		List<Client> clients = calculate.getClient();
		for (Client client : clients) {
			client.setPriceMenu(0D);
			for (LayerProduct layerProduct : client.getLayerProducts()) {
				if (layerProduct.getClients().size() == 0) {
					continue;
				}
				client.setPriceMenu(Math.round((client.getPriceMenu() + layerProduct.getCost() / layerProduct.getClients().size()) * 100) / 100.00);
			}
		}
		clientService.saveAll(clients);
	}
}
