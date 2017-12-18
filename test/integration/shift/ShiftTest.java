package integration.shift;

import com.cafe.crm.Main;
import com.cafe.crm.controllers.boss.MenuController;
import com.cafe.crm.controllers.calculate.CalculateController;
import com.cafe.crm.controllers.calculate.MenuCalculateController;
import com.cafe.crm.controllers.shift.ShiftController;
import com.cafe.crm.dto.WrapperOfProduct;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.impl.calculate.MenuCalculateControllerServiceImpl;
import com.cafe.crm.services.interfaces.calculate.CalculateControllerService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.CompanyIdCache;
import org.cloudinary.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import com.yc.easytransformer.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Created on 11/8/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class ShiftTest {

	private MenuCalculateController menuCalculateController;
	private ProductService productService;
	private ShiftService shiftService;
	private LayerProductService layerProductService;
	private CalculateControllerService calculateControllerService;
	private PropertyService propertyService;
	private ShiftCalculationService shiftCalculationService;
	private MenuCalculateControllerService menuCalculateControllerService;
	private ClientService clientService;
	private DebtService debtService;

	@MockBean
	private CompanyIdCache companyIdCache;

	@MockBean
	Calculate calculate;

	@MockBean
	BindingResult bindingResult;

	@MockBean
	Authentication authentication;

	@Autowired
	public void setDependency(MenuCalculateController menuCalculateController, ProductService productService,
							  ShiftService shiftService, LayerProductService layerProductService,
							  CalculateControllerService calculateControllerService, PropertyService propertyService,
							  ShiftCalculationService shiftCalculationService,
							  MenuCalculateControllerService menuCalculateControllerService,
							  ClientService clientService, DebtService debtService) {
		this.menuCalculateController = menuCalculateController;
		this.productService = productService;
		this.shiftService = shiftService;
		this.layerProductService = layerProductService;
		this.calculateControllerService = calculateControllerService;
		this.propertyService = propertyService;
		this.shiftCalculationService = shiftCalculationService;
		this.menuCalculateControllerService = menuCalculateControllerService;
		this.clientService = clientService;
		this.debtService = debtService;
	}

	@Before
	public void setupShiftTest() throws Exception {
		DBInitSQLScript.run();
		Product product = productService.findOne(1L);
		Long productId = product.getId();

		// set company id
		when(companyIdCache.getCompanyId()).thenReturn(1L);

		// set employees for the shift
		long[] employeeIds = {1L, 2L, 3L};
		// set clients for the shift
		long[] clientIds = {1L};
		Long numberOfCustomers = 1L;
		Long billId = 1L;

		shiftService.createNewShift(1000D, 1000D, employeeIds);
		calculateControllerService.createCalculate(billId, numberOfCustomers, "");

		//set time of visit to 3 hours
		LocalDateTime startTime = LocalDateTime.now();
		startTime = startTime.minusHours(3);
		Client client = clientService.getOne(clientIds[0]);
		client.setTimeStart(startTime);
		clientService.save(client);

		// set product to be added to the bill
		LayerProduct layerProduct = new LayerProduct();
		layerProduct.setProductId(1L);
		layerProduct.setCost(product.getCost());
		layerProduct.setName(product.getName());
		layerProduct.setDescription(product.getDescription());
		if (!product.getCategory().isDirtyProfit()) {
			layerProduct.setDirtyProfit(false);
		}
		layerProductService.save(layerProduct);
		//run calculation
		menuCalculateControllerService.calculatePriceMenu(billId);
		menuCalculateController.createLayerProduct(billId, clientIds, productId);
		calculateControllerService.calculatePrice();
		calculateControllerService.closeClientDebt("Name", clientIds, billId, 500D);
		calculateControllerService.closeClient(clientIds, billId);

		//create notes and bones maps (not used in test)
		Map<Long, Integer> mapOfUsersIdsAndBonuses = new HashMap<>();
		mapOfUsersIdsAndBonuses.put(1L, 0);
		mapOfUsersIdsAndBonuses.put(2L, 5);
		mapOfUsersIdsAndBonuses.put(3L, 0);
		Map<String, String> mapOfNoteNameAndValue = new HashMap<>();
		mapOfNoteNameAndValue.put("Note1", "NoteVal");

		shiftService.closeShift(mapOfUsersIdsAndBonuses,
				shiftCalculationService.getAllPrice(shiftService.getLast()), 0D, 1000D,
				"shift Comment", mapOfNoteNameAndValue);

		//SECOND SHIFT
		long[] clientIds2 = {2L};

		shiftService.createNewShift(shiftService.getLast().getCashBox(), shiftService.getLast().getBankCashBox(), employeeIds);
		calculateControllerService.createCalculate(2L, numberOfCustomers, "");

		//set time of visit to 3 hours
		LocalDateTime startTime2 = LocalDateTime.now();
		startTime2 = startTime2.minusHours(3);
		Client client2 = clientService.getOne(clientIds2[0]);
		client2.setTimeStart(startTime2);
		clientService.save(client2);
		// set product to be added to the bill
		LayerProduct layerProduct2 = new LayerProduct();
		layerProduct2.setProductId(2L);
		layerProduct2.setCost(product.getCost());
		layerProduct2.setName(product.getName());
		layerProduct2.setDescription(product.getDescription());
		if (!product.getCategory().isDirtyProfit()) {
			layerProduct2.setDirtyProfit(false);
		}
		layerProductService.save(layerProduct2);

		//run calculations
		menuCalculateControllerService.calculatePriceMenu(2L);
		menuCalculateController.createLayerProduct(2L, clientIds2, productId);
		calculateControllerService.calculatePrice();
		calculateControllerService.closeClientDebt("Name2", clientIds2, 2L, 500D);
		calculateControllerService.closeClient(clientIds2, 2L);

		shiftService.closeShift(mapOfUsersIdsAndBonuses,
				shiftCalculationService.getAllPrice(shiftService.getLast()), 0D, 1000D,
				"shift Comment", mapOfNoteNameAndValue);
	}

	@Test
	public void testShift() {
		//three employees on shift, initial cash 1000, bankCard 1000, product cost 400, first hour price 300, after 200
		//1 client, 3 hours,
		//bill payment: 500 paid, the rest on credit
		//the same scenario for two shifts
		//shift profit is checked only
		Product product = productService.findOne(1L);
		Integer firstHourPrice = Integer.parseInt(propertyService.findByName("цена за первый час").getValue());
		Integer afterFirstHourPrice = Integer.parseInt(propertyService.findByName("цена за последующие часы").getValue());
		Double productPrice = product.getCost();
		Double shiftProfit = shiftService.findOne(1L).getProfit();
		Double debt = debtService.get(1L).getDebtAmount();

		assertEquals(firstHourPrice + afterFirstHourPrice*2 + productPrice - debt, shiftProfit, 0);
		Double shiftProfit2 = shiftService.findOne(2L).getProfit();
		Double debt2 = debtService.get(2L).getDebtAmount();
		assertEquals(firstHourPrice + afterFirstHourPrice*2 + productPrice - debt2, shiftProfit2, 0);
	}


}
