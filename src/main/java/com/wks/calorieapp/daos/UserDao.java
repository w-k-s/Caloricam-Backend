package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@Named
@ApplicationScoped
public class UserDao {

    EntityManagerFactory entityManagerFactory;

    public UserDao(){
        entityManagerFactory = Persistence.createEntityManagerFactory("calorieapp");
    }

    public boolean create(User user) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }


    public User find(String id) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(User.class, id);
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }

}
