package com.cafe.crm.dto;

public class MissingUserDTO {
    private long id;

    private String firstName;

    private String lastName;

    private int shiftSalary;

    private int paidBonus;

    private boolean salary;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getShiftSalary() {
        return shiftSalary;
    }

    public void setShiftSalary(int shiftSalary) {
        this.shiftSalary = shiftSalary;
    }

    public int getPaidBonus() {
        return paidBonus;
    }

    public void setPaidBonus(int paidBonus) {
        this.paidBonus = paidBonus;
    }

    public boolean getSalary() {
        return salary;
    }

    public void setSalary(boolean salary) {
        this.salary = salary;
    }
}
