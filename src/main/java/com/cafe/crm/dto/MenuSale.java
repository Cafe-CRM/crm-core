package com.cafe.crm.dto;

import com.cafe.crm.models.menu.Product;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.SqlResultSetMapping;
import java.util.Objects;

//@NamedNativeQuery(
//        name = "getMenuSales",
//        query =
//                "select new MenuSale(product.id as product_id, product.name, ifnull(layer_products.cost,0), sum(1)," +
//                        "sum(ifnull(layer_products.cost,0)), sum(ifnull(product.self_cost,0))," +
//                        "(ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)," +
//                        "(sum(ifnull(layer_products.cost,0)) " +
//                        "- sum(ifnull(product.self_cost,0)) " +
//                        "- (ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)) ) " +
//                        "from (select * from shifts where shift_date >= :startDate and shift_date <= :endDate) as shiftForPeriod " +
//                        "left join shifts_clients on shiftForPeriod.id = shifts_clients.shift_id " +
//                        "left join client_layer_product on shifts_clients.clients_id = client_layer_product.client_id " +
//                        "left join layer_products on client_layer_product.layer_product_id = layer_products.id " +
//                        "left join product on layer_products.product_id = product.id " +
//                        "left join product_staff_percent on product.id = product_staff_percent.product_id " +
//                        "where ifnull(layer_products.cost,0) > 0 " +
//                        " group by product.id, product.name, ifnull(layer_products.cost,0)",
//        resultSetMapping = "MenuSale"
//)

//@SqlResultSetMapping(
//        name = "getMenuSales",
//        classes = @ConstructorResult(
//                targetClass = MenuSale.class,
//                columns = {
//                        @ColumnResult(name = "productId"),
//                        @ColumnResult(name = "productName"),
//                        @ColumnResult(name = "price"),
//                        @ColumnResult(name = "count"),
//                        @ColumnResult(name = "sumSale"),
//                        @ColumnResult(name = "costSale"),
//                        @ColumnResult(name = "sumPercentStuff"),
//                        @ColumnResult(name = "sumProfit")
//                }
//        )
//)

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
                "ProductId=" + productId +
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
