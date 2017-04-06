package de.mpii.factspotting.text.verbalization.dictionarybased;

import com.google.common.collect.Sets;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.IFactVerbalizer;
import de.mpii.factspotting.text.verbalization.IParaphrase;
import de.mpii.factspotting.text.verbalization.TextParaphrase;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/16/17.
 */
public class DictionaryBasedVerbalizer implements IFactVerbalizer<Fact> {

    IDictionary<TextParaphrase> predicatesDictionary;
    IDictionary<TextParaphrase> argumentMentions;


    public DictionaryBasedVerbalizer(){
        this(Configuration.getInstance());
    }

    public DictionaryBasedVerbalizer(Configuration conf) {
        predicatesDictionary= LexcialDictionary.fromFiles(conf.getPredicatesDictionariesFiles());
        argumentMentions= LexcialDictionary.fromFiles(conf.getArgumentsMentionsFiles());
    }


    public DictionaryBasedVerbalizer(IDictionary<TextParaphrase> predicatesDictionary, IDictionary<TextParaphrase> argumentMentions) {
        this.predicatesDictionary = predicatesDictionary;
        this.argumentMentions = argumentMentions;
    }

    public DictionaryBasedVerbalizer(List<String> predicatesDictionariesFilenames, List<String> argumentMentionsFilenames) {
        this(LexcialDictionary.fromFiles(predicatesDictionariesFilenames),LexcialDictionary.fromFiles(argumentMentionsFilenames));
    }


    /**
     * The fact is assumed to have perdicate and arguments. This methods gets all possible patterns for the predicate and arguments mentions to create paraphrases to search.
     * Currently only Binary facts are supported
     * @param fact
     * @return
     */

    @Override
    public List<IParaphrase> getVerbalizations(Fact fact)
    {

        List<IParaphrase> verbalizations=new TreeList<>();

        // get patterns for the predicate
        Collection<TextParaphrase> predicateParaphrases=predicatesDictionary.getParaphrases(fact.getPredicate());


        System.out.println(predicateParaphrases);
        // get paraphrases of the mentions
        List<Collection<TextParaphrase>> argumentsParaphrases= fact.getArguments().stream().map(arg-> argumentMentions.getParaphrases(arg)).collect(Collectors.toList());

        argumentsParaphrases.forEach(l-> System.out.println(l));
        //combinations

        ArrayList<Set<TextParaphrase>> combinationsSets = new ArrayList<>();
        int numberOfArgs = fact.getArguments().size();

        for (int i=0;i<numberOfArgs;i++){
            combinationsSets.add(new TreeSet<>(argumentsParaphrases.get(i)));
        }

        Set<List<TextParaphrase>> combinations = Sets.cartesianProduct(combinationsSets);

        for (TextParaphrase predicateParaphrase: predicateParaphrases ) {
                combinations.forEach(args-> verbalizations.add(predicateParaphrase.getParaphrase(args)));
        }

//        for (TextParaphrase predicateParaphrase: predicateParaphrases ) {
//            //TODO extend support to n-tuples
//            for(TextParaphrase subjectParaphrase:argumentsParaphrases.get(0)){
//                for (TextParaphrase objectParaphrase:argumentsParaphrases.get(1)) {
//                    verbalizations.add(predicateParaphrase.getParaphrase(subjectParaphrase,objectParaphrase ));
//                }
//            }
//        }
        // add the fact as its to the top of the list
        //verbalizations.add(new TextParaphrase(fact.toSearchableString(),-2));

        return verbalizations;
    }





}
