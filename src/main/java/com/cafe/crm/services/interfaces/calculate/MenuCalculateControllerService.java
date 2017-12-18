package com.cafe.crm.services.interfaces.calculate;

import com.cafe.crm.models.client.LayerProduct;

import java.util.Set;

public interface MenuCalculateControllerService {

    LayerProduct createLayerProduct(long calculateId, long[] clientsId, long productId);

    LayerProduct createLayerProductWithFloatingPrice(long calculateId, long[] clientsId, long productId, double productPrice);

    LayerProduct addClientOnLayerProduct(long calculateId, long[] clientsId, long layerProductId);

    LayerProduct deleteProductOnClient(long calculateId, long[] clientsId, long layerProductId);

    Set<LayerProduct> getProductOnCalculate(long calculateId);

	void calculatePriceMenu(long calculateId);
}



