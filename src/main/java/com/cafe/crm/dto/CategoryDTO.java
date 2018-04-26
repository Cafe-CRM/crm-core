package com.cafe.crm.dto;

import com.cafe.crm.models.menu.Category;
import com.cafe.crm.models.menu.Menu;
import com.cafe.crm.models.menu.Product;

import java.util.List;
import java.util.Set;


public class CategoryDTO {

	private Long id;

	private String name;

	private List<Product> products;

	private Set<Menu> menus;

	private boolean dirtyProfit = true;

	private boolean floatingPrice;

	private boolean accountability;

	public CategoryDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		this.products = category.getProducts();
		this.menus = category.getMenus();
		this.dirtyProfit = category.isDirtyProfit();
		this.floatingPrice = category.isFloatingPrice();
		this.accountability = category.isAccountability();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Set<Menu> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}

	public boolean isDirtyProfit() {
		return dirtyProfit;
	}

	public void setDirtyProfit(boolean dirtyProfit) {
		this.dirtyProfit = dirtyProfit;
	}

	public boolean isFloatingPrice() {
		return floatingPrice;
	}

	public void setFloatingPrice(boolean floatingPrice) {
		this.floatingPrice = floatingPrice;
	}

	public boolean isAccountability() {
		return accountability;
	}

	public void setAccountability(boolean accountability) {
		this.accountability = accountability;
	}
}
