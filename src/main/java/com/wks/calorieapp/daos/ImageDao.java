package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.ImageEntry;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@ApplicationScoped
@Named
public class ImageDao {

    private static final Logger logger = Logger.getLogger(ImageDao.class);

    @Inject
    private EntityManager entityManager;

    public ImageDao() {

    }

    public boolean create(ImageEntry image) throws DataAccessObjectException {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.persist(image);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            throw new DataAccessObjectException(e);
        }
    }

    public boolean update(ImageEntry image) throws DataAccessObjectException {
        try {
            entityManager.merge(image);
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }

    public boolean delete(String id) throws DataAccessObjectException {
        try {
            entityManager.remove(find(id));
            return true;
        } catch (Exception e) {
            throw new DataAccessObjectException(e);
        }
    }

    public ImageEntry find(String id) throws DataAccessObjectException {
        try {
            return entityManager.find(ImageEntry.class, id);
        } catch (Exception e) {
            logger.info("Failed to find image with id " + id, e);
            throw new DataAccessObjectException(e);
        }
    }

}