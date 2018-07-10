package com.cafe.crm.repositories.fetchGraphRepository;

import com.cafe.crm.models.missingModel.MissingProduct;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Transactional
public class FetchGraphRepositoryImpl<PK extends Serializable, T> implements FetchGraphRepositoryCustom<PK, T> {

   /* @PersistenceContext
    EntityManager entityManager;

    private final Class<T> persistentClass;

    private final String companyIdQuery;

    private final String findOneQuery;

    //private final String findAllQuery;

    @SuppressWarnings("unchecked")
    public FetchGraphRepositoryImpl() {
        persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        String genericClassName = persistentClass.toGenericString();
        String className = genericClassName.substring(genericClassName.lastIndexOf('.') + 1);

        findOneQuery = "SELECT e FROM " + className + " e WHERE e.";
        companyIdQuery = " e.company.id = :companyId";
    }

    @Override
    public T findOneWithFetchGraph(PK id, String graphName, Long companyId) {
        EntityGraph graph = entityManager.getEntityGraph(graphName);

        Map<String,Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);

        return entityManager.find(persistentClass, id, hints);
    }

    @Override
    public T findOneByFiledMapWithFetchGraph(Map<String, String> values, String graphName, Long companyId) {
        EntityGraph graph = entityManager.getEntityGraph(graphName);

        StringBuilder queryBuilder = new StringBuilder(findOneQuery);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            queryBuilder.append(entry.getKey())
                    .append(" =:")
                    .append(entry.getKey())
                    .append(" AND ");
        }

        queryBuilder.append(companyIdQuery);

        TypedQuery<T> query = entityManager.createQuery(queryBuilder.toString(), persistentClass);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        try {
            return  query.setHint("javax.persistence.fetchgraph", graph).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public List<T> findAllWithFetchGraph(String graphName, Long companyId) {
        return null;
    }

    @Override
    public List<T> findAllByFiledMapWithFetchGraph(Map<String, String> values, String graphName, Long companyId) {
        return null;
    }*/

    /*public static void main(String[] args) {
        FetchGraphRepositoryImpl<Long, MissingProduct> impl = new FetchGraphRepositoryImpl();
        Map<String, String> fileds = new HashMap<>();
        fileds.put("id", "1");
        impl.findOneByFiledMapWithFetchGraph(fileds, "MissingProduct.category", 101L);
    }*/
}
