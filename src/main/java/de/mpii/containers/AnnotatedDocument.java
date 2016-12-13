package de.mpii.containers;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.de.mpii.processing.SentenceExtractor;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 8/31/16.
 */
public class AnnotatedDocument {



    //String title;
    int id;
    String text;
    Mentions mentions;
    List<Sentence> sentences;
    SetMultimap<Entity, Sentence> entity2Sentences;
    private boolean corefResolved;
    private String url;


    public AnnotatedDocument(int id, String text,String url) {
//        this(text,new Mentions());
        this.text = text;
        this.id=id;
        this.url=url;
        this.mentions = new Mentions();
        this.entity2Sentences = HashMultimap.create();
    }


    public AnnotatedDocument(String text,String url) {
        this(0,text,url);

    }


    private void createEntity2SentencesMap() {


        List<Mention> sortedMentions=mentions.getInTextualOrder();

        // Assumes sentences already sorted TODO fix

        for (Sentence sentence:sentences) {
            sortedMentions.stream().filter(m-> m.in(sentence)).forEach(m->{
                linkMention2Sentence(m, sentence);
            });
        }

    }



    private void linkMention2Sentence(Mention mention, Sentence sentence) {
        // to way linking
        mention.setSentence(sentence);
        sentence.addMention(mention);
        if(mention.hasEntity())
            entity2Sentences.put(mention.getEntity(),sentence);
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

    public List<Sentence> getSentences() {
        return sentences;
    }

    public Set<Sentence> getSentencesWith(Entity ...entity) {

        if(sentences==null){
//            System.out.println("compute sentences");
            this.setSentences(SentenceExtractor.getSentences(this));
        }

        Set<Sentence> output= entity2Sentences.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output= Sets.union(output, entity2Sentences.get(entity[i]));
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

        for (Sentence sentence:sentences) {
            if(mention.in(sentence)){
                linkMention2Sentence(mention,sentence);
                break;
            }
        }


    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
        if(mentions!=null)
            this.createEntity2SentencesMap();
//        else
//            System.out.println("No mentions");

    }

    public void printEntitySentences() {

        for (Entity e:entity2Sentences.keySet()) {

            System.out.println(e);

            entity2Sentences.get(e).stream().forEach(s-> System.out.println("->\t"+s));
            System.out.println("-----------------------");
        }
    }

    public void resolveCoreferences(Collection<CorefChain> coreferenceChains) {

        if(sentences==null){
            setSentences(SentenceExtractor.getSentences(this));
        }

        List<Mention> sortedMentions=mentions.getInTextualOrder();

        for (CorefChain cc : coreferenceChains) {
            if (cc.getMentionsInTextualOrder().size()<=1)
                continue;
            //System.out.println("************************" + cc.getRepresentativeMention());

            // list to collect all candidate annotations
            TObjectIntHashMap<Entity> candidateEntities=new TObjectIntHashMap<>();

            // container for the mentions in the chain
            List<Mention> chainMentions=new ArrayList<>();

            // now try to match one of the coreferences with the disambiguated mentions

            for(CorefChain.CorefMention cm:cc.getMentionsInTextualOrder()){

//                System.out.println(cm.mentionSpan+ " ("+cm.startIndex+", "+cm.endIndex+", "+cm.position+", "+cm.headIndex+", "+cm.corefClusterID+")");
                // get the sentence
                Sentence sentence = sentences.get(cm.sentNum - 1);

                List<CoreLabel> tokens = sentence.getTokens();
                int startCharOffset= tokens.get(cm.startIndex-1).beginPosition();
                int endIndex=tokens.get(cm.endIndex-2).endPosition();
                int length= endIndex-startCharOffset;
                //TODO reconstruction is not accurate
//                String mentionText=edu.stanford.nlp.ling.Sentence.listToOriginalTextString(tokens.subList(cm.startIndex-1,cm.endIndex-1)).trim();
                String mentionText=sentence.getSubText(cm.startIndex-1,cm.endIndex-1);
//                String mentionText=Joiner.on(" ").join(tokens.subList(cm.startIndex-1,cm.endIndex-1));

//                System.out.println(startCharOffset+", "+endIndex +", ("+length+") ("+ mentionText+")");

                Mention candidateMention=new Mention(mentionText,startCharOffset,length,null,sentence,true);
                // if the candidate overlaps with other mentions just skip it.
//                if(overlapping(candidateMention,sortedMentions))
//                      continue;


                chainMentions.add(candidateMention);


                // search if any of the annotated mentions shares the same start
                int index=Collections.binarySearch(sortedMentions,candidateMention,Mention.charOffsetAndLengthCompartor);


                if(index>=0){

                    Entity entity=sortedMentions.get(index).getEntity();
                    if(entity!=null)
                        candidateEntities.adjustOrPutValue(entity,1,1);
                }
//                System.out.println(cm.sentNum+":\t("+sentence+")");
            }
            int v=candidateEntities.remove(null);
            if(v>0) System.out.println("Null is inserted");

            // if many entities are
            if(candidateEntities.size()==1){
                Entity entity=candidateEntities.keySet().iterator().next();
                chainMentions.stream().forEach(cm->{cm.setEntity(entity); linkMention2Sentence(cm,cm.getSentence());/*mentions.add(cm); cm.getSentence().addMention(cm); entity2Sentences.put(entity,cm.getSentence());*/});
            }
            else
            {
            if(candidateEntities.size()>1)
                System.out.println("Chain: "+cc.getRepresentativeMention()+"\tConflicts: "+candidateEntities.keySet());
            }
        }

        setCorefResolved(true);
    }


    public void setText(String text) {
        this.text = text;
    }

    public synchronized boolean isCorefResolved() {
        return corefResolved;
    }

    public synchronized void setCorefResolved(boolean corefResolved) {
        this.corefResolved = corefResolved;
    }

    public void printSentenceMentions() {

        sentences.forEach(s-> {
            System.out.println("*****************\n"+s+"\n");
            s.printMentions();
        });

    }

    public int sentencesWithEntities(){
//        System.out.println(entity2Sentences);
        return entity2Sentences.size();
    }

    public boolean hasEntities() {
        return mentions.hasEntities();
    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();

        JSONArray entitiesJSON=new JSONArray();
        mentions.getEntities().stream().map(Entity::toJSON).forEach(e->entitiesJSON.add(e));
//        entity2Sentences.keySet().stream().map(Entity::toJSON).forEach(e->entitiesJSON.add(e));

        JSONArray sentencesJSON=new JSONArray();
        sentences.stream().map(Sentence::toJSON).forEach(s->sentencesJSON.add(s));

        jsonObject.put("text",text);
        jsonObject.put("mentions", mentions.toJSON());
        jsonObject.put("entities", entitiesJSON);
        jsonObject.put("sentences", sentencesJSON);
        jsonObject.put("url", url);
//        jsonObject.put("sentences",sentences.)
        return jsonObject;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
