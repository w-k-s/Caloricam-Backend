package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.ImageEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

@ApplicationScoped
@Named
public class ImageDao {

    @Inject
    private EntityManager entityManager;

    public ImageDao() {

    }

    public boolean create(ImageEntry image) throws DataAccessObjectException {
        try {
            entityManager.persist(image);
            return true;
        } catch (Exception e) {
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
            throw new DataAccessObjectException(e);
        }
    }

}