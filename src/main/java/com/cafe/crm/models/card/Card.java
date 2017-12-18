package com.cafe.crm.models.card;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String phoneNumber;

	private String token;

	private String name;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "photo")
	@Lob
	private byte[] photo = new byte[1];

	@Email
	private String email;

	private boolean advertising = true;

	@Range(min = 0, max = 100)
	private Long discount = 0L;

	private Double balance = 0D;

	private Double spend = 0D;

	private LocalDate visitDate;

	private File qrCode;

	private String keyNameQrCode;

	private String surname;

	private String accessKey;

	private String secretKey;

	private String link;

	private Boolean activatedCard = false;

	@ElementCollection
	private Set<Long> idMyInvitedUsers = new HashSet<>();

	private Long WhoInvitedMe;

	public Card() {
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Boolean getActivatedCard() {
		return activatedCard;
	}

	public void setActivatedCard(Boolean activatedCard) {
		this.activatedCard = activatedCard;
	}

	public Set<Long> getIdMyInvitedUsers() {
		return idMyInvitedUsers;
	}

	public void setIdMyInvitedUsers(Set<Long> idMyInvitedUsers) {
		this.idMyInvitedUsers = idMyInvitedUsers;
	}

	public Long getWhoInvitedMe() {
		return WhoInvitedMe;
	}

	public void setWhoInvitedMe(Long whoInvitedMe) {
		WhoInvitedMe = whoInvitedMe;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getDiscount() {
		return discount;
	}

	public void setDiscount(Long discount) {
		this.discount = discount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getSpend() {
		return spend;
	}

	public void setSpend(Double spend) {
		this.spend = spend;
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDate visitDate) {
		this.visitDate = visitDate;
	}

	public File getQrCode() {
		return qrCode;
	}

	public void setQrCode(File qrCode) {
		this.qrCode = qrCode;
	}

	public String getKeyNameQrCode() {
		return keyNameQrCode;
	}

	public void setKeyNameQrCode(String keyNameQrCode) {
		this.keyNameQrCode = keyNameQrCode;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isAdvertising() {
		return advertising;
	}

	public void setAdvertising(boolean advertising) {
		this.advertising = advertising;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		if (id != null ? !id.equals(card.id) : card.id != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(card.phoneNumber) : card.phoneNumber != null) return false;
		if (token != null ? !token.equals(card.token) : card.token != null) return false;
		if (name != null ? !name.equals(card.name) : card.name != null) return false;
		if (!Arrays.equals(photo, card.photo)) return false;
		return email != null ? email.equals(card.email) : card.email == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (token != null ? token.hashCode() : 0);
		return result;
	}
}
