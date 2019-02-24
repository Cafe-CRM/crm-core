package com.cafe.crm.dto;

import java.time.LocalDate;
import java.util.Objects;

public class SaleProductOnDay {
    private Long productId;
    private String productName;
    private LocalDate date;
    private long count;

    public SaleProductOnDay() {
    }

    public SaleProductOnDay(Long productId, String productName, LocalDate date, long count) {
        this.productId = productId;
        this.productName = productName;
        this.date = date;
        this.count = count;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleProductOnDay that = (SaleProductOnDay) o;
        return count == that.count &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(productId, productName, date, count);
    }

    @Override
    public String toString() {
        return "SaleProductOnDay{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", date=" + date +
                ", count=" + count +
                '}';
    }
}