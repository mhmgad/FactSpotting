package de.mpii.factspotting.text;

import de.mpii.datastructures.Document;
import de.mpii.factspotting.ISpottedEvidence;

import java.util.Collection;

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
    public String toString() {
        return "TextEvidence{" +
                "documents=" + documents+
                ", isSupporting=" + isSupporting()+
                '}';
    }
}
