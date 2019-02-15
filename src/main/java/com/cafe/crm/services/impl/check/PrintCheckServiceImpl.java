package com.cafe.crm.services.impl.check;

import com.cafe.crm.exceptions.check.CheckException;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.check.PrintCheckService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PrintCheckServiceImpl implements PrintCheckService {

    private final RestTemplate restTemplate;
    private final ClientService clientService;
    private final CalculateService calculateService;
    private final ProductService productService;

    @Value("${property.name.check.url}")
    private String url;

    @Autowired
    public PrintCheckServiceImpl(RestTemplateBuilder restTemplateBuilder, ClientService clientService,
                                 CalculateService calculateService, ProductService productService) {
        restTemplate = restTemplateBuilder.build();
        this.clientService = clientService;
        this.calculateService = calculateService;
        this.productService = productService;
    }

    @Override
    public void printCheck(long[] clientsId) {
        List<Client> clients = clientService.findByIdIn(clientsId);
        MultiValueMap<String, String> check = createCheck(clients, Collections.emptyList());
        sendCheck(check);
    }

    @Override
    public void printCheckWithCostPrice(long[] clientsId, long[] costPriceClientsId) {
        List<Client> clients = clientService.findByIdIn(clientsId);
        List<Client> costPriceClients = clientService.findByIdIn(costPriceClientsId);
        MultiValueMap<String, String> check = createCheck(clients, costPriceClients);
        sendCheck(check);
    }

    @Override
    public void repeatPrintCheck(Long calculateId) {
//        Calculate calculate = calculateService.getOne(calculateId);
//        List<Client> clients = calculate.getClient();
//        List<Client> closedClients = new LinkedList<>();
//
//        for (Client client : clients) {
//            if (!client.isState()) {
//                closedClients.add(client);
//            }
//        }
//        if (closedClients.isEmpty()) {
//            throw new CheckException("Нет рассчитаных клиентов на этом счете!");
//        }
//
//        MultiValueMap<String, String> check = createCheck(closedClients);
//        sendCheck(check);

    }

    private MultiValueMap<String, String> createCheck(List<Client> clients, List<Client> costPriceClients) {
        StringBuilder bodyCheck = new StringBuilder();
        Long totalAmount = 0L;
        MultiValueMap<String, String> check = new LinkedMultiValueMap<>();

        int hour = 0;
        int minute = 0;
        int passedHour = 0;
        int passedMinute = 0;
        int hourForOut = 0;
        int minuteForOut = 0;
        int numClient = 1;
        for (Client client : clients) {
            hour = client.getTimeStart().getHour();
            minute = client.getTimeStart().getMinute();
            passedHour = client.getPassedTime().getHour();
            passedMinute = client.getPassedTime().getMinute();
            hourForOut = client.getTimeStart().plusHours(passedHour).getHour();
            minuteForOut = client.getTimeStart().plusMinutes(passedMinute).getMinute();
            bodyCheck.append(numClient + ".");
            if (client.getDescription().equals("")) {
                bodyCheck.append("Гость ");
            } else if (client.getDescription().length() > 6) {
                bodyCheck.append(client.getDescription() + "\n");
            } else {
                bodyCheck.append(client.getDescription() + " ");
            }
            bodyCheck.append((hour < 10 ? "0" + hour : hour) + ":" +
                    (minute < 10 ? "0" + minute : minute));
            bodyCheck.append("-" + (hourForOut < 10 ? "0" + hourForOut : hourForOut)
                    + ":" + (minuteForOut < 10 ? "0" + minuteForOut : minuteForOut));
            if (client.getDescription().length() > 6) {
                bodyCheck.append("         " + client.getPriceTime().intValue() + "\n");
            } else {
                bodyCheck.append(" " + client.getPriceTime().intValue() + "\n");
            }

            totalAmount += Math.round(client.getPriceTime());

            if (!client.getLayerProducts().isEmpty()) {
                for (LayerProduct layerProduct : client.getLayerProducts()) {
                    int clientCount = layerProduct.getClients().size();

                    bodyCheck
                            .append(layerProduct.getName());
                    int j = 20 - layerProduct.getName().length();
                    for (int i = j; i > 0; i--) {
                        bodyCheck.append(" ");
                    }

                    if(costPriceClients.contains(client)) {
                        Product product = productService.findOne(layerProduct.getProductId());

                        Long selfCostForClient;

                        if (product.getCost().equals(0D)) {
                            selfCostForClient = Math.round(layerProduct.getCost() / clientCount);
                        } else {
                            selfCostForClient = Math.round(product.getSelfCost() / clientCount);
                        }

                        totalAmount += selfCostForClient;

                        bodyCheck.append(selfCostForClient)
                                .append("  ")
                                .append("\n");
                    } else {

                        Double costProduct = layerProduct.getCost();
                        Long costForClient = Math.round(costProduct / clientCount);

                        totalAmount += costForClient;

                        bodyCheck.append(costForClient)
                                .append("  ")
                                .append("\n");
                    }
                }
            }
            bodyCheck.append("\n");
            numClient++;
        }

        check.add("bodyCheck", bodyCheck.toString());
        check.add("totalAmount", totalAmount.toString());

        return check;
    }

       private void sendCheck(MultiValueMap<String, String> check) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(check, headers);

        System.out.println(check.get("bodyCheck"));
        System.out.println(check.get("totalAmount"));

//        try {
//            restTemplate.postForEntity(url, request, String.class);
//        } catch (RestClientException e) {
//            throw  new CheckException("Чек не был напечатан. Ошибка отправки на принтер.");
//        }
    }
}
