package de.mpii.sticsAnalysis;

import org.json.simple.JSONObject;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mention {

    String mentionText;
    long offset;
    String entityId;
    long length;

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
        this.offset = offset;
        this.entityId = entityId;
        this.length =length;
    }

    public Mention() {
        this(null,0,0,null);
    }


    public static Mention fromJSON(JSONObject mentionJSON) {

        long length=(Long) mentionJSON.get("length");
        long offset=(Long) mentionJSON.get("offset");
        String mentionText= (String) mentionJSON.get("name");

        JSONObject bestEntity= (JSONObject)mentionJSON.get("bestEntity");
        String entityId= (bestEntity==null)? null : fixEntityId((String) bestEntity.get("kbIdentifier"));


      return new Mention( mentionText,  offset,  length,  entityId);
    }

    private static String fixEntityId(String kbIdentifier) {
        return kbIdentifier.replaceFirst("YAGO3:","");

    }

    @Override
    public String toString() {
        return "Mention{" +
                "mentionText='" + mentionText + '\'' +
                ", offset=" + offset +
                ", entityId='" + entityId + '\'' +
                ", length=" + length +
                '}';
    }

    public String getEntityId() {
        return entityId;
    }

    public boolean hasEntity() {
        return entityId!=null;
    }

    public long getPosition() {
        return offset;
    }
}
