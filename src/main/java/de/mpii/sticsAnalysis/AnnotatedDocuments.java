package de.mpii.sticsAnalysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gadelrab on 9/5/16.
 */
public class AnnotatedDocuments {


    ArrayList<AnnotatedDocument> docs;
    SetMultimap<String,AnnotatedDocument> entity2doc;

    public AnnotatedDocuments() {
        this.docs = new ArrayList<>();
        this.entity2doc= HashMultimap.create();
    }


    public static AnnotatedDocuments fromJSONFile(String filePath) throws IOException {
        System.out.println("=============== Load Annotated Documents ");
        AnnotatedDocuments docs=new AnnotatedDocuments();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String line=br.readLine();line!=null;line=br.readLine()){
            docs.add(AnnotatedDocument.fromJSON(line));
        }

        System.out.println("=============== Done! ");
        return docs;


    }

    private void add(AnnotatedDocument annotatedDocument) {
        docs.add(annotatedDocument);
        annotatedDocument.getEntities().forEach(entity-> entity2doc.put(entity,annotatedDocument));
    }

    @Override
    public String toString() {
        return "AnnotatedDocuments{" +
                "docs=" + docs +
                '}';
    }

    public static void main(String[]args) throws IOException {

        AnnotatedDocuments annDocs = AnnotatedDocuments.fromJSONFile("Amy_Adams_Academy_Awards.json");

        System.out.println(annDocs.size());
        Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith("<Amy_Adams>", "<Academy_Awards>"/*,"<France>"*/ );
        System.out.println(filteredDocs.size());
       // filteredDocs.forEach(d-> System.out.println(d.getSentences()));
        filteredDocs.stream().map(d-> d.getSentencesWith("<Amy_Adams>" )).flatMap(s->s.stream()).forEach(s-> System.out.println("-> "+s));
        //filteredDocs.forEach(d-> System.out.println(d.getMentionsWith("<Amy_Adams>")));

    }

    public int size() {
        return docs.size();

    }

    public Set<AnnotatedDocument> getDocsWith(String... entity) {
//       Set<AnnotatedDocument> output=Stream.of(entity).map(e->entity2doc.get(e)).reduce((a,b)->Sets.intersection(a,b));

        Set<AnnotatedDocument> output=entity2doc.get(entity[0]);
        for (int i = 1; i <entity.length ; i++) {
            output=Sets.intersection(output,entity2doc.get(entity[i]));
        }
        return output;
    }

}
