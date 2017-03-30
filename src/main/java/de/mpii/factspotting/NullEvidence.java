package de.mpii.factspotting;

/**
 * Created by gadelrab on 3/22/17.
 */
public class NullEvidence implements ISpottedEvidence{
    @Override
    public boolean isSupporting() {
        return false;
    }
}
