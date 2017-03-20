package de.mpii.factspotting.text;

/**
 * Created by gadelrab on 3/17/17.
 */
public class TextParaphrase implements IParaphrase<TextParaphrase>{



    private String text;
    private double score;

    @Override
    public String getString() {
        return text;
    }



    @Override
    public int compareTo(TextParaphrase o) {
        return Double.compare(score,o.score);
    }
}
