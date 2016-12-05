package de.mpii.de.mpii.processing;

import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;


import java.io.IOException;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 8/31/16.
 */
public class SentenceExtractor {

    private  static  SentenceExtractor instance;

    Properties props;
    StanfordCoreNLP pipeline;

    private SentenceExtractor() {
        System.out.println("New Sentence extractor");
        this.props = new Properties();
        //tokenize,ssplit,pos,lemma,ner,parse,mention,coref
        props.setProperty("annotators", "tokenize, ssplit");
        props.setProperty("ssplit.newlineIsSentenceBreak", "always");
        this.pipeline = new StanfordCoreNLP(props);

//        props.setProperty("annotators", "tokenize, ssplit ");//, pos, lemma, ner, parse, dcoref");

    }

    public synchronized static SentenceExtractor getInstance() {

        if(instance==null)
            instance=new SentenceExtractor();
        return instance;
    }


    public static List<Sentence> getSentences(AnnotatedDocument doc){
//        System.out.println("Get sentences");

        Annotation document = new Annotation(doc.getText());
            // run all Annotators on this text
        getInstance().pipeline.annotate(document);

        List<Sentence> sentences = document.get(CoreAnnotations.SentencesAnnotation.class).stream().map(s-> new Sentence( doc.getId(),(int) s.get(CoreAnnotations.SentenceIndexAnnotation.class)/*(int) s.get(CoreAnnotations.IndexAnnotation.class)*/,s/*.asCoreMap()*/)).collect(Collectors.toList());
        sentences.sort(Sentence.charOffsetCompartor);
        return sentences;
    }



//    public static void main(String[] args) throws IOException {
//
//        System.out.println("test Extractor");
//           // String text="Near the beginning of his career, Einstein thought that Newtonian mechanics was no longer enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field. This led to the development of his special theory of relativity. He realized, however, that the principle of relativity could also be extended to gravitational fields, and with his subsequent theory of gravitation in 1916, he published a paper on general relativity.";
//        String text=" Gad test \n\n [Barack Obama] was born in [Hawaii].  He is the president.  Obama was elected in 2008.";
//
//        System.out.println("-----------------");
//        String text2=" I am a scientist, Albert said.";
//
//
//        System.out.println(getSentences(text));
//
//
//        }



}



