package com.cafe.crm.dto;


import com.cafe.crm.models.user.Position;
import com.yc.easytransformer.annotations.Transform;

@Transform(Position.class)
public class PositionDTO {

    private Long id;

    private String name;

    private Integer percentageOfSales;

    private boolean isPositionUsePercentOfSales;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPercentageOfSales() {
        return percentageOfSales;
    }

    public void setPercentageOfSales(Integer percentageOfSales) {
        this.percentageOfSales = percentageOfSales;
    }

    public boolean isPositionUsePercentOfSales() {
        return isPositionUsePercentOfSales;
    }

    public void setIsPositionUsePercentOfSales(boolean isPositionUsePercentOfSales) {
        this.isPositionUsePercentOfSales = isPositionUsePercentOfSales;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionDTO position = (PositionDTO) o;

        return name != null ? name.equals(position.name) : position.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
