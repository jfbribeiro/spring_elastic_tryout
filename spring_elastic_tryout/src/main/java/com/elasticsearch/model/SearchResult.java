package com.elasticsearch.model;

import java.util.List;

public class SearchResult {

   private List<User> usersList ;
   private int totalHits ;

    public SearchResult(List<User> usersList, int totalHits) {
        this.usersList = usersList;
        this.totalHits = totalHits;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }
}
