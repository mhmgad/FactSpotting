package eleasticsearch;

import de.mpii.datastructures.AnnotatedDocument;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.SentAnnotatedDocument;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 12/12/16.
 */
public class EleasticSearchRetriever {


    private  String matchingThreshold;
    private  int resultSize=5;
    private  String indexName="wiki";
    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    public EleasticSearchRetriever() {
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .connTimeout(3000000)
                .readTimeout(300000)
                .build());
        client = factory.getObject();
    }

    public EleasticSearchRetriever(String indexName,int resultSize,String matchingThreshold) {
        this();
        this.indexName = indexName;
        this.resultSize=resultSize;
        this.matchingThreshold=matchingThreshold;
    }


    public EleasticSearchRetriever(String indexName,int resultSize) {
        this(indexName,resultSize,"10%");

    }

    public EleasticSearchRetriever(String indexName) {
        this(indexName,5);
    }

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
        System.out.println("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        System.out.println("hitsSize in response: "+response.getTotal());
        List<SearchResult.Hit<AnnotatedDocument, Void>> responseList = response.getHits(AnnotatedDocument.class);
        System.out.println("hitsSize: "+responseList.size());
//        AnnotatedDocument fristDoc=responseList.get(0).source;
//        System.out.println("first: "+fristDoc.getUrl());//+"\n"+fristDoc.getText());

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


    public List<SentAnnotatedDocument> getByTitle(int resultSize, List<String> pages, List<String> filteringString) throws IOException {


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

//        System.out.println("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        System.out.println("hitsSize in response: " + response.getTotal());
        List<SearchResult.Hit<SentAnnotatedDocument, Void>> responseList = response.getHits(SentAnnotatedDocument.class);

        List<SentAnnotatedDocument> docList = new ArrayList<>();

        if (responseList != null){
            System.out.println("hitsSize: " + responseList.size());


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
    public List<Document> searchFields(List<String> fieldsToSearch, List<String> queriesStrings) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder combined = QueryBuilders.boolQuery();


        for (String field:fieldsToSearch) {

            for (String queryString : queriesStrings) {

//                combined.should(QueryBuilders.matchQuery(field, queryString).operator(Operator.AND).minimumShouldMatch(this.matchingThreshold));
                combined.should(QueryBuilders.matchPhraseQuery(field, queryString));
            }
        }


        //BoolQueryBuilder query = QueryBuilders.boolQuery().must(combined);
        searchSourceBuilder.query(
                combined
        ).size(resultSize);


        System.out.println("Query: "+searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        SearchResult response = client.execute(search);
        System.out.println("hitsSize in response: " + response.getTotal());

        System.out.println("response"+ response.getJsonString());
        List<SearchResult.Hit<SentAnnotatedDocument, Void>> responseList = response.getHits(SentAnnotatedDocument.class);

        List<Document> docList = new ArrayList<>();

        if (responseList != null){
            System.out.println("hitsSize: " + responseList.size());


            docList = responseList.stream().map(d -> d.source).collect(Collectors.toList());

            int i=0;
            // set the order in the result value
            for (Document doc:docList) {
                doc.setOrder(++i);
            }
        }

        return docList;

    }



    public static void main(String[] args) throws IOException {

        EleasticSearchRetriever f=new EleasticSearchRetriever("wiki_sent");

        System.out.println(f.getByTitle(5,Arrays.asList("albert einstein"), Arrays.asList("born in")));

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
