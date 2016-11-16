import de.mpii.containers.AnnotatedDocument;
import de.mpii.sticsAnalysis.SentenceExtractor;

import entitydisambiguation.AmbiverseDocumentAnnotator;
import entitydisambiguation.DocumentAnnotator;

/**
 * Created by gadelrab on 11/3/16.
 */
public class Pipeline {

    DocumentAnnotator documentAnnotator;
//    SentenceExtractor sentenceExtractor;

    public void initialize() throws Exception {
        // init disambiguation
        documentAnnotator= AmbiverseDocumentAnnotator.getInstance();

//        //init NLP
//        sentenceExtractor= SentenceExtractor.getInstance();


    }

    public void prepareDocument(AnnotatedDocument doc) throws Exception {

        // Annotate
        documentAnnotator.annotate(doc);
        doc.addSentences(SentenceExtractor.getSentences(doc.getText()));






    }


}
