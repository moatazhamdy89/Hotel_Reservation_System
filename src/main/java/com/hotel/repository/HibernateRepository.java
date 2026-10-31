package com.hotel.repository;

import com.hotel.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Function;

public class HibernateRepository {

    // Save a NEW entity
    // <T> ==> btkhly no3 el project yt7dd w2t ma nstd3y el method
    public <T> T save(T entity) {
        // withTransaction ==> Commit for save
        // or Rollback ==> not saved
        return withTransaction(session -> {
            session.persist(entity); // ==> persist used for notlob mn el hibernate to save new entity
            return entity;
        });
    }

    // Find an entity using its primary key
    public <T> T findById(Class<T> entityClass, Object id) {
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            return session.find(entityClass, id);
        }
    }

    // Get all records of an entity
    public <T> List<T> findAll(Class<T> entityClass) {
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            return session.createSelectionQuery(
                    "from " + entityClass.getName(),
                    entityClass
            ).getResultList();
        }
    }

    // Update an existing entity
    public <T> T update(T entity) {
        return withTransaction(session ->
                session.merge(entity)
        );
    }

    // Delete an entity using its primary key
    public <T> boolean deleteById(Class<T> entityClass, Object id) {
        return withTransaction(session -> {

            T entity = session.find(entityClass, id);

            if (entity == null) {
                return false;
            }

            session.remove(entity);
            return true;
        });
    }

    // Run multiple operations in ONE transaction
    public <R> R withTransaction(Function<Session, R> action) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                R result = action.apply(session);

                tx.commit();

                return result;

            } catch (RuntimeException e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }
}