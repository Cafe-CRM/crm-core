package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.calculate.MenuCalculateControllerService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.layerproduct.LayerProductService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuCalculateControllerServiceImplTest {

    @MockBean
    private LayerProductService layerProductService;

    @Autowired
    private MenuCalculateControllerService menuCalculateControllerService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CalculateService calculateService;

    @MockBean
    private Calculate calculate;

    @Test
    public void testCreateLayerProduct_IfProductCategoryIsDirtyProfit() throws Exception {
        long calculateId = 1;
        long[] ids = {1, 2};
        Client client1 = initClient(1);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientService.findByIdIn(ids)).thenReturn(clients);
        Product product = initProduct(1, 100.1, 95.2, 30, "",
                                        "product","description", true);
        when(productService.findOne(1L)).thenReturn(product);
        product.setRating(31);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        //use the real Object from MenuCalculateControllerService to test a layerProduct creation
        LayerProduct layerProduct = menuCalculateControllerService.createLayerProduct(calculateId, ids, 1);

        assertEquals(new Double(100.1), layerProduct.getCost());
        assertEquals("product", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(clients, layerProduct.getClients());

        if (!product.getCategory().isDirtyProfit()) {
            layerProduct.setDirtyProfit(false);
            assertEquals(false, layerProduct.isDirtyProfit());
        } else {
            assertEquals(true, layerProduct.isDirtyProfit());
        }

        verify(clientService, times(1)).findByIdIn(ids);
        verify(productService, times(1)).findOne(1L);
    }


    @Test
    public void testCreateLayerProduct_IfProductCategoryIsNotDirtyProfit() throws Exception {
        long calculateId = 1;
        long[] ids = {1, 2};
        Client client1 = initClient(1);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientService.findByIdIn(ids)).thenReturn(clients);

        Product product = initProduct(1, 100.1, 95.2, 30, "",
                "product","description", false);
        when(productService.findOne(1L)).thenReturn(product);
        product.setRating(31);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        //use the real Object from MenuCalculateControllerService to test a layerProduct creation
        LayerProduct layerProduct = menuCalculateControllerService.createLayerProduct(calculateId, ids, 1);

        assertEquals(new Double(100.1), layerProduct.getCost());
        assertEquals("product", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(clients, layerProduct.getClients());

        if (!product.getCategory().isDirtyProfit()) {
            layerProduct.setDirtyProfit(false);
            assertEquals(false, layerProduct.isDirtyProfit());
        } else {
            assertEquals(true, product.getCategory().isDirtyProfit());
        }

        verify(clientService, times(1)).findByIdIn(ids);
        verify(productService, times(1)).findOne(1L);
    }


    @Test
    public void testCreateLayerProduct_IfMethodGetZeroLongsAndDescriptions() throws Exception {
        long calculateId = 0;
        long[] ids = {0, 0};
        Client client1 = initClient(0);
        Client client2 = initClient(0);
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientService.findByIdIn(ids)).thenReturn(clients);

        Product product = initProduct(0, 0, 0, 0, "",
                "","", false);
        when(productService.findOne(0L)).thenReturn(product);
        product.setRating(31);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        //use the real Object from MenuCalculateControllerService to test a layerProduct creation
        LayerProduct layerProduct = menuCalculateControllerService.createLayerProduct(calculateId, ids, 0);

        assertEquals(new Double(0), layerProduct.getCost());
        assertEquals("", layerProduct.getName());
        assertEquals("", layerProduct.getDescription());
        assertEquals(clients, layerProduct.getClients());

        if (!product.getCategory().isDirtyProfit()) {
            layerProduct.setDirtyProfit(false);
            assertEquals(false, layerProduct.isDirtyProfit());
        } else {
            assertEquals(true, layerProduct.isDirtyProfit());
        }

        verify(clientService, times(1)).findByIdIn(ids);
        verify(productService, times(1)).findOne(0L);
    }

    @Test
    public void testCreateLayerProductWithFloatingPrice_IfProductIsFloatingPrice() throws Exception {
        long calculateId = 0;
        long[] ids = {1, 2};
        Client client1 = initClient(1);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientService.findByIdIn(ids)).thenReturn(clients);
        Product product = initProduct(1, 100.1, 95.2, 30, "",
                "product","description", true);
        when(productService.findOne(1L)).thenReturn(product);
        product.setRating(31);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        //use the real Object from MenuCalculateControllerService to test a layerProduct creation
        LayerProduct layerProduct = menuCalculateControllerService
                                    .createLayerProductWithFloatingPrice(calculateId, ids, 1, 100.1);

        assertEquals(new Double(100.1), layerProduct.getCost());
        assertEquals("product", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(clients, layerProduct.getClients());

        if (product.getCategory().isFloatingPrice()) {
            layerProduct.setFloatingPrice(true);
            assertEquals(true, layerProduct.isFloatingPrice());
        } else {
            assertEquals(false, layerProduct.isFloatingPrice());
        }

        if (!product.getCategory().isDirtyProfit()) {
            layerProduct.setDirtyProfit(false);
            assertEquals(false, layerProduct.isDirtyProfit());
        } else {
            assertEquals(true, layerProduct.isDirtyProfit());
        }

        verify(clientService, times(1)).findByIdIn(ids);
        verify(productService, times(1)).findOne(1L);

    }


    @Test
    public void testCreateLayerProductWithFloatingPrice_IfProductIsNotFloatingPrice() throws Exception {
        long calculateId = 0;
        long[] ids = {1, 2};
        Client client1 = initClient(1);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientService.findByIdIn(ids)).thenReturn(clients);
        Product product = initProduct(1, 20, 95.2, 30, "",
                "product","description", true);
        when(productService.findOne(1L)).thenReturn(product);
        product.setRating(31);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        //use the real Object from MenuCalculateControllerService to test a layerProduct creation
        LayerProduct layerProduct = menuCalculateControllerService.createLayerProductWithFloatingPrice(calculateId, ids, 1, 20);

        assertEquals(new Double(20), layerProduct.getCost());
        assertEquals("product", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(clients, layerProduct.getClients());

        if (product.getCategory().isFloatingPrice()) {
            layerProduct.setFloatingPrice(true);
            assertEquals(true, layerProduct.isFloatingPrice());
        } else {
            assertEquals(false, layerProduct.isFloatingPrice());
        }

        if (!product.getCategory().isDirtyProfit()) {
            layerProduct.setDirtyProfit(false);
            assertEquals(false, layerProduct.isDirtyProfit());
        } else {
            assertEquals(true, layerProduct.isDirtyProfit());
        }

        verify(clientService, times(1)).findByIdIn(ids);
        verify(productService, times(1)).findOne(1L);

    }



    @Test
    public void testAddClientOnLayerProduct_IfClientIsNotState() throws Exception {
        long calculateId = 1;
        long[] ids = {1, 2};
        long[] ids2 = {3, 4};

        LayerProduct layerProduct = new LayerProduct();
        layerProduct.setId(1L);
        Client client1 = initClient(1);
        client1.setState(false);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);

        Client client3 = initClient(3);
        client3.setState(true);
        Client client4 = initClient(4);
        List<Client> clients2 = Arrays.asList(client3, client4);
        layerProduct.setClients(clients);

        when(clientService.findByIdIn(ids2)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenReturn(layerProduct);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        layerProduct = menuCalculateControllerService.addClientOnLayerProduct(1L,ids,1L);
        assertEquals(clients, layerProduct.getClients());

        for (Client client : clients) {
            if (!client.isState()) {
                assertEquals(false, layerProduct.getClients().get(0).isState());
            }
        }

        layerProduct.setClients(new ArrayList<Client>(new LinkedHashSet<Client>(clients)));
        assertEquals(clients, layerProduct.getClients());

        verify(clientService, times(0)).findByIdIn(ids);
        verify(layerProductService, times(1)).getOne(1L);

    }

    @Test
    public void testAddClientOnLayerProduct_IfClientIsState() throws Exception {
        long calculateId = 1;
        long[] ids = {1, 2};
        long[] ids2 = {3, 4};

        LayerProduct layerProduct = new LayerProduct();
        layerProduct.setId(1L);
        Client client1 = initClient(1);
        client1.setState(true);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);

        Client client3 = initClient(3);
        client3.setState(true);
        Client client4 = initClient(4);
        List<Client> clients2 = Arrays.asList(client3, client4);
        layerProduct.setClients(clients);

        when(clientService.findByIdIn(ids2)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenReturn(layerProduct);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);

        layerProduct = menuCalculateControllerService.addClientOnLayerProduct(1L,ids,1L);
        assertEquals(clients, layerProduct.getClients());

        for (Client client : clients) {
            if (!client.isState()) {
                assertEquals(false, layerProduct.getClients().get(0).isState());
            }
        }

        layerProduct.setClients(new ArrayList<Client>(new LinkedHashSet<Client>(clients)));
        assertEquals(clients, layerProduct.getClients());

        verify(clientService, times(1)).findByIdIn(ids);
        verify(layerProductService, times(1)).getOne(1L);

    }


    @Test(expected = NullPointerException.class)
    public void testAddClientOnLayerProduct_IfCalculateIdIsZero() throws Exception {
        long calculateId = 0;
        long[] ids = {1, 2};
        long[] ids2 = {3, 4};

        LayerProduct layerProduct = new LayerProduct();
        layerProduct.setId(1L);
        Client client1 = initClient(1);
        client1.setState(true);
        Client client2 = initClient(2);
        List<Client> clients = Arrays.asList(client1, client2);

        Client client3 = initClient(3);
        client3.setState(true);
        Client client4 = initClient(4);
        List<Client> clients2 = Arrays.asList(client3, client4);
        layerProduct.setClients(clients);

        when(clientService.findByIdIn(ids2)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenReturn(layerProduct);
        when(calculateService.getAllOpenOnCalculate(calculateId).getClient()).thenThrow(new NullPointerException());

        layerProduct = menuCalculateControllerService.addClientOnLayerProduct(1L,ids,1L);
        assertEquals(clients, layerProduct.getClients());

        for (Client client : clients) {
            if (!client.isState()) {
                assertEquals(false, layerProduct.getClients().get(0).isState());
            }
        }

        layerProduct.setClients(new ArrayList<>(new LinkedHashSet<>(clients)));
        assertEquals(clients, layerProduct.getClients());

        verify(clientService, times(1)).findByIdIn(ids);
        verify(layerProductService, times(1)).getOne(1L);

    }


    @Test
    public void testDeleteProductOnClient_LayerProductGetClientsIsNotEmpty() throws Exception {

        long calculateId = 1;
        long[] ids = {1, 2};

        LayerProduct layerProduct = initLayerProduct(1L, 100d, "layerProduct", "description");

        List<Client> clients = initClientsList(5);
        List<Client> clients2 = initClientsList(2);
        layerProduct.setClients(clients);

        Product product = initProduct(1, 200, 150, 50, "", "product", "", false);
        String name = layerProduct.getName();
        String description = layerProduct.getDescription();
        Double cost = !layerProduct.isFloatingPrice() ? layerProduct.getCost() : 0d;

        when(clientService.findByIdIn(ids)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenReturn(layerProduct);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);
        when(productService.findByNameAndDescriptionAndCost(name, description, cost)).thenReturn(product);
        layerProduct = menuCalculateControllerService.deleteProductOnClient(1L,ids,1L);

        assertEquals(clients2, layerProduct.getClients());
        assertEquals("layerProduct", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(new Double(100d), layerProduct.getCost());

        if (layerProduct.getClients().isEmpty()) {
            layerProductService.delete(layerProduct);
            verify(layerProductService, times(0)).delete(layerProduct);
        } else {
            verify(layerProductService, times(1)).save(layerProduct);
        }
    }


    @Test
    public void testDeleteProductOnClient_LayerProductGetClientsIsEmpty() throws Exception {

        long calculateId = 1;
        long[] ids = {1, 2};

        LayerProduct layerProduct = initLayerProduct(1L, 100d, "layerProduct", "description");

        List<Client> clients = initClientsList(5);
        List<Client> clients2 = initClientsList(5);
        layerProduct.setClients(clients);

        Product product = initProduct(1, 200, 150, 50, "", "product", "", false);
        String name = layerProduct.getName();
        String description = layerProduct.getDescription();
        Double cost = !layerProduct.isFloatingPrice() ? layerProduct.getCost() : 0d;

        when(clientService.findByIdIn(ids)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenReturn(layerProduct);
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);
        when(productService.findByNameAndDescriptionAndCost(name, description, cost)).thenReturn(product);
        layerProduct = menuCalculateControllerService.deleteProductOnClient(1L,ids,1L);

        assertEquals(clients2, layerProduct.getClients());
        assertEquals("layerProduct", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(new Double(100d), layerProduct.getCost());

        if (layerProduct.getClients().isEmpty()) {
            assertEquals(true, layerProduct.getClients().isEmpty());
            verify(layerProductService, times(1)).delete(layerProduct);
        } else {
            verify(layerProductService, times(0)).save(layerProduct);
        }
    }


    @Test(expected = NullPointerException.class)
    public void testDeleteProductOnClient_NoLayerProductUsingLayerProductId() throws Exception {

        long calculateId = 1;
        long[] ids = {1, 2};

        LayerProduct layerProduct = initLayerProduct(1L, 100d, "layerProduct", "description");
        List<Client> clients = initClientsList(5);
        List<Client> clients2 = initClientsList(5);
        layerProduct.setClients(clients);

        Product product = initProduct(1, 200, 150, 50, "", "product", "", false);
        String name = layerProduct.getName();
        String description = layerProduct.getDescription();
        Double cost = !layerProduct.isFloatingPrice() ? layerProduct.getCost() : 0d;

        when(clientService.findByIdIn(ids)).thenReturn(clients2);
        when(layerProductService.getOne(1L)).thenThrow(new NullPointerException());
        when(calculateService.getAllOpenOnCalculate(calculateId)).thenReturn(calculate);
        when(productService.findByNameAndDescriptionAndCost(name, description, cost)).thenReturn(product);
        layerProduct = menuCalculateControllerService.deleteProductOnClient(1L,ids,1L);

        assertEquals(clients2, layerProduct.getClients());
        assertEquals("layerProduct", layerProduct.getName());
        assertEquals("description", layerProduct.getDescription());
        assertEquals(new Double(100d), layerProduct.getCost());

        if (layerProduct.getClients().isEmpty()) {
            assertEquals(true, layerProduct.getClients().isEmpty());
            verify(layerProductService, times(1)).delete(layerProduct);
        } else {
            verify(layerProductService, times(0)).save(layerProduct);
        }
    }



    //init Client for test methods
    private Client initClient(long x){
        String description = "description" + x;
        Client client = new Client();
        client.setId(x);
        client.setDescription(description);
        return client;
    }

    //init Product for test methods
    private Product initProduct(long prodId, double cost, double selfCost,
                                int rating, String categoryName, String name, String description, boolean dirtyProfit){
        Product product = new Product();
        Category category = new Category();
        category.setId(prodId);
        category.setName(categoryName);
        category.setDirtyProfit(dirtyProfit);
        product.setId(prodId);
        product.setName(name);
        product.setDescription(description);
        product.setCost(cost);
        product.setSelfCost(selfCost);
        product.setCategory(category);
        product.setRating(rating);
        return product;
    }

    //init LayerProduct for delete test methods
    private LayerProduct initLayerProduct(long id, double cost, String name, String description) {
        LayerProduct layerProduct = new LayerProduct();
        layerProduct.setId(id);
        layerProduct.setCost(cost);
        layerProduct.setName(name);
        layerProduct.setDescription(description);
        return  layerProduct;
    }

    //init List<Client> for delete test method
    private List<Client> initClientsList(int clientsPcs){
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < clientsPcs; i++) {
            Client client = initClient(i);
            clients.add(client);
        }
        return clients;
    }
}