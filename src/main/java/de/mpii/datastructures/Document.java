package de.mpii.datastructures;

import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import io.searchbox.annotations.JestId;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import output.writers.SerializableData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by gadelrab on 3/14/17.
 */
public class Document implements Comparable<Document>,SerializableData {


    //String title;
    @JestId
    private String id;


    private String text;
    private String title;


    private int order;
    private String url;


    String sent;


    public String getText() {
        return (sent!=null)? sent:text;

    }

    public Document(String id, String title, String text, String url, int order) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.order = order;
        this.url = url;
    }

    public Document(String id,String title, String text,  String url) {
        this(id,    title,text,  url,0);
    }

    public String getId() {
        return id!=null? id:title;
    }

    public void setId(String id) {
        this.id = id;
    }



    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int compareTo(@NotNull Document o) {
        return Integer.compare(this.getOrder(),o.getOrder());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return Objects.equals(getId(), document.getId()) &&
                Objects.equals(getText(), document.getText()) &&
                Objects.equals(getTitle(), document.getTitle()) &&
                Objects.equals(getUrl(), document.getUrl());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getText(), getTitle(), getUrl());
    }

    public String getReadableString() {
        return "("+getTitle()+") "+getText();
    }

    /**
     *
     * @return
     */
    public String getBriefReadableString() {
        String cleanText=getText().replaceAll("\\s+"," ");

        return cleanText.substring(0,Math.min(400,cleanText.length()))+" ( from "+getTitle()+" wiki page) ";
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

    public List<String> getCsvHeader(){
        return Arrays.asList(new String[]{"id","url","title","text","order"});
    }

    @Override
    public String toCsv() {
        String id=StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(getId()));
        String url=StringEscapeUtils.escapeCsv(getUrl());
        String title=StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(getTitle()));
        String text=StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(getBriefReadableString()));


        List<Object> line = Arrays.asList(id,url,title,text, getOrder());
       return  Joiner.on(",").join(line);
    }




}
