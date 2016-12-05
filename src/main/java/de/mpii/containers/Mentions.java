package de.mpii.containers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mentions extends HashSet<Mention> {

    //List<Mention> mentions;
    SetMultimap<Entity,Mention> entity2Mentions;



    public Mentions() {
        this(new ArrayList<>());

    }

    public Mentions(List<Mention> mentions) {
        //this.mentions = mentions;
        this.entity2Mentions = HashMultimap.create();
        this.addAll(mentions);
       // mentions.stream().filter(m-> m.hasEntity()).forEach(m-> entity2Mentions.put(m.getEntity(),m));
    }



    @Override
    public boolean add(Mention mention) {
        //mentions.add(mention);
        boolean added=super.add(mention);
        if(added&& mention.hasEntity())
            entity2Mentions.put(mention.getEntity(),mention);
        return added;
    }

    public Set<Entity> getEntities() {
        return entity2Mentions.keySet();
    }

    public Set<Mention> getMentions(Entity entity) {
        return entity2Mentions.get(entity);
            }

    @Override
    public String toString() {
        return "Mentions{" +
                "mentions=" + super.toString() +
                '}';
    }

    public void printEntityMentions() {

        for (Entity k: entity2Mentions.keySet()) {
            System.out.println(k);
            for (Mention em: entity2Mentions.get(k)) {
                System.out.println(em);

            }
            System.out.println("****************");
        }
    }

    public List<Mention> getInTextualOrder(){
//        Collection<Mention> values=entity2Mentions.values();

    return this.stream().sorted(Mention.charOffsetAndLengthCompartor).collect(Collectors.toList());

    }


    public boolean hasEntities() {
        return entity2Mentions!=null && entity2Mentions.size()>0;
    }

    public List<Mention> getMentionsSorted(Entity ...entity) {
        Set<Mention> mentionsFiltered=new HashSet<>();

        for (Entity e:entity ) {
            mentionsFiltered.addAll(getMentions(e));
        }

        return mentionsFiltered.stream().sorted(Mention.charOffsetAndLengthCompartor).collect(Collectors.toList());

    }
    public JSONArray toJSON(){
        JSONArray mentionsJSON=new JSONArray();
        this.stream().map(Mention::toJSON).forEach(jo->mentionsJSON.add(jo) );
        return mentionsJSON;
    }

}
