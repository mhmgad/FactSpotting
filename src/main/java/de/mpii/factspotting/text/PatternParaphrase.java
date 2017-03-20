package de.mpii.factspotting.text;

/**
 * Created by gadelrab on 3/17/17.
 */
public class PatternParaphrase  extends TextParaphrase{


    /**
     * Substitutes the values of the subject and object in the predicate pattern
     * @param subjectParaphrase
     * @param objectParaphrase
     * @return currently naive implementation that returns "subject predicate object" just paraphrases with spaces in between
     */
    String getString(IParaphrase subjectParaphrase, IParaphrase objectParaphrase){
        return subjectParaphrase+" "+this.getString()+" "+objectParaphrase;
    }



}
