package com.cafe.crm.models.shift;


import com.cafe.crm.dto.MenuSale;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.note.NoteRecord;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.models.user.User;
import com.cafe.crm.utils.annotation.Dateable;
import com.cafe.crm.utils.annotation.SelfDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@SqlResultSetMapping(
		name = "MenuSaleMapping",
		classes = {
				@ConstructorResult(
						targetClass = MenuSale.class,
						columns = {
								@ColumnResult(name = "productId", type = Long.class),
								@ColumnResult(name = "productName", type = String.class),
								@ColumnResult(name = "price", type = Double.class),
								@ColumnResult(name = "count", type = Long.class),
								@ColumnResult(name = "sumSale", type = Double.class),
								@ColumnResult(name = "costSale", type = Double.class),
								@ColumnResult(name = "sumPercentStuff", type = Double.class),
								@ColumnResult(name = "sumProfit", type = Double.class)
						}
				)
		}
)


@Entity
@Table(name = "shifts")
@Dateable
public class Shift extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private boolean opened;

	@Column(name = "shift_date")
	@SelfDate
	private LocalDate shiftDate;

	@Column(name = "check_value")
	private Integer checkValue;// after change on set<>

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<Calculate> calculates;

	@OneToMany(fetch = FetchType.EAGER)
	private Set<Client> clients;

	@Column(name = "cash_box")
	private double cashBox;

	private double profit;

	private double alteredCashAmount;

	@Column(name = "bank_cash_box")
	private double bankCashBox;

	@ManyToMany(mappedBy = "shifts", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<User> users;

	@OneToMany(mappedBy = "shift", cascade = CascadeType.REMOVE)
	private List<Receipt> receipts;

	// TODO: 26.07.2017 Подумать над размером
	private String comment;

	@OneToMany(mappedBy = "shift")
	private List<NoteRecord> noteRecords;

	private boolean missingShift;

	public Shift(LocalDate shiftDate, List<User> users, double bankCashBox) {
		this.shiftDate = shiftDate;
		this.users = users;
		this.bankCashBox = bankCashBox;
	}

	public Shift(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public Shift() {
	}

	public double getBankCashBox() {
		return bankCashBox;
	}

	public void setBankCashBox(double bankCashBox) {
		this.bankCashBox = bankCashBox;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getUsersNames() {
		StringBuilder usersNames = new StringBuilder();
		for (User user : users) {
			usersNames.append(user.getFirstName()).append(" ");
		}
		return usersNames.toString();
	}

	public double getCashBox() {
		return cashBox;
	}

	public void setCashBox(double cashBox) {
		this.cashBox = cashBox;
	}

	public double getProfit() {
		return profit;
	}

	public List<Receipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<Receipt> receipts) {
		this.receipts = receipts;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public double getAlteredCashAmount() {
		return alteredCashAmount;
	}

	public void setAlteredCashAmount(double alteredCashAmount) {
		this.alteredCashAmount = alteredCashAmount;
	}

	public Set<Calculate> getCalculates() {
		return calculates;
	}

	public void setCalculates(Set<Calculate> calculates) {
		this.calculates = calculates;
	}

	public Set<Client> getClients() {
		return clients;
	}

	public void setClients(Set<Client> clients) {
		this.clients = clients;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public Integer getCheckValue() {
		return checkValue;
	}

	public void setCheckValue(Integer checkValue) {
		this.checkValue = checkValue;
	}

	public boolean isOpen() {
		return opened;
	}

	public void setOpen(boolean open) {
		opened = open;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<NoteRecord> getNoteRecords() {
		return noteRecords;
	}

	public void setNoteRecords(List<NoteRecord> noteRecords) {
		this.noteRecords = noteRecords;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isMissingShift() {
		return missingShift;
	}

	public void setMissingShift(boolean missingShift) {
		this.missingShift = missingShift;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Shift shift = (Shift) o;

		if (opened != shift.opened) return false;
		if (id != null ? !id.equals(shift.id) : shift.id != null) return false;
		if (shiftDate != null ? !shiftDate.equals(shift.shiftDate) : shift.shiftDate != null) return false;
		return checkValue != null ? checkValue.equals(shift.checkValue) : shift.checkValue == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (opened ? 1 : 0);
		result = 31 * result + (shiftDate != null ? shiftDate.hashCode() : 0);
		result = 31 * result + (checkValue != null ? checkValue.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Shift{" +
				"id=" + id +
				", shiftDate=" + shiftDate +
				", opened=" + opened +
				'}';
	}
}
