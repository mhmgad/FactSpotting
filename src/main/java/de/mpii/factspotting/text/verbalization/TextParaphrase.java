package de.mpii.factspotting.text.verbalization;

/**
 * Created by gadelrab on 3/17/17.
 */
public class TextParaphrase implements IParaphrase<TextParaphrase>{



    private String text;
    private double score;

    boolean isPattern;

    public TextParaphrase(String text, double score, boolean isPattern) {
        this.text = text;
        this.score = score;
        this.isPattern = isPattern;
    }

    public TextParaphrase(String text, double score) {
        this(text,score,false);

    }

    @Override
    public void setText(String text) {
        this.text=text;
    }

    @Override
    public String getSearchableString() {
        return text;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void setScore(double score) {
        this.score=score;
    }


    @Override
    public int compareTo(TextParaphrase o) {
        return Double.compare(score,o.score);
    }

    @Override
    public boolean equals(Object obj) {
        return text.equals(((TextParaphrase)obj).text);
    }

    @Override
    public String toString() {
        return "TextParaphrase{" +
                "text='" + text + '\'' +
                ", score=" + score +
                '}';
    }


    /**
     * Substitutes the values of the subject and object in the predicate pattern
     * @param subjectParaphrase
     * @param objectParaphrase
     * @return currently naive implementation that returns "subject predicate object" just paraphrases with spaces in between
     */
    public TextParaphrase getParaphrase(IParaphrase subjectParaphrase, IParaphrase objectParaphrase){

        TextParaphrase textParaphrase=new TextParaphrase(subjectParaphrase.getSearchableString()+" "+this.getSearchableString()+" "+objectParaphrase.getSearchableString(),this.getScore()*subjectParaphrase.getScore()*objectParaphrase.getScore());
        return textParaphrase;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = text.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isPattern ? 1 : 0);
        return result;
    }
}
