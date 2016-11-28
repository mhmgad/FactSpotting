package de.mpii.containers;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * Created by gadelrab on 11/21/16.
 */
public class Sentence {


    //TODO Should be replaced to work independently from coreNlp
    CoreMap sentence;

    Mentions mentions;


    public static Comparator<? super Sentence> charOffsetCompartor =new Comparator<Sentence>() {
        @Override
        public int compare(Sentence o1, Sentence o2) {
            return Long.compare(o1.getStartCharIndex(),o2.getEndCharIndex());
        }
    };


    public CoreMap getSentence() {
        return sentence;
    }

    public void setSentence(CoreMap sentence) {
        this.sentence = sentence;
    }

    public Mentions getMentions() {
        return mentions;
    }

    public void setMentions(Mentions mentions) {
        this.mentions = mentions;
    }

    public long getStartCharIndex() {
        return startCharIndex;
    }

    public void setStartCharIndex(long startCharIndex) {
        this.startCharIndex = startCharIndex;
    }

    public long getEndCharIndex() {
        return endCharIndex;
    }

    public void setEndCharIndex(long endCharIndex) {
        this.endCharIndex = endCharIndex;
    }

    long startCharIndex;
    long endCharIndex;

    public Sentence(CoreMap sentence, Mentions mentions) {
        this.sentence = sentence;
        this.mentions = mentions;
        List<CoreLabel> tokens = this.getTokens();
        this.startCharIndex = tokens.get(0).beginPosition();
        this.endCharIndex = tokens.get(tokens.size() - 1).endPosition();
    }



    public Sentence(CoreMap sentence) {
        this(sentence,new Mentions());
    }

    public void addMention(Mention mention) {
        mentions.add(mention);

    }

    public List<CoreLabel> getTokens() {
        return sentence.get(CoreAnnotations.TokensAnnotation.class);
    }


    @Override
    public boolean equals(Object obj) {
        Sentence other=(Sentence) obj;
        return this.startCharIndex==other.startCharIndex&&this.endCharIndex==other.endCharIndex&& this.sentence.equals(other.sentence);
    }

    public boolean offsetWithinBoundries(long charOffset) {
        return charOffset>=this.startCharIndex&& charOffset<this.endCharIndex;
    }

    public void printMentions(){
        mentions.stream().forEach(m-> System.out.println(m));
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "sentence=" + sentence +
                "\n, startCharIndex=" + startCharIndex +
                "\n, endCharIndex=" + endCharIndex +
                '}';
    }



}
