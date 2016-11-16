package de.mpii.containers;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.sticsAnalysis.SentenceExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    SetMultimap<Entity,CoreMap> entity2Sentences;


    public AnnotatedDocument(/*String title,*/ String text) {
//        this(text,new Mentions());
        this.text = text;
        this.mentions = new Mentions();
        this.entity2Sentences = HashMultimap.create();
    }

//    public AnnotatedDocument(/*String title,*/ String text, Mentions mentions) {
//       /* this.title = title;*/
//        this.text = text;
//        this.mentions = mentions;
//        this.entity2Sentences = HashMultimap.create();
//
//
//    }

    private void createEntity2SentencesMap() {

        for (CoreMap sentence:sentences) {

            List<CoreLabel> tokens=sentence.get(CoreAnnotations.TokensAnnotation.class);


            int startIndex = tokens.get(0).beginPosition();
            int endIndex = tokens.get(tokens.size() - 1).beginPosition();

            mentions.stream().filter(m-> m.getEntity()!=null && m.getPosition()>=startIndex && m.getPosition()<=endIndex)
                    .forEach(m-> entity2Sentences.put(m.getEntity(),sentence));
            //System.out.println(startIndex +" "+ endIndex);

        }
    }


    public Set<Entity> getEntities(){
        return mentions.getEntities();
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

    public Set<Mention> getMentionsWith(Entity entity) {
        return mentions.getMentions(entity);
    }

    public List<CoreMap> getSentences() {
        return sentences;
    }

    public Set<CoreMap> getSentencesWith(Entity ...entity) {

        if(sentences==null){
            this.addSentences(SentenceExtractor.getSentences(text));

            this.createEntity2SentencesMap();
        }

        Set<CoreMap> output= entity2Sentences.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output= Sets.intersection(output, entity2Sentences.get(entity[i]));
        }
        return output;
    }

    public void addSentences(List<CoreMap> sentences) {
        this.sentences.addAll(sentences);
    }

    public void addEntityMention(Mention em) {
        mentions.add( em);

    }

    public void printEntityMentions(){
        mentions.printEntityMentions();
    }


    public String getText() {
        return text;
    }


    public void setMentions(Mentions mentions) {
        this.mentions = mentions;
    }
}
