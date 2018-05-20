package de.mpii.factspotting.text;

import com.carrotsearch.hppc.ObjectDeque;
import com.google.common.base.Joiner;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.Fact;
import de.mpii.de.mpii.processing.NEExtractor;
import de.mpii.factspotting.ISpottedEvidence;
import org.apache.commons.lang.StringEscapeUtils;
import org.elasticsearch.common.inject.internal.Join;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/16/17.
 */
public class TextEvidence implements ISpottedEvidence{


    List<Document> documents;
    Fact query;

    public TextEvidence(Fact query, List<Document> documents) {
        this(documents);
        this.query=query;
    }

    public TextEvidence() {
        documents=new LinkedList<>();
    }

    public TextEvidence(List<Document> documents) {
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
    public List<Document> getDocuments() {
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


    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public String toTriple() {
        return null;
    }

    @Override
    public String toTsv() {
        return null;
    }


    public static String getHeader(){
        return Joiner.on(",").join(Arrays.asList(new String[]{"query","result_size","documents"}));
    }

    @Override
    public String toCsv() {
        StringBuilder sb=new StringBuilder();
        List<Document> outdoc=documents.stream().limit(5).collect(Collectors.toList());
        String queryText=StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(((BinaryFact) query).toReadableString()));

        for (Document d: outdoc){
            String id=(StringEscapeUtils.escapeHtml(d.getId()));
            String url=d.getUrl();
            String title=StringEscapeUtils.escapeHtml(d.getTitle());
            String text=StringEscapeUtils.escapeHtml(d.getBriefReadableString());


            String appear="<div>\n" +
                    "<p>\n" +
                    "  <b><a href=\""+url+"\" target=\"_blank\" >"+ title+"</a></b>\n" +
                    "  <br/>\n" +
                    "  <small><a href=\""+url+"\" target=\"_blank\">"+ url+"</a></small>\n" +
                    "  <br/>\n" +
                    text+"\n" +
                    "  </p>\n" +
                    "</div>\n";

            sb.append(appear);
        }
        return queryText+","+outdoc.size()+"," +StringEscapeUtils.escapeCsv(sb.toString());

//        return Joiner.on("\n").join(documents.stream().map(Document::toCsv).collect(Collectors.toList()));
    }
}
