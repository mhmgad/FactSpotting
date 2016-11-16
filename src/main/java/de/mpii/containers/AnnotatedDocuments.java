package de.mpii.containers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by gadelrab on 9/5/16.
 */
public class AnnotatedDocuments {


    ArrayList<AnnotatedDocument> docs;

    SetMultimap<Entity,AnnotatedDocument> entity2doc;

    public AnnotatedDocuments() {
        this.docs = new ArrayList<>();
        this.entity2doc= HashMultimap.create();
    }



    public void add(AnnotatedDocument annotatedDocument) {
        docs.add(annotatedDocument);
        annotatedDocument.getEntities().forEach(entity-> entity2doc.put(entity,annotatedDocument));
    }

    @Override
    public String toString() {
        return "AnnotatedDocuments{" +
                "docs=" + docs +
                '}';
    }


    public int size() {
        return docs.size();

    }

    public Set<AnnotatedDocument> getDocsWith(Entity... entity) {
//       Set<AnnotatedDocument> output=Stream.of(entity).map(e->entity2doc.get(e)).reduce((a,b)->Sets.intersection(a,b));

        Set<AnnotatedDocument> output=entity2doc.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output=Sets.intersection(output,entity2doc.get(entity[i]));
        }
        return output;
    }

}
