package de.mpii.sticsAnalysis;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mention {

    String mention;
    int offset;
    String entity;
    int length;

     /*
      {
     "length": 14,
     "allEntities": [{"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"}],
     "bestEntity": {"disambiguationScore": "1", "kbIdentifier": "YAGO:Edward_Snowden"},


     "offset": 0,
     "name": "Edward Snowden"}
     */

    public Mention(String mention, int offset, int length, String entity) {
        this.mention = mention;
        this.offset = offset;
        this.entity = entity;
        this.length =length;
    }



}
