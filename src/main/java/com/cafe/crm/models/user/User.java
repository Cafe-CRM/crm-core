package com.cafe.crm.models.user;

import com.cafe.crm.dto.UserDTO;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.utils.PatternStorage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yc.easytransformer.annotations.NotTransform;
import com.yc.easytransformer.annotations.Transform;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties({"password", "roles", "positions", "shifts", "shiftSalary", "userSalaryDetail"})
@Transform(UserDTO.class)
public class User extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank(message = "Поле \"firstName\" не может быть пустым")
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "Поле \"lastName\" не может быть пустым")
	@Column(nullable = false)
	private String lastName;

	@Pattern(regexp = PatternStorage.EMAIL, message = "Поле \"email\" должно соответствовать шаблону (пример mail@mail.ru)")
	@Column(unique = true)
	private String email;

	@Pattern(regexp = PatternStorage.PHONE, message = "Поле \"phone\" должно соответствовать шаблону")
	@Column(unique = true)
	private String phone;

	@NotBlank(message = "Поле \"password\" не может быть пустым")
	@Column(nullable = false)
	private String password;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "users_roles", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "role_id")})
	@NotTransform
	private Set<Role> roles;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "users_positions", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "position_id")})
	private List<Position> positions;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_shifts", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "shift_id")})
	@NotTransform
	private Set<Shift> shifts;

	@Min(value = 0, message = "Поле \"shiftSalary\" должно быть цифрой большей 0!")
	@Max(value = Integer.MAX_VALUE, message = "Поле \"shiftSalary\" должно быть цифрой меньшей 2147483647!")
	@Column(name = "shift_salary")
	private int shiftSalary;

	@Min(value = 0, message = "Поле \"salary\" должно быть цифрой большей 0!")
	@Max(value = Integer.MAX_VALUE, message = "Поле \"salary\" должно быть цифрой меньшей 2147483647!")
	private int totalSalary;

	@Min(value = 0, message = "Поле \"salary\" должно быть цифрой большей 0!")
	@Max(value = Integer.MAX_VALUE, message = "Поле \"salary\" должно быть цифрой меньшей 2147483647!")
	private int totalBonus;

	@Max(value = Integer.MAX_VALUE, message = "Поле \"bonus\" должно быть цифрой меньшей 2147483647!")
	private int salaryBalance;

	@Max(value = Integer.MAX_VALUE, message = "Поле \"bonus\" должно быть цифрой меньшей 2147483647!")
	private int bonusBalance;

	private boolean activated = true;

	private boolean enabled = true;

	@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
	@NotTransform
	private List<UserSalaryDetail> userSalaryDetail;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

	public Set<Shift> getShifts() {
		return shifts;
	}

	public void setShifts(Set<Shift> shifts) {
		this.shifts = shifts;
	}

	public int getShiftSalary() {
		return shiftSalary;
	}

	public void setShiftSalary(int shiftSalary) {
		this.shiftSalary = shiftSalary;
	}

	public int getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(int totalSalary) {
		this.totalSalary = totalSalary;
	}

	public int getTotalBonus() {
		return totalBonus;
	}

	public void setTotalBonus(int totalBonus) {
		this.totalBonus = totalBonus;
	}

	public int getSalaryBalance() {
		return salaryBalance;
	}

	public void setSalaryBalance(int salaryBalance) {
		this.salaryBalance = salaryBalance;
	}

	public int getBonusBalance() {
		return bonusBalance;
	}

	public void setBonusBalance(int bonusBalance) {
		this.bonusBalance = bonusBalance;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<UserSalaryDetail> getUserSalaryDetail() {
		return userSalaryDetail;
	}

	public void setUserSalaryDetail(List<UserSalaryDetail> userSalaryDetail) {
		this.userSalaryDetail = userSalaryDetail;
	}

	public void addSalaryDetail(UserSalaryDetail detail) {
		this.userSalaryDetail.add(detail);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		if (!firstName.equals(user.firstName)) return false;
		if (!lastName.equals(user.lastName)) return false;
		if (!email.equals(user.email)) return false;
		return phone.equals(user.phone);
	}

	@Override
	public int hashCode() {
		int result = firstName.hashCode();
		result = 31 * result + lastName.hashCode();
		result = 31 * result + email.hashCode();
		result = 31 * result + phone.hashCode();
		return result;
	}
}
