package de.mpii.factspotting.text;

import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import de.mpii.factspotting.utils.DataUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/17/17.
 */
public class LexcialDictionary<T extends IParaphrase> implements IDictionary<T> {


//    private static LexcialDictionary instance;

    SortedSetMultimap<String,T> predicate2patterns;


    private LexcialDictionary() {
        TreeMultimap.create();
    }

    private LexcialDictionary(Multimap<String, T> predicate2patterns) {
        this();
        this.predicate2patterns.putAll(predicate2patterns);
    }

    @Override
    public Collection<T> getParaphrases(String key) {
        return predicate2patterns.get(key);
    }

    @Override
    public Collection<T> getParaphrases(String key, int topk) {
        return predicate2patterns.get(key).stream().limit(topk).collect(Collectors.toList());
    }

//    public synchronized static LexcialDictionary getInstance(){
//
//        if(instance==null)
//            instance=new LexcialDictionary();
//
//        return instance;
//    }


    public static LexcialDictionary fromFiles(List<String> predicatesDictionariesFiles) {

        return new LexcialDictionary(DataUtils.loadDictionaries(predicatesDictionariesFiles));

    }
}
