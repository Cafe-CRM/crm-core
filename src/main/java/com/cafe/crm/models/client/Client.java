package com.cafe.crm.models.client;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.discount.Discount;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yc.easytransformer.annotations.NotTransform;
import com.yc.easytransformer.annotations.Transform;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Entity
@Table(name = "clients")
@Transform(Client.class)
public class Client extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@Size(max = 30)
	@NotNull
	private String description = "";

	private boolean isPause = false;  // now is paused ?

	private boolean pausedIndex = false;     // true - if was pause

	@NotTransform
	private LocalDateTime timeStart;

	private boolean state = true;// Open or Closed

	private boolean deleteState = false;// Open or Closed
	@NotNull
	@Max(100)
	private Long discount = 0L;//связанный обьект отдает скидку в это поле

	@Max(100)
	@NotNull
	private Long discountWithCard = 0L;

	private Double allPrice = 0D;//initial amount

	private Double cache = 0D;// ready money

	@NotTransform
	private LocalTime passedTime;

	private Double priceMenu = 0D;

	private Double priceTime = 0D;

	@NotNull
	@NotTransform
	private Double payWithCard = 0D;

	@ManyToMany(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinTable(name = "client_layer_product",
			joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name = "layer_product_id", referencedColumnName = "id")})
	@NotTransform
	private List<LayerProduct> layerProducts;

	@ManyToOne
	@NotTransform
	private Card card;

	@ManyToOne
	@NotTransform
	private Discount discountObj;

	public Client() {
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean pause) {
		isPause = pause;
	}

	public boolean isPausedIndex() {
		return pausedIndex;
	}

	public void setPausedIndex(boolean pausedIndex) {
		this.pausedIndex = pausedIndex;
	}

	public Discount getDiscountObj() {
		return discountObj;
	}

	public void setDiscountObj(Discount discountObj) {
		this.discountObj = discountObj;
	}

	public boolean isDeleteState() {
		return deleteState;
	}

	public void setDeleteState(boolean deleteState) {
		this.deleteState = deleteState;
	}

	public List<LayerProduct> getLayerProducts() {
		return layerProducts;
	}

	public void setLayerProducts(List<LayerProduct> layerProducts) {
		this.layerProducts = layerProducts;
	}

	public Long getDiscountWithCard() {
		return discountWithCard;
	}

	public void setDiscountWithCard(Long discountWithCard) {
		this.discountWithCard = discountWithCard;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Double getCache() {
		return cache;
	}

	public void setCache(Double cache) {
		this.cache = cache;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Long getDiscount() {
		return discount;
	}

	public void setDiscount(Long discount) {
		this.discount = discount;
	}

	public Double getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(Double allPrice) {
		this.allPrice = allPrice;
	}

	public LocalTime getPassedTime() {
		return passedTime;
	}

	public void setPassedTime(LocalTime passedTime) {
		this.passedTime = passedTime;
	}

	public Double getPriceMenu() {
		return priceMenu;
	}

	public void setPriceMenu(Double priceMenu) {
		this.priceMenu = priceMenu;
	}

	public Double getPriceTime() {
		return priceTime;
	}

	public void setPriceTime(Double priceTime) {
		this.priceTime = priceTime;
	}

	public Double getPayWithCard() {
		return payWithCard;
	}

	public void setPayWithCard(Double payWithCard) {
		this.payWithCard = payWithCard;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(LocalDateTime timeStart) {
		this.timeStart = timeStart;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Client client = (Client) o;

		if (id != null ? !id.equals(client.id) : client.id != null) return false;
		return description != null ? description.equals(client.description) : client.description == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}
