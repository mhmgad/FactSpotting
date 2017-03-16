package de.mpii.datastructures;

import io.searchbox.annotations.JestId;

/**
 * Created by gadelrab on 3/14/17.
 */
public class Document {


    //String title;
    @JestId
    private String id;

    private String text;
    private String title;


    private int order;
    private String url;

    public Document(String id, String text, String title, String url, int order) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.order = order;
        this.url = url;
    }

    public Document(String id, String text, String title, String url) {
        this(id,  text,  title,  url,0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
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
}
