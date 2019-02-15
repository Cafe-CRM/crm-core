package com.cafe.crm.services.interfaces.calculate;

import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;

import java.util.List;

public interface MenuCalculateControllerService {

	LayerProduct createLayerProduct(long calculateId, long[] clientsId, long productId);

	LayerProduct createLayerProductWithFloatingPrice(long calculateId, long[] clientsId, long productId, double productPrice);

	LayerProduct addClientOnLayerProduct(long calculateId, long[] clientsId, long layerProductId);

	List<Client> deleteProductOnClient(long calculateId, long[] clientsId, long layerProductId);

	List<LayerProduct> getProductOnCalculate(long calculateId);

	void calculatePriceMenu(long calculateId);

    long getCostPriceMenu(long clientId);
}



