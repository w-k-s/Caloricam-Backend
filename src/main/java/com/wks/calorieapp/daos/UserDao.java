package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

@Named
@ApplicationScoped
public class UserDao {

    @Inject
    EntityManager entityManager;

    public UserDao() {
    }

    public boolean create(User user) throws DataAccessObjectException {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }


    public User find(String id) throws DataAccessObjectException {
        try {
            return entityManager.find(User.class, id);
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }

}
