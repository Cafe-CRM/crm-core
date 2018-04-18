package com.cafe.crm.dto;


import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.user.User;
import com.yc.easytransformer.annotations.Transform;

import java.util.List;

@Transform(User.class)
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private List<PositionDTO> positions;
    private int shiftSalary;
    private int balance;
    private int salary;
	private int shiftAmount;
    private int bonus;
    private boolean activated = true;
    private boolean enabled = true;

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

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionDTO> positions) {
        this.positions = positions;
    }

    public int getShiftSalary() {
        return shiftSalary;
    }

    public void setShiftSalary(int shiftSalary) {
        this.shiftSalary = shiftSalary;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getShiftAmount() {
        return shiftAmount;
    }

    public void setShiftAmount(int shiftAmount) {
        this.shiftAmount = shiftAmount;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (shiftSalary != userDTO.shiftSalary) return false;
        if (balance != userDTO.balance) return false;
        if (salary != userDTO.salary) return false;
        if (shiftAmount != userDTO.shiftAmount) return false;
        if (bonus != userDTO.bonus) return false;
        if (activated != userDTO.activated) return false;
        if (enabled != userDTO.enabled) return false;
        if (!id.equals(userDTO.id)) return false;
        if (!firstName.equals(userDTO.firstName)) return false;
        if (!lastName.equals(userDTO.lastName)) return false;
        if (!email.equals(userDTO.email)) return false;
        if (!phone.equals(userDTO.phone)) return false;
        if (!password.equals(userDTO.password)) return false;
        return positions.equals(userDTO.positions);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + positions.hashCode();
        result = 31 * result + shiftSalary;
        result = 31 * result + balance;
        result = 31 * result + salary;
        result = 31 * result + shiftAmount;
        result = 31 * result + bonus;
        result = 31 * result + (activated ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", positions=" + positions +
                ", shiftSalary=" + shiftSalary +
                ", balance=" + balance +
                ", salary=" + salary +
                ", shiftAmount=" + shiftAmount +
                ", bonus=" + bonus +
                ", activated=" + activated +
                ", enabled=" + enabled +
                '}';
    }
}
