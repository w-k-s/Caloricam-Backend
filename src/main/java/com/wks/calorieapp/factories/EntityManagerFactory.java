package com.wks.calorieapp.factories;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class EntityManagerFactory {

    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return Persistence.createEntityManagerFactory("calorieapp").createEntityManager();
    }
}
