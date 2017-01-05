package de.mpii.containers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.de.mpii.processing.CoreferenceResolver;
import de.mpii.de.mpii.processing.SentenceExtractor;
import mpi.tools.javatools.util.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
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
//    private Set<Entity> entities;

    public AnnotatedDocuments() {
        this.docs = new ArrayList<>();
        this.entity2doc= HashMultimap.create();
    }

    public AnnotatedDocuments(Collection<AnnotatedDocument> docs) {
        this();
        docs.forEach(d-> this.add(d));
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


    public Set<Sentence> getAllSentencesWithOneOf(Set<AnnotatedDocument> filteredDocs,boolean withCoref , Entity ... entity ) {
        if(withCoref){
            filteredDocs.stream().filter(d->!d.isCorefResolved()).forEach(d-> d.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(d.getText())));
        }
        Set<Sentence> sentences=filteredDocs.stream().map(d-> d.getSentencesWith(entity)).flatMap(Collection::stream).collect(Collectors.toSet());
        return sentences;
    }

    public Set<Sentence> getAllSentencesWithOneOf(boolean withCoref,Entity ... entity ) {
        Set<AnnotatedDocument> filteredDocs=getDocsWith(entity);

        return getAllSentencesWithOneOf(filteredDocs,withCoref, entity);
    }


    public void findSentences(){
        docs.parallelStream().forEach(d->d.setSentences(SentenceExtractor.getSentences(d)));
    }

    public void resolveCoreferences(){
        docs.parallelStream().forEach(d->d.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(d.getText())));
    }


    public JSONArray toJSON(){
        JSONArray arr=new JSONArray();
        docs.stream().forEach(j->arr.add(j.toJSON()));
        return arr;
    }

    public void dumpJSON(String filePath) throws IOException {
        BufferedWriter br = FileUtils.getBufferedUTF8Writer(filePath);
        JSONArray arr = toJSON();
        arr.writeJSONString(br);
        br.flush();
        br.close();

    }


    public ArrayList<AnnotatedDocument> getDocs() {
        return docs;
    }

    public Set<Entity> getEntities() {
        return entity2doc.keySet();
    }
}
