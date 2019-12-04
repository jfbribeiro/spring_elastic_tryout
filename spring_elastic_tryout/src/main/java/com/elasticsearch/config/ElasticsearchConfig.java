package com.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.scheme}")
    private String elasticsearchScheme;

    @Value("${elasticsearch.port}")
    private int elasticsearchPort;

    private boolean databaseReady = false;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final long MAX_TIME_SERVER_START = 300000;
    private long startTime = 0;
    private final long TIME_ADDER = 30000;
    private boolean isCreated = false ;
    public RestHighLevelClient client ;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() throws InterruptedException {
            while( !isCreated && startTime < MAX_TIME_SERVER_START) {
                createConnection(startTime);
            }
            return client;
    }

    public boolean createConnection( long startTime  ) throws InterruptedException {

        if( startTime < MAX_TIME_SERVER_START ) {
            try {
                client = new RestHighLevelClient(
                        RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchScheme)));

                GetIndexRequest getRequest = new GetIndexRequest().indices("user");
                boolean testIndexIsExists = client.indices().exists(getRequest, RequestOptions.DEFAULT);
                if (!testIndexIsExists) {

                    CreateIndexRequest request = new CreateIndexRequest("user");
                    request.settings(Settings.builder()
                            .put("index.number_of_shards", 1)
                            .put("index.number_of_replicas", 2)
                    );

                    request.mapping("_doc",
                            "{\n" +
                                    "   \"_doc\":  {\n" +
                                    "       \"properties\": {\n" +
                                    "         \"userId\": {\n" +
                                    "            \"type\": \"text\" \n" +
                                    "         },\n" +
                                    "         \"name\" : {\n" +
                                    "            \"type\" : \"text\" \n" +
                                    "           } \n" +
                                    "      }\n" +
                                    "  }\n" +
                                    "}",
                            XContentType.JSON);

                    client.indices().create(request, RequestOptions.DEFAULT);
                }
                 isCreated = true ;
            } catch (Exception e) {
                LOG.info("Elasticsearch database not ready");
                startTime += TIME_ADDER;
                TimeUnit.MILLISECONDS.sleep(TIME_ADDER);
            } finally {
                if(!isCreated) {
                    createConnection(startTime);
                }
            }
        }
            return isCreated;
    }



    @Bean
    public ElasticsearchRestTemplate elasticsearchTemplate() throws InterruptedException {
        try {
            return new ElasticsearchRestTemplate(client());
        }
        catch (Exception e){
            LOG.error("Elasticsearch database connection was not properly created.");
        }
        return null;
    }
}
