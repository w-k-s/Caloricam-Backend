package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDao;
import com.wks.calorieapp.daos.ImageDao;
import com.wks.calorieapp.daos.UserDao;
import com.wks.calorieapp.entities.User;
import org.apache.log4j.Logger;

import javax.inject.Inject;

public class UserService {

    private static Logger logger = Logger.getLogger(UserService.class);

    private UserDao userDao;
    private ImageDao imageDAO;

    public UserService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void authenticate(String username, String password) {
        try {
            final User user = userDao.find(username);
            if (user == null || !user.getPassword().equals(password)) {
                throw new InvalidCredentialsException();
            }
        } catch (DataAccessObjectException e) {
            throw new RuntimeException(e);
        }
    }
}
