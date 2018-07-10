package com.cafe.crm.repositories.customRepository;

import com.cafe.crm.models.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface DateableRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

    T findByField(String fieldName, String fieldValue);

    T test123(String dsd);

    //T findBySelfDate(LocalDate selfDate);

    //List<T> findAllBySelfDate(LocalDate selfDate);

    //List<T> findAllByFieldAndSelfDate(String fieldName, String fieldValue, LocalDate selfDate);

    //List<T> findAllBySelfDateBetween(LocalDate startDate, LocalDate endDate);

    //List<T> findAllByFieldAndSelfDateBetween(String fieldName, String fieldValue, LocalDate startDate, LocalDate endDate);

}
