package com.elasticsearch.controller;


import com.elasticsearch.dao.UserDAO;
import com.elasticsearch.model.Search;
import com.elasticsearch.model.SearchResult;
import com.elasticsearch.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestSearchController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserDAO userDAO;


    @RequestMapping(value = "/searchuser", method = RequestMethod.POST)
    public SearchResult searchUser(@RequestBody Search searchReceived) {
        LOG.info("SEARCH STRING = " + searchReceived.getSearch());
        return userDAO.pesquisaUser(searchReceived.getSearch() , searchReceived.getPage() );
    }


}



