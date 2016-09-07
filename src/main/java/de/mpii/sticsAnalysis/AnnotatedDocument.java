package de.mpii.sticsAnalysis;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collection;
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
    SetMultimap<String,CoreMap> entityId2Sentence;


    public AnnotatedDocument(/*String title,*/ String text, Mentions mentions) {
       /* this.title = title;*/
        this.text = text;
        this.mentions = mentions;

        this.sentences = SentenceExtractor.getSentences(text);
        this.entityId2Sentence= HashMultimap.create();
        this.createEntityId2SentenceMap();


    }

    private void createEntityId2SentenceMap() {

        for (CoreMap sentence:sentences) {

            List<CoreLabel> tokens=sentence.get(CoreAnnotations.TokensAnnotation.class);


            int startIndex = tokens.get(0).beginPosition();
            int endIndex = tokens.get(tokens.size() - 1).beginPosition();

            mentions.stream().filter(m-> m.getEntityId()!=null && m.getPosition()>=startIndex && m.getPosition()<=endIndex)
                    .forEach(m-> entityId2Sentence.put(m.getEntityId(),sentence));
            //System.out.println(startIndex +" "+ endIndex);

        }
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

    public List<CoreMap> getSentences() {
        return sentences;
    }

    public Set<CoreMap> getSentencesWith(String ...entity) {
        Set<CoreMap> output=entityId2Sentence.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output= Sets.intersection(output,entityId2Sentence.get(entity[i]));
        }
        return output;
    }
}
