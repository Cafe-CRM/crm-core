package com.cafe.crm.repositories.menu;

import com.cafe.crm.dto.MenuSale;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MenuSaleRepository extends JpaRepository<Product, Long> {

//    @Query(value = "select activated, last_name," +
//            "first_name from " +
//            "users", nativeQuery = true)
//    List<User> getAllUsers();

//    private Product product;
//    private double price;
//    private long count;
//    private double sumSale;
//    private double costSale;
//    private double sumPercentStuff;
//    private double sumProfit;

//    @Query(value = "select product.id as product_id, product.name as product_name,  ifnull(layer_products.cost,0) as price, sum(1) as count," +
//            "sum(ifnull(layer_products.cost,0)) as sumSale, sum(ifnull(product.self_cost,0)) as costSale," +
//                "(ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100) as sumPercentStuff," +
//            "(sum(ifnull(layer_products.cost,0)) " +
//                "- sum(ifnull(product.self_cost,0)) " +
//                "- (ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)) as sumProfit " +
//            "from (select * from shifts where shift_date >= :startDate and shift_date <= :endDate) as shiftForPeriod " +
//            "left join shifts_clients on shiftForPeriod.id = shifts_clients.shift_id " +
//            "left join client_layer_product on shifts_clients.clients_id = client_layer_product.client_id " +
//            "left join layer_products on client_layer_product.layer_product_id = layer_products.id " +
//            "left join product on layer_products.product_id = product.id " +
//            "left join product_staff_percent on product.id = product_staff_percent.product_id " +
//            "where ifnull(layer_products.cost,0) > 0 " +
//            " group by product.id, product.name, ifnull(layer_products.cost,0)", nativeQuery = true)

    @Query(value =  "select product.id as product_id, " +
                    "product.name as product_name, " +
                    "ifnull(layer_products.cost,0) as price, " +
                    "sum(1) as count, " +
                    "sum(ifnull(layer_products.cost,0)) as sumSale, " +
                    "sum(ifnull(product.self_cost,0)) as costSale, " +
                    "(ifnull(layer_products.cost,0)*ifnull(product_staf f_percent.percent,0)/100) as sumPercentStuff, " +
                    "(sum(ifnull(layer_products.cost,0)) - sum(ifnull(product.self_cost,0)) - (ifnull(layer_products.cost,0)*ifnull(product_staff_percent.percent,0)/100)) as sumProfit " +
                    "from (select *  " +
                        "from (select client_layer_product.layer_product_id as layer_product_id from " +
                            "(select * fr om shifts where shift_date >= :startDate and shift_date <= :endDate and weekday(shifts.shift_date) in :weekDaysTemplate) as shiftForPeriod " +
                            "left join shifts_clients on shiftForPeriod.id = shifts_clients.shift_id " +
                            "left join client_layer_product on shifts_clients.clients_id = client_layer_product.client_id " +
                            "group by client_layer_product.layer_product_id) as layer_products_id " +
                        "where layer_produc t_id is not null) as layer_product_ids " +
                    "left join layer_products on layer_product_ids.layer_product_id = layer_products.id " +
                    "left  join product on layer_products.product_id = product.id " +
                    "left jo in product_and_categories on product_and_categories.product_id = product.id " +
                    "left  join category on product_and_categories.category_id = category.id " +
                    "left jo in product_staff_percent on product.id = product_staff_percent.product_id " +
                    "where ifnull(layer_products.cost,0) > 0 and catego ry.dirty_profit <> 0 and category.floating_price <> 1 " +
                    "group by product.id, product.name, ifnull(layer_products.cost,0)", nativeQuery = true)

    List<MenuSale> findMenuSalesByDatesAndCompanyId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("weekDaysTemplate") String weekDaysTemplate);
}
