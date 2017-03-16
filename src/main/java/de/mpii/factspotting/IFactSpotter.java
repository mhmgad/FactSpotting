package de.mpii.factspotting;

import de.mpii.datastructures.Fact;


/**
 * Created by gadelrab on 3/14/17.
 */
public interface IFactSpotter {

    /**
     * Spotting evidence for the fact. The implementation may differ according to the target spotting context (e.g. text vs repository)
     * @param fact
     * @return An evidence object spotted in the source
     */
    public ISpottedEvidence spot(Fact fact);



}
