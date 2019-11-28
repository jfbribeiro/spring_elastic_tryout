package com.elasticsearch.controller;

import com.elasticsearch.dao.UserDAO;
import com.elasticsearch.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserDAO userDAO;

    @RequestMapping("/all")
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @RequestMapping("/id/{userId}")
    public User getUser(@PathVariable String userId) {
        LOG.info("Getting user with ID: {}", userId);
        User user = userDAO.getUserById(userId);
        LOG.info("User with ID: {} is {}", userId, user);
        return user;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public User addNewUsers(@RequestBody User user) {
        LOG.info("Adding user : {}", user);
        userDAO.addNewUser(user);
        LOG.info("Added user : {}", user);
        return user;
    }

    @RequestMapping(value = "/newbulk", method = RequestMethod.POST)
    public List<User> addNewUsers(@RequestBody List<User> users) {
        LOG.info("Adding bulk users ");
        userDAO.bulkUsers(users);
        LOG.info("Added bulk users ");
        return users;
    }


}