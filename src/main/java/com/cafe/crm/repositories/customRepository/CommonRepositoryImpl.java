package com.cafe.crm.repositories.customRepository;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Transactional
public class CommonRepositoryImpl<T extends BaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements CommonRepository<T, ID> {

    protected EntityManager entityManager;

    @Autowired
    protected CompanyIdCache companyIdCache;

    protected final Class<T> persistentClass;

    //private final String findAllQuery;

    protected final String findDistinctQueryWhere;

    protected final String companyIdQuery;

    protected final String andCompanyIdQuery;

    @SuppressWarnings("unchecked")
    @Autowired
    public CommonRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;


        persistentClass = getDomainClass();
        String genericClassName = persistentClass.toGenericString();
        String className = genericClassName.substring(genericClassName.lastIndexOf('.') + 1);
        //findOneQuery = "SELECT FROM " + className;
        findDistinctQueryWhere = "SELECT DISTINCT e FROM " + className + " e WHERE ";
        companyIdQuery = "e.company.id = :companyId";
        andCompanyIdQuery = companyIdQuery + " AND ";
    }

    public List<T> findByAttributeContainsText(String attributeName, String text) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
        Root<T> root = cQuery.from(getDomainClass());
        cQuery
                .select(root)
                .where(builder
                        .like(root.<String>get(attributeName), "%" + text + "%"));
        TypedQuery<T> query = entityManager.createQuery(cQuery);
        return query.getResultList();
    }

    @Override
    public List<T> findAll() {

        try {
            return entityManager.createQuery(findDistinctQueryWhere + companyIdQuery, persistentClass)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public T getOne(ID var1) {

        try {
            return entityManager.createQuery(findDistinctQueryWhere + "e.id =:id " + andCompanyIdQuery, persistentClass)
                    .setParameter("id", var1)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public T findByField(String fieldName, String fieldValue) {

        try {
            return  entityManager.createQuery(findDistinctQueryWhere + fieldName +" = :val" + andCompanyIdQuery, persistentClass)
                    .setParameter("val", fieldValue)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .getSingleResult();
        } catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<T> findAllWithFetchGraph(String graphName) {

        EntityGraph graph = entityManager.getEntityGraph(graphName);

        try {
            return entityManager.createQuery(findDistinctQueryWhere + companyIdQuery, persistentClass)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<T> findAllByField(String fieldName, String fieldValue) {

        try {
            return  entityManager.createQuery(findDistinctQueryWhere + fieldName +" = :val" + andCompanyIdQuery, persistentClass)
                    .setParameter("val", fieldValue)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .getResultList();
        } catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<T> findAllByFieldWithFetchGraph(String fieldName, String fieldValue, String graphName) {

        EntityGraph graph = entityManager.getEntityGraph(graphName);

        try {
            return  entityManager.createQuery(findDistinctQueryWhere + fieldName +" = :val" + andCompanyIdQuery, persistentClass)
                    .setParameter("val", fieldValue)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getResultList();
        } catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<T> findAllByFieldsMapWithFetchGraph(Map<String, String> fields, String graphName) {

        EntityGraph graph = entityManager.getEntityGraph(graphName);

        StringBuilder queryBuilder = new StringBuilder(findDistinctQueryWhere);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            queryBuilder.append(entry.getKey())
                    .append(" =:")
                    .append(entry.getKey())
                    .append(" AND ");
        }

        queryBuilder.append(companyIdQuery);

        TypedQuery<T> query = entityManager.createQuery(queryBuilder.toString(), persistentClass);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        query.setParameter("companyId", companyIdCache.getCompanyId());

        try {
            return query.setHint("javax.persistence.fetchgraph", graph).getResultList();
        } catch (NoResultException e){
            return null;
        }

    }

    T test123(String dsd){
        return null;
    }

}

