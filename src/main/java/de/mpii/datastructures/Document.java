package de.mpii.datastructures;

import com.google.gson.annotations.SerializedName;
import io.searchbox.annotations.JestId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by gadelrab on 3/14/17.
 */
public class Document implements Comparable<Document> {


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
}
