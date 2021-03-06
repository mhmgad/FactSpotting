package de.mpii.factspotting.text.retrievers;

import de.mpii.datastructures.AnnotatedDocument;
import de.mpii.datastructures.Document;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//import de.mpii.datastructures.SentAnnotatedDocument;

/**
 * Created by gadelrab on 12/12/16.
 */
public class EleasticSearchRetriever {
    Logger loggger= LoggerFactory.getLogger(EleasticSearchRetriever.class);


    private  String matchingThreshold;
    private  int resultSize=5;
    private  String indexName="wiki_sent";
    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    public EleasticSearchRetriever(String serverURL) {
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(serverURL)
                .multiThreaded(true)
                .connTimeout(3000000)
                .readTimeout(3000000)
                .build());
        client = factory.getObject();
    }

    public EleasticSearchRetriever(String elasticUrl,String indexName,int resultSize,String matchingThreshold) {
        this(elasticUrl);
        this.indexName = indexName;
        this.resultSize=resultSize;
        this.matchingThreshold=matchingThreshold;
    }


    public EleasticSearchRetriever(String elasticUrl,String indexName,int resultSize) {
        this(elasticUrl,indexName,resultSize,"30%");
    }

//    public EleasticSearchRetriever(String indexName) {
//        this(indexName,5);
//    }

    public List<AnnotatedDocument> getDocuments(int resultSize,String ... filteringString) throws IOException {


//        AnnotatedDocuments docs=new AnnotatedDocuments();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String s:filteringString) {

            query.must(QueryBuilders.matchQuery("text",s));
        }

        searchSourceBuilder.query(
                query
//                QueryBuilders.boolQuery()
//                        .must(QueryBuilders.matchQuery("text",filteringString))
        ).size(resultSize);
        loggger.debug("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        loggger.debug("hitsSize in response: "+response.getTotal());
        List<SearchResult.Hit<AnnotatedDocument, Void>> responseList = response.getHits(AnnotatedDocument.class);
        loggger.debug("hitsSize: "+responseList.size());
//        AnnotatedDocument fristDoc=responseList.get(0).source;
//        loggger.debug("first: "+fristDoc.getUrl());//+"\n"+fristDoc.getText());

        List<AnnotatedDocument> docList = new ArrayList<>();
        if(responseList!=null) {
            docList = responseList.stream().map(d -> d.source).collect(Collectors.toList());
            int i=0;
            // set the order in the result value
            for (AnnotatedDocument doc:docList) {
                doc.setOrder(++i);
            }
        }
//        SearchResponse response = client.prepareSearch("index1", "index2")
//                .setTypes("type1", "type2")
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
//                //.setFrom(0).setSize(60).setExplain(true)
//                .get();


        return docList;
    }


    public List<AnnotatedDocument> getByTitle(int resultSize, List<String> pages, List<String> filteringString) throws IOException {


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder titleQuery = QueryBuilders.boolQuery();

        for (String pageTitle : pages) {
            titleQuery.should(QueryBuilders.matchQuery("title", pageTitle).operator(Operator.AND).minimumShouldMatch("75%"));
        }

        BoolQueryBuilder bodyQuery = QueryBuilders.boolQuery();

        for (String s : filteringString) {

            bodyQuery.should(QueryBuilders.matchQuery("sent", s).operator(Operator.AND));
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery().must(titleQuery).must(bodyQuery);
        searchSourceBuilder.query(
                query
        ).size(resultSize);

//        loggger.debug("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        loggger.debug("hitsSize in response: " + response.getTotal());
        List<SearchResult.Hit<AnnotatedDocument, Void>> responseList = response.getHits(AnnotatedDocument.class);

        List<AnnotatedDocument> docList = new ArrayList<>();

        if (responseList != null){
            loggger.debug("hitsSize: " + responseList.size());


            docList = responseList.stream().map(d -> d.source).collect(Collectors.toList());

            int i=0;
            // set the order in the result value
            for (AnnotatedDocument doc:docList) {
                doc.setOrder(++i);
            }
        }

        return docList;
    }

    /**
     * Send query to elasticsearch using some fileds and queries to search for any of them
     * @param fieldsToSearch
     * @param queriesStrings
     * @return
     * @throws IOException
     */
    public List<Document> searchFieldsFullSentences(List<String> fieldsToSearch, List<String> queriesStrings) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder combined = QueryBuilders.boolQuery();

        for (String field:fieldsToSearch) {

            for (String queryString : queriesStrings) {

//                combined.should(QueryBuilders.matchQuery(field, queryString).operator(Operator.AND).minimumShouldMatch(this.matchingThreshold));
                combined.should(QueryBuilders.matchQuery(field, queryString).operator(Operator.AND));
            }
        }




        //BoolQueryBuilder query = QueryBuilders.boolQuery().must(combined);
        searchSourceBuilder.query(
                combined
        ).size(resultSize);


        loggger.debug("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        loggger.debug("hitsSize in response: " + response.getTotal());

        loggger.debug("response"+ response.getJsonString());
        List<SearchResult.Hit<AnnotatedDocument, Void>> responseList = response.getHits(AnnotatedDocument.class);

        List<Document> docList = new ArrayList<>();

        if (responseList != null){
            loggger.debug("hitsSize: " + responseList.size());


            docList = responseList.stream().map(d -> d.source).collect(Collectors.toList());

            int i=0;
            // set the order in the result value
            for (Document doc:docList) {
                doc.setOrder(++i);
            }
        }

        return docList;

    }


    /**
     * Send query to elasticsearch using some fields and queries to search for any of them
     * @param fieldsToSearch
     * @param queriesStrings
     * @return
     * @throws IOException
     */
    public List<Document> searchFieldsSeparately(List<String> fieldsToSearch, List<List<String>> queriesStrings) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder combined = QueryBuilders.boolQuery();

        for (List<String> queryString : queriesStrings) {
            BoolQueryBuilder paraphraseQuery = QueryBuilders.boolQuery();
            for (String queryItem:queryString) {
                BoolQueryBuilder subQuery = QueryBuilders.boolQuery();

                for (String field : fieldsToSearch) {
//                combined.should(QueryBuilders.matchQuery(field, queryString).operator(Operator.AND).minimumShouldMatch(this.matchingThreshold));
                    subQuery.should(QueryBuilders.matchPhraseQuery(field, queryItem).slop(2));
                }
                paraphraseQuery.must(subQuery);
            }
            combined.should(paraphraseQuery);

        }




        //BoolQueryBuilder query = QueryBuilders.boolQuery().must(combined);
        searchSourceBuilder.query(
                combined
        ).size(resultSize);


      //  loggger.debug("Query: "+searchSourceBuilder.toString());
//        System.out.println(searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        loggger.debug("hitsSize in response: " + response.getTotal());

        loggger.debug("response"+ response.getJsonString());
        System.out.println("response"+ response.getJsonString());
        List<SearchResult.Hit<AnnotatedDocument, Void>> responseList = response.getHits(AnnotatedDocument.class);

        List<Document> docList = new ArrayList<>();

        if (responseList != null){
            loggger.debug("hitsSize: " + responseList.size());


            docList = responseList.stream().map(d -> d.source).collect(Collectors.toList());

            int i=0;
            // set the order in the result value
            for (Document doc:docList) {
                doc.setOrder(++i);
            }
        }

        return docList;

    }

    @Override
    public String toString() {
        return "EleasticSearchRetriever{" +
                "matchingThreshold='" + matchingThreshold + '\'' +
                ", resultSize=" + resultSize +
                ", indexName='" + indexName + '\'' +
                ", factory=" + factory +
                ", client=" + client +
                '}';
    }

        public static void main(String[] args) throws IOException {
//
//        EleasticSearchRetriever f=new EleasticSearchRetriever("wiki_sent",5);
//
//
//
//        System.out.println(f.searchFieldsSeparately(Arrays.asList("sent","title"),Arrays.asList(Arrays.asList("Albert","was born in","Ulm"))));
//        System.out.println(f.getByTitle(5,Arrays.asList("albert einstein"), Arrays.asList("born in")));

//        EleasticSearchRetriever f=new EleasticSearchRetriever("wiki");
//
//        System.out.println(f.getDocuments(1,"obama"));
//        System.out.println(f.getDocuments(2,"Barak Obama"));
//        System.out.println(f.getDocuments(3,"Barak", "Obama"));
//
//        System.out.println(f.getDocuments(4,"Oscars"));
//        System.out.println(f.getDocuments(5,"Leonardo DiCaprio"));
//        System.out.println(f.getDocuments(6,"Oscars","Leonardo DiCaprio"));

    }

}
