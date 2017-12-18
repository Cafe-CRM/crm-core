package com.cafe.crm.models.menu;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.user.Position;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "Укажите название")
	@NotEmpty
	@Length(min = 1, max = 30)
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull(message = "Укажите описание")
	@Column(name = "description")
	private String description;

	@NotNull(message = "Укажите цену")
	@Column(name = "cost")
	private Double cost;

	@NotNull(message = "Укажите себестоимость")
	private Double selfCost = 0D;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Category.class)
	@JoinTable(name = "product_and_categories", joinColumns = {@JoinColumn(name = "product_id")}, inverseJoinColumns = {@JoinColumn(name = "category_id")})
	private Category category;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyJoinColumn(name = "ingredient")
	@Column(name = "amount")
	private Map<Ingredients, Double> recipe;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyJoinColumn(name = "position")
	@Column(name = "percent")
	private Map<Position, Integer> staffPercent;

	private int rating;

	public Product() {
	}

	public Product(String name, String description, Double cost) {
		this.name = name;
		this.description = description;
		this.cost = cost;
	}

	public Map<Ingredients, Double> getRecipe() {
		return recipe;
	}

	public void setRecipe(Map<Ingredients, Double> recipe) {
		this.recipe = recipe;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getSelfCost() {
		return selfCost;
	}

	public void setSelfCost(Double selfCost) {
		this.selfCost = selfCost;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Product product = (Product) o;

		if (name != null ? !name.equals(product.name) : product.name != null) return false;
		if (description != null ? !description.equals(product.description) : product.description != null) return false;
		return cost != null ? cost.equals(product.cost) : product.cost == null;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (cost != null ? cost.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", cost=" + cost +
				'}';
	}

	public Map<Position, Integer> getStaffPercent() {
		return staffPercent;
	}

	public void setStaffPercent(Map<Position, Integer> staffPercent) {
		this.staffPercent = staffPercent;
	}
}
