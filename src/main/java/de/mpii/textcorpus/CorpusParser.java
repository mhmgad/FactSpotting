package de.mpii.textcorpus;

import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.AnnotatedDocuments;
import de.mpii.containers.Mentions;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by gadelrab on 11/28/16.
 */
public abstract class CorpusParser {



//    public AnnotatedDocuments parseCollection(String filePath) throws IOException {
//
//        System.out.println("=============== Load Annotated Documents ");
//        AnnotatedDocuments docs=new AnnotatedDocuments();
//
//        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);
//
//        for(String line=br.readLine();line!=null;line=br.readLine()){
//
//
//            AnnotatedDocument doc = parseDocument(line);
//            doc.setMentions(parseMentions(line));
//
//
//        }
//
//    return docs;
//
//    }
//
//    protected abstract Mentions parseMentions(String line);
//
//    protected abstract AnnotatedDocument parseDocument(String line) ;


}
