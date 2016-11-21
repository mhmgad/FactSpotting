package de.mpii.containers;


import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.de.mpii.processing.SentenceExtractor;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.Collection;
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
                    .forEach(m-> {m.setSentence(sentence);entity2Sentences.put(m.getEntity(),sentence);});
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
            this.setSentences(SentenceExtractor.getSentences(text));
        }

        Set<CoreMap> output= entity2Sentences.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output= Sets.intersection(output, entity2Sentences.get(entity[i]));
        }
        return output;
    }



    public void addMentions(Mention em) {
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
        if(sentences!=null)
            this.createEntity2SentencesMap();
    }

    public void addMention(Mention mention){
        mentions.add(mention);

        if(sentences!=null&&!sentences.isEmpty()&& mention.getEntity()!=null ){
            for (CoreMap sentence:sentences) {

                List<CoreLabel> tokens=sentence.get(CoreAnnotations.TokensAnnotation.class);

                int startIndex = tokens.get(0).beginPosition();
                int endIndex = tokens.get(tokens.size() - 1).beginPosition();

                if( mention.getPosition()>=startIndex && mention.getPosition()<=endIndex) {
                    mention.setSentence(sentence);
                    entity2Sentences.put(mention.getEntity(), sentence);
                    break;
                }
                //System.out.println(startIndex +" "+ endIndex);

            }
        }

    }

    public void setSentences(List<CoreMap> sentences) {
        this.sentences = sentences;
        if(mentions!=null)
            this.createEntity2SentencesMap();

    }

    public void printEntitySentences() {

        for (Entity e:entity2Sentences.keySet()) {

            System.out.println(e);

            entity2Sentences.get(e).stream().forEach(s-> System.out.println("->\t"+s));
            System.out.println("-----------------------");
        }
    }

    public void resolveCoreferences(Collection<CorefChain> coreferenceChains) {


        for (CorefChain cc : coreferenceChains) {
            if (cc.getMentionsInTextualOrder().size()<=1)
                continue;
            System.out.println("************************" + cc.getRepresentativeMention());
            for(CorefChain.CorefMention cm:cc.getMentionsInTextualOrder()){

                System.out.println(cm.mentionSpan+ " ("+cm.startIndex+", "+cm.endIndex+", "+cm.position+", "+cm.corefClusterID+")");
                CoreMap sentence = sentences.get(cm.sentNum - 1);
                List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                int startCharOffset= tokens.get(cm.startIndex-1).beginPosition();
                int endIndex=tokens.get(cm.endIndex-2).endPosition();
                int length= endIndex-startCharOffset;
                System.out.println(startCharOffset+", "+endIndex +", ("+length+") ("+ Joiner.on(" ").join(tokens.subList(cm.startIndex-1,cm.endIndex-1))+")");

                System.out.println(cm.sentNum+":\t("+sentence+")");


            }
        }

    }
}
