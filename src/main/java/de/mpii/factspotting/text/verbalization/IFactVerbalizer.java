package de.mpii.factspotting.text.verbalization;

import de.mpii.datastructures.IFact;

import java.util.List;

/**
 * Created by gadelrab on 3/16/17.
 */
public interface IFactVerbalizer<T extends IFact> {

    public List<IParaphrase> getVerbalizations(T fact);

}
