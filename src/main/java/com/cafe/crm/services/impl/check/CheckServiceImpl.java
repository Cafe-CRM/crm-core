package com.cafe.crm.services.impl.check;

import com.cafe.crm.exceptions.check.CheckException;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.services.interfaces.check.CheckService;
import com.cafe.crm.services.interfaces.menu.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckServiceImpl implements CheckService {

    private final ProductService productService;
    private final RestTemplate restTemplate;

    @Value("${check.url}")
    private String url;

    @Autowired
    public CheckServiceImpl(ProductService productService, RestTemplateBuilder restTemplateBuilder) {
        this.productService = productService;
        restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void printCheck(List<Client> clients) {
        Map<String, String> check = createCheck(clients, Collections.EMPTY_LIST);
        printCheck(check);
    }

    @Override
    public void printCheckWithCostPrice(List<Client> clients, List<Client> costPriceClients) {
        Map<String, String> check = createCheck(clients, costPriceClients);
        printCheck(check);
    }

    @Override
    public void printCheckWithNewAmount(List<Client> clients, Double modifiedAmount) {
        Map<String, String> check = createCheck(clients, Collections.EMPTY_LIST);

        StringBuilder totalAmount = new StringBuilder();
        totalAmount.append(check.get("totalAmount"));
        totalAmount.append(" -> ");
        totalAmount.append(modifiedAmount);

        check.put("totalAmount", totalAmount.toString());

        printCheck(check);
    }

    private Map<String, String> createCheck(List<Client> clients, List<Client> costPriceClients) {
        StringBuilder bodyCheck = new StringBuilder();
        Long totalAmount = 0L;
        Map<String, String> check = new LinkedHashMap<>();

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

        check.put("bodyCheck", bodyCheck.toString());
        check.put("totalAmount", totalAmount.toString());

        return check;
    }

    private void printCheck(Map<String, String> check) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(check);

        System.out.println(check.get("bodyCheck"));
        System.out.println(check.get("totalAmount"));

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            throw  new CheckException("Чек не был напечатан. Ошибка отправки на принтер.");
        }
    }
}
