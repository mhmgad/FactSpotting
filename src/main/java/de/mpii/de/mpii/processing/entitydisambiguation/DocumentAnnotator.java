package de.mpii.de.mpii.processing.entitydisambiguation;

import de.mpii.datastructures.AnnotatedDocument;
import de.mpii.datastructures.Mentions;

/**
 * Created by gadelrab on 10/27/16.
 */
public interface DocumentAnnotator {


//    protected static DocumentAnnotator documentAnnotator;




    public abstract Mentions annotate(AnnotatedDocument document) throws Exception;


//    public static DocumentAnnotator getInstance() throws Exception {
//        if(documentAnnotator==null)
//            documentAnnotator=initAnnototor();
//        return documentAnnotator;
//    }
//
//    protected  static DocumentAnnotator initAnnototor() throws Exception {
//        throw new Exception("initAnnototor is not Implemented");
//
//    }

}
