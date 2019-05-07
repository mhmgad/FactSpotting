package de.mpii.factspotting;

import de.mpii.datastructures.IFact;
import de.mpii.factspotting.text.TextEvidence;

/**
 * Created by gadelrab on 3/22/17.
 */
public class DefualtFactSpotter implements IFactSpotter<IFact> {


    @Override
    public ISpottedEvidence spot(IFact fact) {
        return null;
    }
}
