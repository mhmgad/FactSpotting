package de.mpii.factspotting.text.verbalization;

/**
 * Created by gadelrab on 3/17/17.
 */
public interface IParaphrase<T extends IParaphrase> extends Comparable<T>{



    public void setText(String text);

    public String getSearchableString();

    public double getScore();

    public void setScore(double score);


}
