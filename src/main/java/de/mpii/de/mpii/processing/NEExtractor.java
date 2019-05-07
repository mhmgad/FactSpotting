package de.mpii.de.mpii.processing;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by gadelrab on 7/31/17.
 */
public class NEExtractor {


    private  static  NEExtractor instance;

    Properties props;
    StanfordCoreNLP pipeline;

    private NEExtractor() {
        System.out.println("New NER");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        props.setProperty("ssplit.newlineIsSentenceBreak", "always");
        props.setProperty("dcoref.postprocessing","true");
        this.pipeline = new StanfordCoreNLP(props);

//        props.setProperty("annotators", "tokenize, ssplit ");//, pos, lemma, ner, parse, dcoref");

    }


    public List<String> getEntities(String text){
        Annotation document = new Annotation(text);

        LinkedList<String> nes = new LinkedList<String>();
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);


        for(CoreMap sentence: sentences) {
            Sentence sentenceObj = new Sentence(sentence);
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                nes.add("<"+word+", "+pos+", "+ne+">");
//            }
            nes.addAll(sentenceObj.mentions());


        }
        return nes;

    }

    public synchronized static NEExtractor getInstance() {

        if(instance==null)
            instance=new NEExtractor();
        return instance;
    }

    public static void main(String[] args) {

        NEExtractor neExtractor=getInstance();
        //String text="Einstein developed the theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics). Einstein's work is also known for its influence on the philosophy of science.";
        String text="Albert Einstein (/ˈaɪnstaɪn/;[4] German: [ˈalbɛɐ̯t ˈaɪnʃtaɪn] (About this sound listen); 14 March 1879 – 18 April 1955) was a German-born theoretical physicist.";
        List<String> nes = neExtractor.getEntities(text);
        System.out.println(nes);

    }
}
