package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GenericDao<T> {
    private Class<T> entityClass;
    protected EntityManagerFactory entityManagerFactory;
    @PersistenceContext
    protected EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(GenericDao.class);

    public GenericDao() {}

    public GenericDao(Class<T> entityClass, EntityManagerFactory entityManagerFactory) {
        this.entityClass = entityClass;
        this.entityManagerFactory = entityManagerFactory;
    }

    public void create(T entity) {
        try {
            entityManager.persist(entity);
            logger.info("Сущность {} добавлена в БД", entity);
        } catch (Exception e) {
            logger.error("Ошибка при добавлении сущности {} в БД: {}", entity, e.getMessage());
        }
    }

    public T read(long id) {
        try {
            return entityManager.find(entityClass, id);
        } catch (Exception e) {
            logger.error("Ошибка при получении сущности класса " + entityClass + "с ID: " + id + "в БД: " + e.getMessage() );
        }
        return null;
    }

    public void update(T entity) {
        try {
            entityManager.merge(entity);
            logger.info("Сущность {} обновлена в БД", entity);
        } catch (RuntimeException e) {
            logger.error("Ошибка при обновлении сущности {} в БД: {}", entity, e.getMessage());
            throw e;
        }
    }

    public void delete(long id) {
        try {
            T entity = entityManager.find(entityClass, id);
            if (entity != null) {
                entityManager.remove(entity);
            }
            logger.info("Сущность {} удалена из БД", entity);
        } catch (Exception e) {

            logger.error("Ошибка при удалении сущности класса {} с ID: {} из БД: {}", entityClass, id, e.getMessage());
        }
    }

    public List<T> findAll() {
        try {
            String hql = "FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = entityManager.createQuery(hql, entityClass);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Ошибка при получении всеъ сущностей типа {} из БД: ", entityClass);
        }
        return null;
    }
}
