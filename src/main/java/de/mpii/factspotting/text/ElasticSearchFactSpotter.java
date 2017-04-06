package de.mpii.factspotting.text;

import com.google.common.base.Joiner;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.Fact;
import de.mpii.datastructures.IFact;
import de.mpii.factspotting.IFactSpotter;
import de.mpii.factspotting.ISpottedEvidence;
import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.IFactVerbalizer;
import de.mpii.factspotting.text.verbalization.IParaphrase;
import de.mpii.factspotting.text.verbalization.VerbalizerFactory;
import eleasticsearch.EleasticSearchRetriever;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/14/17.
 */
public class ElasticSearchFactSpotter implements IFactSpotter<Fact> {



    private  List<String> fieldsToSearch;
    EleasticSearchRetriever esR;
    IFactVerbalizer<Fact> verbalizer;


//    public ElasticSearchFactSpotter(List<String> indexName) {
//        this(indexName,5);
//    }

//    public ElasticSearchFactSpotter(String indexName,int resultSize) {
//        this(indexName,resultSize,new LinkedList<>());
//    }

    public ElasticSearchFactSpotter(){
        this(Configuration.getInstance().getTextCorpora(),Configuration.getInstance().getFieldsToSearch(),Configuration.getInstance().getEvidencePerFactSize(), Configuration.getInstance().getMatchingThreshold(),VerbalizerFactory.getInstance());
    }


    public ElasticSearchFactSpotter(List<String> indexName,List<String>fieldsToSearch,int resultSize,String matchingThreshold,IFactVerbalizer verbalizer) {
       this(Joiner.on(",").join(indexName),fieldsToSearch,resultSize,matchingThreshold,verbalizer);
    }

    public ElasticSearchFactSpotter(String indexName,List<String>fieldsToSearch,int resultSize,String matchingThreshold,IFactVerbalizer verbalizer) {
        //TODO only one index is supported now
        this.esR = new EleasticSearchRetriever(indexName,resultSize,matchingThreshold);
        this.fieldsToSearch=fieldsToSearch;
        this.verbalizer=verbalizer;
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

        List<IParaphrase> verbalizatios=verbalizer.getVerbalizations(fact);

        System.out.println(verbalizatios);

        List<String> stringsList = verbalizatios.stream().sorted().map(v -> v.getSearchableString()).collect(Collectors.toList());

        return stringsList;


    }

    public void addField(String fieldName){
        fieldsToSearch.add(fieldName);
    }

    public static void main(String[] args) {
        BinaryFact f=new BinaryFact("Albert_Einstein","wasBornIn","Ulm");
        System.out.println(Configuration.getInstance());

        String fieldsString="sent";
        List<String> fields=Arrays.asList(fieldsString.split(","));

        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter();
//        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter("wiki_sent",fields,5,VerbalizerFactory.getInstance());

        System.out.println(fs.spot(f));
    }


}
