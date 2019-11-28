package com.elasticsearch.dao;

import com.elasticsearch.model.SearchResult;
import com.elasticsearch.model.User;

import java.util.List;

public interface UserDAO {

    List<User> getAllUsers();
    User getUserById(String userId);
    User addNewUser(User user);
    SearchResult pesquisaUser(String search, int page) ;
    List<User> bulkUsers(List<User> users ) ;
}