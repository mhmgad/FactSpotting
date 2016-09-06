package de.mpii.sticsAnalysis;

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
    SetMultimap<String,Mention> entityIds2Mentions;


    public Mentions() {
        this(new ArrayList<>());

    }

    public Mentions(List<Mention> mentions) {
        //this.mentions = mentions;
        this.entityIds2Mentions= HashMultimap.create();
        mentions.stream().filter(m-> m.hasEntity()).forEach(m-> entityIds2Mentions.put(m.getEntityId(),m));
    }


    public static Mentions fromJSONArray(JSONArray mentionsJSONArr) {

        Mentions mentions=new Mentions();

        Iterator<JSONObject> iterator = mentionsJSONArr.iterator();
        while (iterator.hasNext()) {
            JSONObject current= iterator.next();
//            System.out.println(current);
            mentions.add(Mention.fromJSON(current));
        }
        return mentions;
    }

    @Override
    public String toString() {
        return "Mentions{" +
                "mentions=" + super.toString() +
                '}';
    }

    @Override
    public boolean add(Mention mention) {
        //mentions.add(mention);
        if(mention.hasEntity())
            entityIds2Mentions.put(mention.getEntityId(),mention);
        return super.add(mention);
    }

    public Set<String> getEntities() {
        return entityIds2Mentions.keySet();
    }

    public Set<Mention> getMentions(String entity) {
        return entityIds2Mentions.get(entity);
            }
}
