package de.mpii.factspotting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gadelrab on 3/22/17.
 */
public class NullEvidence implements ISpottedEvidence{
    @Override
    public boolean isSupporting() {
        return false;
    }

    @Override
    public List<String> getEntities() {
        return new ArrayList<>();
    }

    @Override
    public String readable() {
        return "NULL";
    }

    @Override
    public int size() {
        return 0;
    }
}
