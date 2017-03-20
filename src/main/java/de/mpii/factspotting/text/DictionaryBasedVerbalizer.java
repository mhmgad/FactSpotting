package de.mpii.factspotting.text;

import de.mpii.datastructures.Fact;
import de.mpii.factspotting.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/16/17.
 */
public class DictionaryBasedVerbalizer implements IFactVerbalizer<Fact> {


    IDictionary<PatternParaphrase> predicatesDictionary;
    IDictionary<TextParaphrase> argumentMentions;


    public DictionaryBasedVerbalizer(){
        this(Configuration.getInstance());
    }

    public DictionaryBasedVerbalizer(Configuration conf) {
        predicatesDictionary= LexcialDictionary.fromFiles(conf.getPredicatesDictionariesFiles());
        argumentMentions= LexcialDictionary.fromFiles(conf.getArgumentsMentionsFiles());
    }


    public DictionaryBasedVerbalizer(IDictionary<PatternParaphrase> predicatesDictionary, IDictionary<TextParaphrase> argumentMentions) {
        this.predicatesDictionary = predicatesDictionary;
        this.argumentMentions = argumentMentions;
    }

    public DictionaryBasedVerbalizer(List<String> predicatesDictionariesFilenames, List<String> argumentMentionsFilenames) {
        this(LexcialDictionary.fromFiles(predicatesDictionariesFilenames),LexcialDictionary.fromFiles(argumentMentionsFilenames));
    }




    @Override
    public List<String> getVerbalizations(Fact fact)
    {

        List<String> verbalizations=new ArrayList<>();

        // get patterns for the predicate
        Collection<PatternParaphrase> predicateParaphrases=predicatesDictionary.getParaphrases(fact.getPredicate());

        // get paraphrases of the mentions
        List<Collection<TextParaphrase>> argumentsParaphrases= fact.getArguments().stream().map(arg-> argumentMentions.getParaphrases(arg)).collect(Collectors.toList());


        //combinations
        for (PatternParaphrase predicateParaphrase: predicateParaphrases ) {
            //TODO extend support to n-tuples
            for(TextParaphrase subjectParaphrase:argumentsParaphrases.get(0)){
                for (TextParaphrase objectParaphrase:argumentsParaphrases.get(1)) {
                    verbalizations.add(predicateParaphrase.getString(subjectParaphrase,objectParaphrase ));
                }
            }
        }

        return verbalizations;
    }


}
