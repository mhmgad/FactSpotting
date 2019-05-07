package de.mpii.factspotting.text.verbalization.dictionarybased;

import com.google.common.collect.Sets;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.IFactVerbalizer;
import de.mpii.factspotting.text.verbalization.IParaphrase;
import de.mpii.factspotting.text.verbalization.TextParaphrase;
import org.apache.commons.collections4.list.TreeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/16/17.
 */
public class DictionaryBasedVerbalizer implements IFactVerbalizer<BinaryFact> {
    Logger logger= LoggerFactory.getLogger(DictionaryBasedVerbalizer.class);

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
    public List<IParaphrase> getVerbalizations(BinaryFact fact)
    {

        List<IParaphrase> verbalizations=new TreeList<>();

        // get patterns for the predicate
        Collection<TextParaphrase> predicateParaphrases=predicatesDictionary.getParaphrases(fact.getPredicate());


        logger.debug(predicateParaphrases.toString());
        // get paraphrases of the mentions
        //List<Collection<TextParaphrase>> argumentsParaphrases= fact.getArguments().stream().map(arg-> argumentMentions.getParaphrases(arg)).collect(Collectors.toList());

        ArrayList<Set<TextParaphrase>> combinationsSets = new ArrayList<>();
        combinationsSets.add(new TreeSet<>(argumentMentions.getParaphrases(fact.getSubject())));
        combinationsSets.add(new TreeSet<>(argumentMentions.getParaphrases(fact.getObject())));



        Set<List<TextParaphrase>> combinations = Sets.cartesianProduct(combinationsSets);

        for (TextParaphrase predicateParaphrase: predicateParaphrases ) {
                combinations.forEach(args-> verbalizations.add(predicateParaphrase.getParaphrase(args)));
        }


        return verbalizations;
    }





}
