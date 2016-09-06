package de.mpii.sticsAnalysis;


import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 8/31/16.
 */
public class AnnotatedDocument {



    //String title;
    String text;
    Mentions mentions;
    List<CoreMap> sentences;


    public AnnotatedDocument(/*String title,*/ String text, Mentions mentions) {
       /* this.title = title;*/
        this.text = text;
        this.mentions = mentions;

        this.sentences = SentenceExtractor.getSentences(text);
    }


    public Set<String> getEntities(){
        return mentions.getEntities();
    }

    public static AnnotatedDocument fromJSON(String jsonDoc){

        JSONParser parser=new JSONParser();

        try {

            Object obj = parser.parse(jsonDoc);

            JSONObject jsonObject = (JSONObject) obj;

            //since we care only about the sentences we can skip till originaltext field in AIDA output

            JSONObject aidaOutput = (JSONObject) jsonObject.get("aida");
//            System.out.println(aidaOutput);

            String originalText = (String) aidaOutput.get("originalText");
//            System.out.println(originalText);

            // loop array
            JSONArray mentionsJSONArr = (JSONArray) aidaOutput.get("mentions");
//            System.out.println(mentionsJSONArr);

            Mentions mentions=Mentions.fromJSONArray(mentionsJSONArr);

            return new AnnotatedDocument(originalText,mentions);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public String toString() {
        return "AnnotatedDocument{" +
                "text='" + text + '\'' +
                ", mentions=" + mentions +
                ", sentences=" + sentences +
                '}';
    }

    public Mentions getMentions() {
        return mentions;
    }

    public Set<Mention> getMentionsWith(String entity) {
        return mentions.getMentions(entity);
    }
}
