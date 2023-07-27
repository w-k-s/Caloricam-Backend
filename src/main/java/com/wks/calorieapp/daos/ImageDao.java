package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.ImageEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
@Named
public class ImageDao {

    private EntityManagerFactory entityManagerFactory;

    public ImageDao(){
        entityManagerFactory = Persistence.createEntityManagerFactory("calorieapp");
    }

    public boolean create(ImageEntry image) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.persist(image);
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }

    public boolean update(ImageEntry image) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.merge(image);
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }

    public boolean delete(String id) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.remove(find(id));
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }

    public ImageEntry find(String id) throws DataAccessObjectException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(ImageEntry.class, id);
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        } finally {
            entityManager.close();
        }
    }

}