package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.FoodEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Named
@ApplicationScoped
public class FoodDao {

    @Inject
    private EntityManager entityManager;

    public FoodDao() {
    }


    public long create(FoodEntry item) throws DataAccessObjectException {
        try {
            entityManager.persist(item);
            entityManager.flush();
            return item.getFoodId();
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }

    public FoodEntry read(long id) throws DataAccessObjectException {
        try {
            return entityManager.find(FoodEntry.class, id);
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }

    public FoodEntry read(String name) throws DataAccessObjectException {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodEntry> q = cb.createQuery(FoodEntry.class);
            Root<FoodEntry> root = q.from(FoodEntry.class);
            q.select(root).where(cb.equal(root.get("name"), name));
            return entityManager.createQuery(q).getResultList().get(0);
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }
}
