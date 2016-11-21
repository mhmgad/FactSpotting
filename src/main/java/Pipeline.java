import de.mpii.containers.AnnotatedDocument;
import de.mpii.de.mpii.processing.CoreferenceResolver;
import de.mpii.de.mpii.processing.SentenceExtractor;

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
        doc.setSentences(SentenceExtractor.getSentences(doc.getText()));
        doc.setMentions(documentAnnotator.annotate(doc));
        doc.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(doc.getText()));



        doc.printEntityMentions();
        doc.printEntitySentences();

    }

    public static void main(String[] args) throws Exception {

        Pipeline pipeline=new Pipeline();
        pipeline.initialize();

        String text="Albert Einstein (/ˈaɪnstaɪn/;[4] German: [ˈalbɛɐ̯t ˈaɪnʃtaɪn] ( listen); 14 March 1879 – 18 April 1955) was a German-born theoretical physicist. He developed the general theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics).[1][5]:274 Einstein's work is also known for its influence on the philosophy of science.[6][7] Einstein is best known in popular culture for his mass–energy equivalence formula E = mc2 (which has been dubbed \"the world's most famous equation\").[8] He received the 1921 Nobel Prize in Physics for his \"services to theoretical physics\", in particular his discovery of the law of the photoelectric effect, a pivotal step in the evolution of quantum theory.[9]\n" +
                "Near the beginning of his career, Einstein thought that Newtonian mechanics was no longer enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field. This led to the development of his special theory of relativity. He realized, however, that the principle of relativity could also be extended to gravitational fields, and with his subsequent theory of gravitation in 1916, he published a paper on general relativity. He continued to deal with problems of statistical mechanics and quantum theory, which led to his explanations of particle theory and the motion of molecules. He also investigated the thermal properties of light which laid the foundation of the photon theory of light. In 1917, Einstein applied the general theory of relativity to model the large-scale structure of the universe.[10][11]";

        AnnotatedDocument d=new AnnotatedDocument(text);

        pipeline.prepareDocument(d);




    }




}
