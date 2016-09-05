package de.mpii.sticsAnalysis;

import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by gadelrab on 9/5/16.
 */
public class AnnotatedDocuments {


    ArrayList<AnnotatedDocument> docs;

    public AnnotatedDocuments() {
        this.docs = new ArrayList<>();
    }


    public static AnnotatedDocuments fromJSONFile(String filePath) throws IOException {

        AnnotatedDocuments docs=new AnnotatedDocuments();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String line=br.readLine();line!=null;line=br.readLine()){
            docs.add(AnnotatedDocument.fromJSON(line));
        }

        return docs;


    }

    private void add(AnnotatedDocument annotatedDocument) {
        docs.add(annotatedDocument);
    }

    @Override
    public String toString() {
        return "AnnotatedDocuments{" +
                "docs=" + docs +
                '}';
    }

    public static void main(String[]args) throws IOException {

        System.out.println(AnnotatedDocuments.fromJSONFile("Amy_Adams_Academy_Awards.json"));

    }

}
