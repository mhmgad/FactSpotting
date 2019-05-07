package de.mpii.datastructures;



import org.json.simple.JSONObject;

import java.util.Comparator;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mention {

    public static Comparator<? super Mention> charOffsetAndLengthCompartor = Comparator.comparing(de.mpii.datastructures.Mention::getCharOffset).thenComparing(de.mpii.datastructures.Mention::getCharLength);
    public static Comparator<? super Mention> charOffsetCompartor = Comparator.comparing(de.mpii.datastructures.Mention::getCharOffset);

    //TODO text comming from Coref may not be accurate
    String mentionText;
    long charOffset;
    Entity entity;
    long charLength;
    private double confidence;
    private boolean isCoref;

    private Sentence sentence;

     /*
      {
     "length": 14,
     "allEntities": [{"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"}],
     "bestEntity": {"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"},


     "offset": 0,
     "name": "Edward Snowden"}
     */

    public Mention(String mentionText, long offset, long length, String entityId, Sentence sentence ,boolean isCoref) {
        this.mentionText = mentionText;
        this.charOffset = offset;
        if(entityId!=null)
            this.entity = new Entity(entityId);
        this.charLength =length;
        this.isCoref=isCoref;
        this.sentence=sentence;
    }


    public Mention(String mentionText, long offset, long length, String entityId) {
       this(mentionText,  offset,  length,  entityId,null, false);
    }

    public Mention() {
        this(null,0,0,null);
    }




    @Override
    public int hashCode() {
        return Long.hashCode(charOffset)^Long.hashCode(charLength);
    }

    @Override
    public boolean equals(Object obj) {
        Mention em= (Mention) obj;
        return (em.getCharOffset()==this.getCharOffset())&&(em.getCharLength()==this.getCharLength());
    }



    public Entity getEntity() {
        return entity;
    }

    public boolean hasEntity() {
        return entity!=null;
    }

    public long getPosition() {
        return charOffset;
    }

    public long getCharOffset() {
        return charOffset;
    }

    public long getCharLength() {
        return charLength;
    }

    public String getText() {
        return mentionText;
    }


    public void setText(String text) {
        this.mentionText = text;
    }

    public void setCharOffset(Integer charOffset) {
        this.charOffset = charOffset;
    }

    public void setCharLength(Integer charLength) {
        this.charLength = charLength;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Mention{" +
                "mentionText='" + mentionText + '\'' +
                ", entity=" + entity +
                ", charOffset=" + charOffset +
                ", charLength=" + charLength +
                ", endChar=" + (charOffset+charLength) +
                ", confidence=" + confidence +
                ", isCoref=" + isCoref +
                '}';
    }


    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
        this.sentence.addMention(this);
    }

    public Sentence getSentence() {
        return sentence;
    }

    public boolean in(Sentence sentence) {
        if(this.sentence!=null)
            return this.sentence.equals(sentence);
        else

            return sentence.offsetWithinBoundries(this.charOffset);


    }

    public long getEndChar() {
        return getCharOffset()+getCharLength();
    }


    public JSONObject toJSON(){
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("mentionText",mentionText);
        jsonObj.put("entity",entity==null? null:entity.toJSON());
        jsonObj.put("charOffset",charOffset);
        jsonObj.put("charLength",charLength);
        jsonObj.put("endChar",(charOffset+charLength));
        jsonObj.put("sentence_number",sentence.getNumber());
        jsonObj.put("confidence",confidence);
        jsonObj.put("isCoref",isCoref);
        return jsonObj;
    }
}
