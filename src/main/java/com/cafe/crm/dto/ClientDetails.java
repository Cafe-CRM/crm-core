package com.cafe.crm.dto;


import com.cafe.crm.models.client.LayerProduct;

import java.util.ArrayList;
import java.util.List;

public class ClientDetails {
	private Double allDirtyPrice;
	private Double otherPriceMenu;
	private Double dirtyPriceMenu;
	private List<LayerProduct> dirtyProduct;
	private List<LayerProduct> otherProduct;

	public ClientDetails(Double allDirtyPrice, Double otherPriceMenu, Double dirtyPriceMenu, List<LayerProduct> dirtyProduct,
						 List<LayerProduct> otherProduct) {
		this.allDirtyPrice = allDirtyPrice;
		this.otherPriceMenu = otherPriceMenu;
		this.dirtyPriceMenu = dirtyPriceMenu;
		this.dirtyProduct = dirtyProduct;
		this.otherProduct = otherProduct;
	}

	public Double getAllDirtyPrice() {
		return allDirtyPrice;
	}

	public void setAllDirtyPrice(Double allDirtyPrice) {
		this.allDirtyPrice = allDirtyPrice;
	}

	public Double getOtherPriceMenu() {
		return otherPriceMenu;
	}

	public void setOtherPriceMenu(Double otherPriceMenu) {
		this.otherPriceMenu = otherPriceMenu;
	}

	public Double getDirtyPriceMenu() {
		return dirtyPriceMenu;
	}

	public void setDirtyPriceMenu(Double dirtyPriceMenu) {
		this.dirtyPriceMenu = dirtyPriceMenu;
	}

	public List<LayerProduct> getDirtyProduct() {
		return dirtyProduct;
	}

	public void setDirtyProduct(List<LayerProduct> dirtyProduct) {
		this.dirtyProduct = dirtyProduct;
	}

	public List<LayerProduct> getOtherProduct() {
		return otherProduct;
	}

	public void setOtherProduct(List<LayerProduct> otherProduct) {
		this.otherProduct = otherProduct;
	}
}
