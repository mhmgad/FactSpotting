package de.mpii.factspotting;

import de.mpii.datastructures.IFact;

/**
 * Created by gadelrab on 3/22/17.
 */
public class DefualtFactSpotter implements IFactSpotter {


    @Override
    public ISpottedEvidence spot(IFact fact) {
        return null;
    }
}
