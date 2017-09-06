package de.mpii.factspotting;

import com.google.gson.*;
import de.mpii.factspotting.text.TextEvidence;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gadelrab on 3/16/17.
 */
public interface ISpottedEvidence {


    /**
     * Indicates if the spotted evidence is supporting the fact or not
     * @return
     */
    public boolean isSupporting();

    public List<String> getEntities();

    public String readable();

    int size();


}
