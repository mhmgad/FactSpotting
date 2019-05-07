package de.mpii.datastructures;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Sets;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 11/21/16.
 */
public class Sentence {


    private static Comparator<? super CoreLabel> tokensPotion= Comparator.comparing(CoreLabel::beginPosition);
    private final String docId;
    private int number;
    //TODO Should be replaced to work independently from coreNlp
    CoreMap sentence;

    Mentions mentions;


    public static Comparator<? super Sentence> charOffsetCompartor = Comparator.comparing(Sentence::getStartCharIndex);





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

    public Sentence(String docId,int number,CoreMap sentence, Mentions mentions) {
        this.sentence = sentence;
        this.mentions = mentions;
        this.number=number;
        this.docId=docId;
        List<CoreLabel> tokens = this.getTokens();
        this.startCharIndex = tokens.get(0).beginPosition();
        this.endCharIndex = tokens.get(tokens.size() - 1).endPosition();
    }



    public Sentence(String docId,int number, CoreMap sentence) {
        this(docId, number,sentence,new Mentions());
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


    public String toStringWithAnnotations(Entity... entity) {

        // It assumes non intersecting mentions

        List<Mention> sortedMentions = mentions.getMentionsSorted(entity);
        List<CoreLabel> tokens = getTokens();
        long[] mentionsStarts = sortedMentions.stream().mapToLong(Mention::getCharOffset).toArray();

        if (sortedMentions.size() == 0)
            return sentence.toString();
        List<String> output = new ArrayList<>();

        Iterator<CoreLabel> tokenIterator = tokens.iterator();

        CoreLabel currentToken;
        long end = -1;
        while (tokenIterator.hasNext()) {
            currentToken = tokenIterator.next();


            if (end != -1) { // we are still inside some mention
                if (currentToken.beginPosition() > end) {

                    output.add("]]");
                    end = -1;
                }
            } else {

                int index = Arrays.binarySearch(mentionsStarts, ((long) currentToken.beginPosition()));


                if (index >= 0) // matches a start of
                {
                    Mention currentMention = sortedMentions.get(index);
                    output.add("[[");
                    output.add(currentMention.getEntity().getId());
                    output.add("|");

                    end = currentMention.getEndChar();
                }
            }

            output.add(currentToken.originalText());



        }

        if (end > -1) {
            output.add("]]");
        }


        return Joiner.on(" ").join(output);
    }


    @Override
    public String toString() {
        return "Sentence{" +
                "number=" + number +
                ", sentence=" + sentence +
                ", mentions=" + mentions +
                ", startCharIndex=" + startCharIndex +
                ", endCharIndex=" + endCharIndex +
                '}';
    }

    /**
     *
     * @param start included 0 based
     * @param end excluded
     * @return
     */

    public String getSubText(int start,int end){
        List<CoreLabel> tokens=getTokens().subList(start, end);
        //return edu.stanford.nlp.simple.Sentence.listToOriginalTextString(tokens).trim();
        return Joiner.on(' ').join(tokens.stream().map(CoreLabel::originalText).collect(Collectors.toList())).trim();

    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("number",number);
        jsonObject.put("sentence",sentence.toString());
        jsonObject.put("startCharIndex",startCharIndex);
        jsonObject.put("endCharIndex",endCharIndex);
        jsonObject.put("mentions", mentions.toJSON());
        return jsonObject;
    }


    public int getNumber() {
        return number;
    }

    public int matchingEntities(Entity ... entities) {
        return matchingEntities(new HashSet<>(Arrays.asList(entities)));
    }

    public int matchingEntities(Set<Entity> entities) {
        return Sets.intersection(getEntities(),entities).size();
    }

    public Set<Entity> getEntities() {
        return mentions.getEntities();
    }

    public int matchingMentions(Entity ... entities) {
        return mentions.getMentionsSorted(entities).size();
    }

    public String getDocId() {
        return docId;
    }

    public String toStringWithDetails(Entity ... entities){
        String sentenceWithMentions=toStringWithAnnotations(entities);
        return "D:"+getDocId()+" ,S:"+getNumber()+" (M:"+matchingMentions(entities)+"/E:"+matchingEntities(entities)+")\t->\t"+sentenceWithMentions;
    }

}
