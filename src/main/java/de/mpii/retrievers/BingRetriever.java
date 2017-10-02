package de.mpii.retrievers;

import com.google.common.base.Joiner;
import de.mpii.datastructures.AnnotatedDocument;
import de.mpii.datastructures.Document;
import de.mpii.factspotting.config.Keys;
import it.unipi.di.acube.searchapi.WebsearchApi;
import it.unipi.di.acube.searchapi.callers.BingSearchApiCaller;
import it.unipi.di.acube.searchapi.model.WebsearchResponse;
import it.unipi.di.acube.searchapi.model.WebsearchResponseEntry;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BingRetriever {


    private final int numberOfResults;
    BingSearchApiCaller caller ;
    WebsearchApi api;

    public BingRetriever(int numberOfResults) {
        this.caller = new BingSearchApiCaller(Keys.getInstance().getBingKey());
        this.api=new WebsearchApi(caller);
        this.numberOfResults=numberOfResults;
    }


    public List<AnnotatedDocument> search(String query) throws Exception {
        WebsearchResponse response = api.query(query, numberOfResults);
        List<AnnotatedDocument> docs=new LinkedList<>();

        int order=0;

        for (WebsearchResponseEntry entry : response.getWebEntries()){
//            System.out.println(entry.getDisplayUrl());
//            System.out.println(entry.getSnippet());

            docs.add(new AnnotatedDocument(entry.getName(),entry.getName(),entry.getSnippet(),entry.getDisplayUrl(),order++));
        }
        TimeUnit.SECONDS.sleep(25);
        return docs;
    }


    public List<Document> searchParaphrases(List<String> queryParaphrases) throws Exception {
        Set<AnnotatedDocument> docs=new HashSet<>();

        System.out.println(queryParaphrases);

        docs.addAll(search(Joiner.on(" | ").join(queryParaphrases.stream().map(s->s.replace('\t',' ')).collect(Collectors.toList()))));

//        for (String query:queryParaphrases) {
//                docs.addAll(search(query.replace('\t',' ')));
//        }
//        System.out.println("docs ret"+ docs.size() );

        return  docs.stream().sorted().limit(numberOfResults).collect(Collectors.toList());
    }


}
