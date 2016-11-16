package de.mpii.containers;

import org.json.simple.JSONObject;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mention {

    String mentionText;
    long charOffset;
   Entity entity;
    long charLength;
    private double confidence;

     /*
      {
     "length": 14,
     "allEntities": [{"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"}],
     "bestEntity": {"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"},


     "offset": 0,
     "name": "Edward Snowden"}
     */

    public Mention(String mentionText, long offset, long length, String entityId) {
        this.mentionText = mentionText;
        this.charOffset = offset;
        if(entityId!=null)
            this.entity = new Entity(entityId);
        this.charLength =length;
    }

    public Mention() {
        this(null,0,0,null);
    }




    @Override
    public int hashCode() {
        return mentionText.hashCode()^Long.hashCode(charOffset)^Long.hashCode(charLength);
    }

    @Override
    public boolean equals(Object obj) {
        Mention em= (Mention) obj;
        return (em.getCharOffset()==this.getCharOffset())&&(em.getCharLength()==this.getCharLength())&&(em.getText().equals(this.getText()));
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
                ", confidence=" + confidence +
                '}';
    }
}
