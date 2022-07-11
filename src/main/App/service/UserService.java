package main.BankApp.service;

import main.BankApp.data.interfaces.UserDao;
import main.BankApp.data.methods.UserSQL;
import main.BankApp.models.User;

public class UserService {
    private UserDao userDao;
    public UserService() { userDao = new UserSQL(); }

    //add username exception
    //@Override
    public User createUser(User user) {
        userDao.create(user);
        return user;
    }

    public User login(String username, String password){
        User user = findByUsername(username);
        if(user != null && password.equals(user.getPassword())) {
            System.out.println("You're logged in!");
            user = userDao.getUserInfo(user);
            return user;
        } else {
            System.out.println("Your credentials didn't match!");
            return null;
        }
    }

    //@Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
