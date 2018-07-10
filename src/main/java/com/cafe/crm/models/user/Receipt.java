package com.cafe.crm.models.user;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.utils.annotation.Dateable;
import com.cafe.crm.utils.annotation.SelfDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Dateable
public class Receipt extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String receiptComment;

    private Double receiptAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @SelfDate
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;


    public Receipt(){
    }

    public Receipt(String receiptComment, Double receiptAmount, LocalDate date) {
        this.receiptComment = receiptComment;
        this.receiptAmount = receiptAmount;
        this.date = date;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptComment() {
        return receiptComment;
    }

    public void setReceiptComment(String receiptComment) {
        this.receiptComment = receiptComment;
    }

    public Double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(Double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        return Objects.equals(id, receipt.id) &&
                Objects.equals(receiptComment, receipt.receiptComment) &&
                Objects.equals(receiptAmount, receipt.receiptAmount) &&
                Objects.equals(date, receipt.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiptComment, receiptAmount, date);
    }
}
