package com.cafe.crm.dto;

import java.util.Objects;

public class MenuSale {
    private Long productId;
    private String productName;
    private double price;
    private long count;
    private double sumSale;
    private double costSale;
    private double sumPercentStuff;
    private double sumProfit;

    public MenuSale() {
    }

    public MenuSale(Long productId, String productName, double price, long count, double sumSale, double costSale, double sumPercentStuff, double sumProfit) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.count = count;
        this.sumSale = sumSale;
        this.costSale = costSale;
        this.sumPercentStuff = sumPercentStuff;
        this.sumProfit = sumProfit;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public long getCount() {
        return count;
    }

    public double getSumSale() {
        return sumSale;
    }

    public double getCostSale() {
        return costSale;
    }

    public double getSumPercentStuff() {
        return sumPercentStuff;
    }

    public double getSumProfit() {
        return sumProfit;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setSumSale(double sumSale) {
        this.sumSale = sumSale;
    }

    public void setCostSale(double costSale) {
        this.costSale = costSale;
    }

    public void setSumPercentStuff(double sumPercentStuff) {
        this.sumPercentStuff = sumPercentStuff;
    }

    public void setSumProfit(double sumProfit) {
        this.sumProfit = sumProfit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuSale menuSale = (MenuSale) o;
        return Double.compare(menuSale.price, price) == 0 &&
                count == menuSale.count &&
                Double.compare(menuSale.sumSale, sumSale) == 0 &&
                Double.compare(menuSale.costSale, costSale) == 0 &&
                Double.compare(menuSale.sumPercentStuff, sumPercentStuff) == 0 &&
                Double.compare(menuSale.sumProfit, sumProfit) == 0 &&
                Objects.equals(productId, menuSale.productId) &&
                Objects.equals(productName, menuSale.productName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(productId, productName, price, count, sumSale, costSale, sumPercentStuff, sumProfit);
    }

    @Override
    public String toString() {
        return "MenuSale{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", sumSale=" + sumSale +
                ", costSale=" + costSale +
                ", sumPercentStuff=" + sumPercentStuff +
                ", sumProfit=" + sumProfit +
                '}';
    }
}
