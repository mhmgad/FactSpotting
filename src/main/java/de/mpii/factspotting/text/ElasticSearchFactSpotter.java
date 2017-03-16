package de.mpii.factspotting.text;

import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.IFactSpotter;
import de.mpii.factspotting.ISpottedEvidence;
import eleasticsearch.EleasticSearchRetriever;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gadelrab on 3/14/17.
 */
public class ElasticSearchFactSpotter implements IFactSpotter {



    private  List<String> fieldsToSearch;
    EleasticSearchRetriever esR;


    public ElasticSearchFactSpotter(String indexName) {
        this(indexName,5);
    }

    public ElasticSearchFactSpotter(String indexName,int resultSize) {
        this(indexName,resultSize,new LinkedList<>());
    }

    public ElasticSearchFactSpotter(String indexName,int resultSize,List<String>fieldsToSearch) {
        this.esR = new EleasticSearchRetriever(indexName,resultSize);
        this.fieldsToSearch=fieldsToSearch;
    }

    @Override
    public ISpottedEvidence spot(Fact fact) {

        List<String> searchQueries=generateSearchQueries(fact);
        List<Document> docs=new LinkedList<>();
        try {
            docs=esR.searchFields(fieldsToSearch,searchQueries);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new TextEvidence(docs);
    }

    /**
     * Generate all possible verbalization (paraphrases) of the fact in order to search for it
     * @param fact
     * @return list of paraphrases for the input fact.
     */
    private List<String> generateSearchQueries(Fact fact) {
        List<String> st=new LinkedList<>();
        st.add(fact.toSearchableString());
        return st;


    }

    public void addField(String fieldName){
        fieldsToSearch.add(fieldName);
    }

    public static void main(String[] args) {
        Fact f=new BinaryFact("Einstein","wasBornIn","Ulm");
        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter("wiki_sent",5);
        fs.addField("sent");
        System.out.println(fs.spot(f));
    }


}
