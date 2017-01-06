package de.mpii.containers;

/**
 * Created by gadelrab on 1/3/17.
 */
public class SentAnnotatedDocument extends  AnnotatedDocument{


    String sent;


    @Override
    public String getText() {
        return sent;

    }

    @Override
    public String getId() {
        return title;
    }

    public void setSent(String sent) {
        this.sent = sent;
        this.text = sent;
    }
}
