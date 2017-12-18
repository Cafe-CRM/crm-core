package com.cafe.crm.models.menu;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "Укажите название")
	@NotEmpty(message = "Укажите название")
	@Length(min = 1, max = 30)
	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "category", fetch = FetchType.EAGER, orphanRemoval = true)
	private List<Product> products;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "categories")
	private Set<Menu> menus;

	private boolean dirtyProfit = true;

	private boolean floatingPrice;

	public Category(String name) {
		this.name = name;
	}

	public Category() {

	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Category category = (Category) o;

		if (id != category.id) return false;
		return name != null ? name.equals(category.name) : category.name == null;
	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Category{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
