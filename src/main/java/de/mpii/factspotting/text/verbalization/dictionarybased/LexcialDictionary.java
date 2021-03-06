package de.mpii.factspotting.text.verbalization.dictionarybased;

import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.TextParaphrase;
import de.mpii.factspotting.utils.DataUtils;
import de.mpii.dataprocessing.util.FactUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/17/17.
 */
public class  LexcialDictionary implements IDictionary<TextParaphrase> {



    private  int topK=10;
    /**
     * predicate/mention to paraphrases map .. weak implmentation it should be generic but it is hard to populate if it is  generic
     */
    private SetMultimap<String,TextParaphrase> predicate2patterns;


    private LexcialDictionary() {


        this.predicate2patterns=TreeMultimap.create();
        this.topK= Configuration.getInstance().getPerItemParaphrases();

    }


    private LexcialDictionary(Multimap<String, TextParaphrase> predicate2patterns,int topK) {
        this(predicate2patterns);
        this.topK=topK;
    }

    private LexcialDictionary(Multimap<String,TextParaphrase> predicate2patterns) {
        this();
        this.predicate2patterns.putAll(predicate2patterns);
    }

    public LexcialDictionary(List<String> predicatesDictionariesFiles) {
        this();
        predicate2patterns= DataUtils.loadDictionaries(predicatesDictionariesFiles);

    }



//    private LexcialDictionary(Multimap<String, String> predicate2patterns) {
//        this();
//        this.predicate2patterns.putAll(predicate2patterns);
//    }

    @Override
    public Collection<TextParaphrase> getParaphrases(String key) {
        return getParaphrases(key,this.topK);
    }

    @Override
    public Collection<TextParaphrase> getParaphrases(String key, int topk) {
        List<TextParaphrase> paraphs = predicate2patterns.get(key).stream().limit(topk).collect(Collectors.toList());
        String cleanName= FactUtils.getCleanName(key);
        paraphs.add(new TextParaphrase(cleanName,0));
        return paraphs ;
    }




    public static LexcialDictionary fromFiles(List<String> predicatesDictionariesFiles) {

        return new LexcialDictionary(predicatesDictionariesFiles);

    }

    public static void main(String[] args) {
        LexcialDictionary d=new LexcialDictionary();
//        System.out.println(d.getCleanName("AAA"));
//        System.out.println(d.getCleanName("AlllABCooN"));
//        System.out.println(d.getCleanName("<de/kill_A._W>"));
//        System.out.println(d.getCleanName("<eng/kill_A._W>"));

    }


}
