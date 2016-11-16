package de.mpii.containers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mentions extends ArrayList<Mention> {

    //List<Mention> mentions;
    SetMultimap<Entity,Mention> entity2Mentions;


    public Mentions() {
        this(new ArrayList<>());

    }

    public Mentions(List<Mention> mentions) {
        //this.mentions = mentions;
        this.entity2Mentions = HashMultimap.create();
        mentions.stream().filter(m-> m.hasEntity()).forEach(m-> entity2Mentions.put(m.getEntity(),m));
    }


    @Override
    public boolean add(Mention mention) {
        //mentions.add(mention);
        if(mention.hasEntity())
            entity2Mentions.put(mention.getEntity(),mention);
        return super.add(mention);
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


}
