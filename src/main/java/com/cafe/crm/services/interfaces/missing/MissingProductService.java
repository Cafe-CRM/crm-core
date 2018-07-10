package com.cafe.crm.services.interfaces.missing;


import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;

import java.util.List;

public interface MissingProductService {

    List<MissingProduct> findAll();

    //List<MissingProduct> getAllWithFetchGraph(String graphName);

    List<MissingProduct> saveAll(List<MissingProduct> products);

    MissingProduct saveAndFlush(MissingProduct product);

    MissingProduct findOne(Long id);

    void delete(Long id);

    List<MissingProduct> findByShift(Shift shift);

    List<MissingProduct> findByShiftWithCategory(Shift shift);

    void deleteByShift(Shift shift);

    void deleteByShiftIdIn(long[] ids);
}
