package de.mpii.factspotting.text;

import com.google.common.base.Joiner;
import de.mpii.datastructures.Document;
import de.mpii.de.mpii.processing.NEExtractor;
import de.mpii.factspotting.ISpottedEvidence;
import org.elasticsearch.common.inject.internal.Join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/16/17.
 */
public class TextEvidence implements ISpottedEvidence{


    Collection<Document> documents;

    public TextEvidence() {
        documents=new LinkedList<>();
    }

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
        List<String> entities=new LinkedList<>();
        NEExtractor extractor = NEExtractor.getInstance();
        for (Document d:documents){
            entities.addAll(extractor.getEntities(d.getText()));
        }


        return entities;
    }

    @Override
    public String readable() {
        return "["+Joiner.on(",").join(documents.stream().map(d->"\""+d.getTitle()+"//"+d.getText()+"\"").collect(Collectors.toList()))+"]";
    }

    @Override
    public Collection<Document> getDocuments() {
        return documents;
    }

    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public String toString() {
        return "TextEvidence{" +
                "documents=" + documents.stream().map(d->d.getText()).collect(Collectors.toList())+
                ", isSupporting=" + isSupporting()+
                '}';
    }

    @Override
    public String getReadableString() {
        return "#"+Joiner.on("\n#").join(documents.stream().map(d->d.getReadableString()).collect(Collectors.toList()));
    }


}
