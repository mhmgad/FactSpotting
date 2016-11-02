package entitydisambiguation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import java.util.Collection;
import java.util.Set;

/**
 * Created by gadelrab on 10/27/16.
 */
public class Document {


    String text;

    SetMultimap<Entity, EntityMention> entities2Mentions;


    public Document(String text) {
        this.text = text;
        this.entities2Mentions= HashMultimap.create();
    }

    public void addEntityMention(EntityMention em) {
        entities2Mentions.put(em.getEntity(), em);

    }

    public Collection<EntityMention> getEntityMentions() {
        return entities2Mentions.values();
    }


    public void printEntityMentions(){
        for (Entity k:entities2Mentions.keySet()) {
            System.out.println(k);
            for (EntityMention em:entities2Mentions.get(k)) {
                System.out.println(em);

            }
            System.out.println("****************");
        }
    }


}
