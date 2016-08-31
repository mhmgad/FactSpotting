package de.mpii.sticsAnalysis;

/**
 * Created by gadelrab on 8/31/16.
 */
public class AnnotatedDocument {



    String title;
    String text;
    Mentions mentions;


    public AnnotatedDocument(String title, String text, Mentions mentions) {
        this.title = title;
        this.text = text;
        this.mentions = mentions;
    }



}
