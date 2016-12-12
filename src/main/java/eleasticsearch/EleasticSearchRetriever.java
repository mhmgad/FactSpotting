package eleasticsearch;

import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.AnnotatedDocuments;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by gadelrab on 12/12/16.
 */
public class EleasticSearchRetriever {


    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    public EleasticSearchRetriever() {
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }

    public AnnotatedDocuments getDocuments(String filteringString) throws IOException {


        AnnotatedDocuments docs=new AnnotatedDocuments();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("text",filteringString))
//                        .should(QueryBuilders.matchQuery("field2", "7654321"))
        );

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex("wiki")
                .build();

        SearchResult response = client.execute(search);
        System.out.println("hitsSize in response: "+response.getTotal());
        List<SearchResult.Hit<AnnotatedDocument, Void>> doclist = response.getHits(AnnotatedDocument.class);
        System.out.println("hitsSize: "+doclist.size());
        //List<AnnotatedDocument> articles = doclist.getSourceAsObjectList(AnnotatedDocument.class);

//        SearchResponse response = client.prepareSearch("index1", "index2")
//                .setTypes("type1", "type2")
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
//                //.setFrom(0).setSize(60).setExplain(true)
//                .get();


        return null;
    }

    public static void main(String[] args) throws IOException {

        EleasticSearchRetriever f=new EleasticSearchRetriever();

        System.out.println(f.getDocuments("obama"));
        System.out.println(f.getDocuments("Obama"));
        System.out.println(f.getDocuments("Barak Obama"));
        System.out.println(f.getDocuments("Oscars"));

    }

}
