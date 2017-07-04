package com.cafe.crm.models.worker;

import com.cafe.crm.models.shift.Shift;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "worker")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Worker implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@Column(name = "firstName", length = 256)
	private String firstName;

	@Column(name = "lastName", length = 256)
	private String lastName;

	@Column(name = "email")
	private String email;

	// Must be exactly 10 digits
	@Column(name = "phone")
	private Long phone;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Position> allPosition;

	@Column(name = "shiftSalary", nullable = true)
	private Long shiftSalary;

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = Shift.class)
	private Set<Shift> allShifts;

	@Column(name = "countShift", nullable = true)
	private Long countShift;

	@Column(name = "salary", nullable = true)
	private Long salary;

	@Column(name = "bonus", nullable = true)
	private Long bonus = 0L;

	@Column(name = "actionForm")
	private String actionForm;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled = true;

	public Worker() {
		this.allPosition = new ArrayList<>();
	}

	public Worker(String actionForm) {
		this.actionForm = actionForm;
		this.allPosition = new ArrayList<>();
	}

	public Worker(String firstName, String lastName, List<Position> position, Long shiftSalary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.allPosition = position;
		this.shiftSalary = shiftSalary;
	}

	public Worker(Long id, String firstName, String lastName, List<Position> position, Long shiftSalary) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.allPosition = position;
		this.shiftSalary = shiftSalary;
	}

	public Worker(String firstName, String lastName, String email, Long phone, List<Position> allPosition,
				  Long shiftSalary, Long countShift, Long salary, String actionForm, Boolean enabled) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.allPosition = allPosition;
		this.shiftSalary = shiftSalary;
		this.countShift = countShift;
		this.salary = salary;
		this.actionForm = actionForm;
		this.enabled = enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getActionForm() {
		return actionForm;
	}

	public void setActionForm(String actionForm) {
		this.actionForm = actionForm;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Position> getAllPosition() {
		return allPosition;
	}

	public void setAllPosition(List<Position> allPosition) {
		this.allPosition = allPosition;
	}

	public Long getShiftSalary() {
		return shiftSalary;
	}

	public void setShiftSalary(Long shiftSalary) {
		this.shiftSalary = shiftSalary;
	}

	public Set<Shift> getAllShifts() {
		return allShifts;
	}

	public void setAllShifts(Set<Shift> allShifts) {
		this.allShifts = allShifts;
	}

	public Long getCountShift() {
		return countShift;
	}

	public void setCountShift(Long countShift) {
		this.countShift = countShift;
	}

	public Long getSalary() {
		return salary;
	}

	public void setSalary(Long salary) {
		this.salary = salary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Worker worker = (Worker) o;

		if (id != null ? !id.equals(worker.id) : worker.id != null) return false;
		if (firstName != null ? !firstName.equals(worker.firstName) : worker.firstName != null) return false;
		return lastName != null ? lastName.equals(worker.lastName) : worker.lastName == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		return result;
	}
}
