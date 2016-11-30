package de.mpii.containers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.de.mpii.processing.CoreferenceResolver;
import de.mpii.de.mpii.processing.SentenceExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if(annotatedDocument.hasEntities()) {
            annotatedDocument.getEntities().forEach(entity -> entity2doc.put(entity, annotatedDocument));
        }
        else
            System.out.println("No entities");
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


    public Set<Sentence> getAllSentencesWithOneOf(Set<AnnotatedDocument> filteredDocs, Entity ... entity ) {
        Set<Sentence> sentences=filteredDocs.stream().map(d-> d.getSentencesWith(entity)).flatMap(Collection::stream).collect(Collectors.toSet());
        return sentences;
    }

    public Set<Sentence> getAllSentencesWithOneOf(Entity ... entity ) {
        Set<AnnotatedDocument> filteredDocs=getDocsWith(entity);

        return getAllSentencesWithOneOf(filteredDocs,entity);
    }


    public void findSentences(){
        docs.parallelStream().forEach(d->d.setSentences(SentenceExtractor.getSentences(d.getText())));
    }

    public void resolveCoreferences(){
        docs.parallelStream().forEach(d->d.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(d.getText())));
    }
}
