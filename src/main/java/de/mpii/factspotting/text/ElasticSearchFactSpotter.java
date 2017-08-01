package de.mpii.factspotting.text;

import com.google.common.base.Joiner;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.Fact;
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


    private QueryStyle queryStyle;

    public enum QueryStyle {STRING_QUERY, SPLIT_QUERY};
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
        this(Configuration.getInstance().getTextCorpora(),Configuration.getInstance().getFieldsToSearch(),Configuration.getInstance().getEvidencePerFactSize(), Configuration.getInstance().getMatchingThreshold(),VerbalizerFactory.getInstance(),Configuration.getInstance().getElasticQueryStyle());
    }


    public ElasticSearchFactSpotter(List<String> indexName,List<String>fieldsToSearch,int resultSize,String matchingThreshold,IFactVerbalizer verbalizer,ElasticSearchFactSpotter.QueryStyle queryStyle) {
       this(Joiner.on(",").join(indexName),fieldsToSearch,resultSize,matchingThreshold,verbalizer,queryStyle);
    }

    public ElasticSearchFactSpotter(String indexName,List<String>fieldsToSearch,int resultSize,String matchingThreshold,IFactVerbalizer verbalizer,ElasticSearchFactSpotter.QueryStyle queryStyle) {
        //TODO only one index is supported now
        this.esR = new EleasticSearchRetriever(indexName,resultSize,matchingThreshold);
        this.fieldsToSearch=fieldsToSearch;
        this.verbalizer=verbalizer;
        this.queryStyle=queryStyle;
    }




    @Override
    public ISpottedEvidence spot(Fact fact) {

        List<String> searchQueries=generateSearchQueries(fact);
        List<Document> docs=new LinkedList<>();
        try {
            switch (queryStyle) {
                case STRING_QUERY:
                    docs = esR.searchFieldsFullSentences(fieldsToSearch, searchQueries.stream().map(q->q.replaceAll("\t"," ")).collect(Collectors.toList()));
                    break;
                case SPLIT_QUERY:
                    docs = esR.searchFieldsSeparately(fieldsToSearch, searchQueries.stream().map(q->Arrays.asList(q.split("\t"))).collect(Collectors.toList()));
                    break;
            }
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

//        System.out.println(verbalizatios);

        List<String> stringsList = verbalizatios.stream().sorted().map(v -> v.getSearchableString()).collect(Collectors.toList());

        return stringsList;


    }

    public void addField(String fieldName){
        fieldsToSearch.add(fieldName);
    }

    public static void main(String[] args) {
//        BinaryFact f=new BinaryFact("Albert_Einstein","wasBornIn","Ulm");
//        BinaryFact f=new BinaryFact("Albert_Einstein","wasBornIn","");

        Fact f=new Fact("wasBornIn",Arrays.asList(new String[]{"Albert_Einstein"}));
        System.out.println(Configuration.getInstance());

        String fieldsString="sent";
        List<String> fields=Arrays.asList(fieldsString.split(","));

        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter();
//        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter("wiki_sent",fields,5,VerbalizerFactory.getInstance());

        ISpottedEvidence ev = fs.spot(f);
        System.out.println(ev);
        System.out.println(ev.getEntities());
    }


}
