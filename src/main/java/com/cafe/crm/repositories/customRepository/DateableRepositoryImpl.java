package com.cafe.crm.repositories.customRepository;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.utils.CompanyIdCache;
import com.cafe.crm.utils.annotation.Dateable;
import com.cafe.crm.utils.annotation.SelfDate;
import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

@Transactional
public class DateableRepositoryImpl<T  extends BaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements DateableRepository<T, ID> {

    private final String dateFieldName;

    private final String andDateQuery;

    private final String andDateBetweenQuery;

    public DateableRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        if (!getDomainClass().isAnnotationPresent(Dateable.class)) {
            throw new ClassFormatException("Не датируемый класс");
        }

        /*логика поиска selfDate поля*/
        Field[] fields = getDomainClass().getDeclaredFields();

        int fieldsCount = 0;

        String fieldName = "";

        for (Field field : fields) {

            field.setAccessible(true);

            if (field.isAnnotationPresent(SelfDate.class)) {
                Class<?> targetType = field.getType();
                if (targetType.equals(LocalDate.class)) {
                    fieldName = field.getName();
                    fieldsCount++;
                }
            }
        }

        if (fieldsCount < 1) {
            throw new ClassFormatException("Нет ни одного selfDate поля");
        }

        if (fieldsCount > 1) {
            throw new ClassFormatException("В классе присутствует больше одного selfDate поля");
        }

        dateFieldName = fieldName;
        andDateQuery = "AND e." + dateFieldName + " = :selfDate";
        andDateBetweenQuery = "AND e." + dateFieldName + " BETWEEN :startDate and :endDate";
    }

    //@Override
    public T findBySelfDate(LocalDate selfDate) {
        return null;
        /*try {
            return entityManager.createQuery(findDistinctQueryWhere + companyIdQuery + andDateQuery, persistentClass)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setParameter(dateFieldName, selfDate)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }*/

    }

   // @Override
    public List<T> findAllBySelfDate(LocalDate selfDate) {
        return null;
        /*try {
            return entityManager.createQuery(findDistinctQueryWhere + companyIdQuery + andDateQuery, persistentClass)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setParameter(dateFieldName, selfDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }*/

    }

   // @Override
    public List<T> findAllByFieldAndSelfDate(String fieldName, String fieldValue, LocalDate selfDate) {
        return null;
       /* try {
            return entityManager.createQuery(findDistinctQueryWhere + fieldName +" = :val" + companyIdQuery + andDateQuery, persistentClass)
                    .setParameter("val", fieldValue)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setParameter(dateFieldName, selfDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
*/
    }

  //  @Override
    public List<T> findAllBySelfDateBetween(LocalDate startDate, LocalDate endDate) {
        return null;
        /*try {
            return entityManager.createQuery(findDistinctQueryWhere + companyIdQuery + andDateBetweenQuery, persistentClass)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }*/

    }

    //@Override
    public List<T> findAllByFieldAndSelfDateBetween(String fieldName, String fieldValue, LocalDate startDate, LocalDate endDate) {
        return null;
        /*try {
            return entityManager.createQuery(findDistinctQueryWhere + fieldName +" = :val" + companyIdQuery + andDateBetweenQuery, persistentClass)
                    .setParameter("val", fieldValue)
                    .setParameter("companyId", companyIdCache.getCompanyId())
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }*/

    }

    @Override
    public T findByField(String fieldName, String fieldValue) {
        return null;
    }

    @Override
    public T test123(String dsd) {
        return null;
    }
}
