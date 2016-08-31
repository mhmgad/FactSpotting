package de.mpii.sticsAnalysis;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.hcoref.data.Mention;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


import java.io.IOException;

import java.util.List;
import java.util.Properties;

/**
 * Created by gadelrab on 8/31/16.
 */
public class SentenceExtractor {


    public static void main(String[] args) throws IOException {


           // String text="Near the beginning of his career, Einstein thought that Newtonian mechanics was no longer enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field. This led to the development of his special theory of relativity. He realized, however, that the principle of relativity could also be extended to gravitational fields, and with his subsequent theory of gravitation in 1916, he published a paper on general relativity.";
        String text="Barack Obama was born in Hawaii.  He is the president.  Obama was elected in 2008.";
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

// read some text in the text variable
           // String text = ... // Add your text here!

// create an empty Annotation just with the given text
            Annotation document = new Annotation(text);

// run all Annotators on this text
            pipeline.annotate(document);




        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            System.out.println(sentence.toString());
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                // String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                // String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                // String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
//                    System.out.println("\t"+m);
//                }
               // System.out.println(token.beginPosition());

//            }
            // this is the parse tree of the current sentence
//            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
        }
//            System.out.println("coref chains");
//            for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
//                System.out.println("\t"+cc);
//            }
//            for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//                System.out.println("---");
//                System.out.println("mentions");
//                for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
//                    System.out.println("\t"+m);
//                }
//            }

        }



}



