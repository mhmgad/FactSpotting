package de.mpii.de.mpii.processing;


import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


import java.util.Collection;
import java.util.Properties;

/**
 * Created by gadelrab on 11/17/16.
 */
public class CoreferenceResolver {




    private  static  CoreferenceResolver instance;

    Properties props;
    StanfordCoreNLP pipeline;

    private CoreferenceResolver() {
        System.out.println("New Coreference Resolver");
        this.props = new Properties();
        //tokenize,ssplit,pos,lemma,ner,parse,mention,coref
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, dcoref");
        props.setProperty("ssplit.newlineIsSentenceBreak", "always");
        props.setProperty("dcoref.postprocessing","true");
        this.pipeline = new StanfordCoreNLP(props);

//        props.setProperty("annotators", "tokenize, ssplit ");//, pos, lemma, ner, parse, dcoref");

    }

    public synchronized static CoreferenceResolver getInstance() {

        if(instance==null)
            instance=new CoreferenceResolver();
        return instance;
    }


    public static Collection<CorefChain> getCoreferenceChains(String text){


        Annotation document = new Annotation(text);
        // run all Annotators on this text
        getInstance().pipeline.annotate(document);

//        System.out.println(document.keySet());
//        System.out.println("+++++++++++++++");


        Collection<CorefChain> chains= document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values();

//        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
//            System.out.println("\t" + cc);
//
//            System.out.println(cc.getMentionMap());
//            for(CorefChain.CorefMention cm:cc.getMentionsInTextualOrder()){
//
//
//                System.out.println(cm+" ("+cm.startIndex+", "+cm.endIndex+")");
//            }
//        }


        return chains;
    }

}
