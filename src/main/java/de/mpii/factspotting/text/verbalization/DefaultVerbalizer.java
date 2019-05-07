package de.mpii.factspotting.text.verbalization;

import de.mpii.datastructures.IFact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gadelrab on 3/20/17.
 */
public class DefaultVerbalizer implements IFactVerbalizer {


    @Override
    public List<IParaphrase> getVerbalizations(IFact fact) {

        List<IParaphrase> verbs=new ArrayList<>();
        verbs.add(new TextParaphrase(fact.toSearchableString(),1));

        return verbs;
    }
}
