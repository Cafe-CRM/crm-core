package com.cafe.crm.repositories.customRepository;

import com.cafe.crm.models.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface CommonRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {
    @Override
    List<T> findAll();

    @Override
    T getOne(ID var1);

    T findByField(String fieldName, String fieldValue);

    List<T> findAllWithFetchGraph(String graphName);

    List<T> findAllByField(String fieldName, String fieldValue);

    List<T> findAllByFieldWithFetchGraph(String fieldName, String fieldValue, String graphName);

    List<T> findAllByFieldsMapWithFetchGraph(Map<String, String> fields, String graphName);
}
