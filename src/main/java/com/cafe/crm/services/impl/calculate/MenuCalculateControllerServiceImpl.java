package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.exceptions.client.ClientDataException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuCalculateControllerServiceImpl implements MenuCalculateControllerService {

	private final LayerProductService layerProductService;
	private final ClientService clientService;
	private final ProductService productService;
	private final CalculateService calculateService;
	private final IngredientsService ingredientsService;

	@Autowired
	public MenuCalculateControllerServiceImpl(ProductService productService, ClientService clientService,
											  LayerProductService layerProductService, CalculateService calculateService,
											  IngredientsService ingredientsService) {
		this.productService = productService;
		this.clientService = clientService;
		this.layerProductService = layerProductService;
		this.calculateService = calculateService;
		this.ingredientsService = ingredientsService;
	}

	@Override
	public LayerProduct createLayerProduct(long calculateId, long[] clientsId, long productId) {
		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для добавления!");
		}

		List<Client> clients = clientService.findByIdIn(clientsId);
		Product product = productService.findOne(productId);

		if (product.isDeleted()) {
			throw new ClientDataException("Нельзя добавить удалённый продукт!");
		}

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
		if (product.getCategory().isAccountability()) {
			layerProduct.setAccountability(true);
		}

		productService.reduceIngredientAmount(product);
		layerProductService.save(layerProduct);
		calculatePriceMenu(calculateId);
		return layerProduct;
	}

	@Override
	public LayerProduct createLayerProductWithFloatingPrice(long calculateId, long[] clientsId, long productId, double productPrice) {
		if (clientsId == null) {
			throw new ClientDataException("Выберите клиентов для добавления!");
		}

		if (productPrice >= 1_000_000) {
			throw new ClientDataException("Вы ввели слишком большую сумму!");
		}

		List<Client> clients = clientService.findByIdIn(clientsId);
		Product product = productService.findOne(productId);

		if (product.isDeleted()) {
			throw new ClientDataException("Нельзя добавить удалённый продукт!");
		}

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
		if (product.getCategory().isAccountability()) {
			layerProduct.setAccountability(true);
		}

		layerProductService.save(layerProduct);
		calculatePriceMenu(calculateId);
		return layerProduct;
	}

	@Override
	public LayerProduct addClientOnLayerProduct(long calculateId, long[] clientsId, long layerProductId) {
		LayerProduct layerProduct = layerProductService.getOne(layerProductId);
		List<Client> calculatedClients = clientService.findByIdIn(clientsId);
		List<Client> clients = layerProduct.getClients();

		for (Client client : clients) {
			if (!client.isState()) {
				return layerProduct;
			}
		}

		clients.addAll(calculatedClients);
		layerProduct.setClients(new ArrayList<Client>(new LinkedHashSet<Client>(clients))); //говнокод
		// set на случай если продукт уже есть на клиенте, чтобы избежать дублирования
		layerProductService.save(layerProduct);
		clientService.saveAll(clients);
		calculatePriceMenu(calculateId);
		return layerProduct;

	}

	@Override
	public List<Client> deleteProductOnClient(long calculateId, long[] clientsId, long layerProductId) {
		LayerProduct layerProduct = layerProductService.getOne(layerProductId);
		Product product = productService.findOne(layerProduct.getProductId());
		List<Client> clients = layerProduct.getClients();

		if (clientsId == null) {
			layerProduct.setClients(new ArrayList<>());
			layerProductService.delete(layerProduct);
			ingredientsService.retrieveIngredientAmount(product.getRecipe());
			calculatePriceMenu(calculateId);
			return clients;
		}

		List<Client> forDelClients = clientService.findByIdIn(clientsId);
		forDelClients.retainAll(clients);			//защита от лишних клиентов в добавленном списке
		clients.removeAll(forDelClients);
		layerProduct.setClients(clients);

		if (clients.isEmpty()) {
			layerProductService.delete(layerProduct);
			ingredientsService.retrieveIngredientAmount(product.getRecipe());
		} else {
			layerProductService.save(layerProduct);
		}
		calculatePriceMenu(calculateId);

		if (!product.isDeleted()) {
			int oldRating = product.getRating();
			product.setRating(--oldRating);
			productService.saveAndFlush(product);
		}

		return forDelClients;
	}

	@Override
	public List<LayerProduct> getProductOnCalculate(long calculateId) {
		Calculate calculate = calculateService.getAllOpenOnCalculate(calculateId);
		List<Client> listClient = calculate.getClient();
		List<LayerProduct> allProducts = new ArrayList<>();
		for (Client client : listClient) {
			allProducts.addAll(client.getLayerProducts());
		}
		List<LayerProduct> sortedProducts = allProducts.stream()
				.distinct()
				.sorted(Comparator.comparing(LayerProduct::getId))
				.collect(Collectors.toList());
		return sortedProducts;
	}

	public void calculatePriceMenu(long calculateId) {
		Calculate calculate = calculateService.getAllOpenOnCalculate(calculateId);
		List<Client> clients = calculate.getClient();
		for (Client client : clients) {
			Map<Long, Double> prodOnPrice = new HashMap<>();
			client.setPriceMenu(0D);
			for (LayerProduct layerProduct : client.getLayerProducts()) {
				if (layerProduct.getClients().size() == 0) {
					continue;
				}
				long prodId = layerProduct.getProductId();
				long clientCount = layerProduct.getClients().stream().filter(c -> c.isState() && !c.isDeleteState()).count();
				double partPrice = layerProduct.getCost() / clientCount;
				double prodPrice = Math.round((client.getPriceMenu() + partPrice) * 100) / 100.00;
				client.setPriceMenu(prodPrice);

				if (prodOnPrice.containsKey(prodId)) {
					double price = prodOnPrice.get(prodId);
					prodOnPrice.put(prodId, Math.round((price + partPrice) * 100) / 100.00);
				} else {
					prodOnPrice.put(prodId, Math.round(partPrice * 100) / 100.00);
				}
			}
			client.setProductOnPrice(prodOnPrice);
		}
		clientService.saveAll(clients);
	}
}
