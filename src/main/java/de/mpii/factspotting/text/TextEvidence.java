package de.mpii.factspotting.text;

import de.mpii.datastructures.Document;
import de.mpii.factspotting.ISpottedEvidence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gadelrab on 3/16/17.
 */
public class TextEvidence implements ISpottedEvidence{


    Collection<Document> documents;

    public TextEvidence(Collection<Document> documents) {
        this.documents = documents;
    }

    @Override
    public boolean isSupporting() {
        return documents!=null && !documents.isEmpty();
    }

    @Override
    public List<String> getEntities() {
        //TODO get entities in the txt using AIDA or stanford
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TextEvidence{" +
                "documents=" + documents+
                ", isSupporting=" + isSupporting()+
                '}';
    }
}
