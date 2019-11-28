package com.elasticsearch.dao;

import com.elasticsearch.model.SearchResult;
import com.elasticsearch.model.User;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

@Repository
public class UserDAOImpl implements UserDAO {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.index.name}")
    private String indexName;

    @Value("${elasticsearch.user.type}")
    private String userTypeName;

    //@Autowired
    //private ElasticsearchTemplate esTemplate;

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Override
    public List<User> getAllUsers() {
        SearchQuery getAllQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery()).build();
        return esTemplate.queryForList(getAllQuery, User.class);
    }

    @Override
    public User getUserById(String userId) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("userId", userId)).build();
        List<User> users = esTemplate.queryForList(searchQuery, User.class);
        if(!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public List<User> bulkUsers(List<User> users) {

        List<IndexQuery> bulkQuery = new ArrayList<IndexQuery>();
        for (User u : users) {
            IndexQuery userQuery = new IndexQuery();
            userQuery.setIndexName(indexName);
            userQuery.setType(userTypeName);
            userQuery.setObject(u);
            bulkQuery.add(userQuery);
        }
        esTemplate.bulkIndex(bulkQuery);
        esTemplate.refresh(indexName);

        return users;
    }

    @Override
    public User addNewUser(User user) {

        IndexQuery userQuery = new IndexQuery();
        userQuery.setIndexName(indexName);
        userQuery.setType(userTypeName);
        userQuery.setObject(user);
        LOG.info("User indexed: {}", esTemplate.index(userQuery));
        esTemplate.refresh(indexName);
        return user;
    }




    @Override
    public SearchResult pesquisaUser(String search , int page) {

        SearchQuery getPesquisaUser = new NativeSearchQueryBuilder()
                .withQuery(  wildcardQuery( "name" , "*"+search.toLowerCase()+"*" ) ).withTrackScores(true).withPageable(PageRequest.of(page, 10  )).build();
        SearchResult result = esTemplate.query(getPesquisaUser, new ResultsExtractor<SearchResult >() {
            @Override
            public SearchResult  extract(SearchResponse searchResponse) {
                int totalHits = (int) searchResponse.getHits().totalHits;
                List<User> userList = new ArrayList<User>();
                for (SearchHit hit : searchResponse.getHits() ) {
                    if (hit != null) {
                     User u = new User( (String) hit.getSourceAsMap().get("userId") , (String) hit.getSourceAsMap().get("name")  );
                     userList.add(u);
                    }
                }
                return new SearchResult(userList, totalHits);
            }
        });

        return result;
    }



}
