package de.mpii.factspotting.text.retrievers;

import com.google.common.base.Joiner;
import de.mpii.datastructures.AnnotatedDocument;
import de.mpii.datastructures.Document;
import de.mpii.factspotting.config.Keys;
import it.unipi.di.acube.searchapi.CachedWebsearchApi;
import it.unipi.di.acube.searchapi.WebsearchApi;
import it.unipi.di.acube.searchapi.callers.BingSearchApiCaller;
import it.unipi.di.acube.searchapi.model.WebsearchResponse;
import it.unipi.di.acube.searchapi.model.WebsearchResponseEntry;

import javax.print.Doc;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BingRetriever {


    private static final Double SIM_THRESHOLD = 0.5;
    private final int numberOfResults;
    BingSearchApiCaller caller ;
    CachedWebsearchApi api;
    SnippetsExtractor snippetsExtractor=new SnippetsExtractor();

    public BingRetriever(int numberOfResults, String cacheFilepath) throws IOException, ClassNotFoundException {
        this.caller = new BingSearchApiCaller(Keys.getInstance().getBingKey());

        this.api= CachedWebsearchApi.builder().api(caller).path(cacheFilepath).create();


        this.numberOfResults=numberOfResults;
    }



    public synchronized List<Document> search(String query) throws Exception {
        System.out.println(query);
        WebsearchResponse response = api.query(query, numberOfResults);
//        System.out.println(response.getJsonResponses());
        List<Document> docs=new LinkedList<>();

        int order=0;

        for (WebsearchResponseEntry entry : response.getWebEntries()){
//            System.out.println(entry.getDisplayUrl());
//            System.out.println(entry.getSnippet());

            docs.add(new AnnotatedDocument(entry.getName(),entry.getName(),entry.getSnippet(),entry.getDisplayUrl(),order++));

        }



        TimeUnit.MILLISECONDS.sleep(1000);
        return docs;
    }


    public List<Document> searchParaphrases(List<String> queryParaphrases) throws Exception {
        Set<Document> docs=new HashSet<>();



        docs.addAll(search(Joiner.on(" | ").join(queryParaphrases.stream().map(s->s.replace('\t',' ')).collect(Collectors.toList()))));

//        for (String query:queryParaphrases) {
//                docs.addAll(search(query.replace('\t',' ')));
//        }
//        System.out.println("docs ret"+ docs.size() );
        Map<Document,Double> allSnippets=new HashMap<>();


        for (String query:queryParaphrases) {
            for (Document doc: docs) {
                Map<String,Double> snipptsWeights=snippetsExtractor.getSimilatrSnippet(doc.getUrl(),query);
                for (Map.Entry<String,Double> entry:  snipptsWeights.entrySet()) {
                        String snippet=entry.getKey();
                        double score=entry.getValue();
                        Document snippetDoc = new AnnotatedDocument(doc.getId(), doc.getTitle(), snippet, doc.getUrl());
                        if((!allSnippets.containsKey(snippetDoc))||allSnippets.get(snippetDoc)<score)
                            {
                                allSnippets.put(snippetDoc,score);
                            }

                }


            }

        }
        Set<Map.Entry<Document,Double>>entries= allSnippets.entrySet();
        List<Document> topkDocuemts = entries.stream().filter(e -> e.getValue() >= SIM_THRESHOLD).sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(numberOfResults).map(e -> e.getKey())
                .collect(Collectors.toList());
        for (int i=0;i<topkDocuemts.size();i++){
            topkDocuemts.get(i).setOrder(i+1);
        }
        return topkDocuemts;



//        return  docs.stream().sorted().limit(numberOfResults).collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {

        BingRetriever retriever=new BingRetriever(3,"./cache2.tmp");

        System.out.println(retriever.searchParaphrases(Arrays.asList("Jennifer was casted in Mother","Lawrence acted in Mother")));
        System.out.println(retriever.search("Obama was born in Kenya"));
        System.out.println(retriever.searchParaphrases(Arrays.asList("Obama was born in Kenya","Obama birthplace is Kenya")));



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
