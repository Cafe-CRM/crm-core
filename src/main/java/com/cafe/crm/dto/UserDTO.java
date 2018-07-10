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

	private int shiftAmount;

    private int totalSalary;

    private int totalBonus;

    private int salaryBalance;

    private int bonusBalance;

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

    public int getShiftAmount() {
        return shiftAmount;
    }

    public void setShiftAmount(int shiftAmount) {
        this.shiftAmount = shiftAmount;
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

        if (id != null ? !id.equals(userDTO.id) : userDTO.id != null) return false;
        if (!firstName.equals(userDTO.firstName)) return false;
        if (!lastName.equals(userDTO.lastName)) return false;
        return email.equals(userDTO.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
