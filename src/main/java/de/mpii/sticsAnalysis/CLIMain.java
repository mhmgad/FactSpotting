package de.mpii.sticsAnalysis;

import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 9/7/16.
 */
public class CLIMain {

    public static void main(String[] args) throws IOException {

        AnnotatedDocuments annDocs = AnnotatedDocuments.fromJSONFile(args[0]/*"Amy_Adams_Academy_Awards.json"*/);

        System.out.println(annDocs.size());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("Enter Entities: ");
                String s = br.readLine();

                if (s.equals("q")) {
                    System.out.println("Exit");
                    System.exit(0);
                }

                String line = s.replaceAll(" ", "");
                String items[] = line.split(">,<");
                if (items.length > 1) {
                    items[0] = items[0] + ">";
                    items[items.length - 1] = "<" + items[items.length - 1];

                    for (int i = 1; i < items.length - 1; i++) {
                        items[i] = "<" + items[i] + ">";
                    }


                    System.out.println(Arrays.toString(items));

                    Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(items/*, "<Academy_Awards>","<France>"*/);
                    System.out.println(filteredDocs.size());
                    // filteredDocs.forEach(d-> System.out.println(d.getSentences()));
                    Set<CoreMap> allSentences = new HashSet<>();
                    for (String item : items) {
//                filteredDocs.forEach(d -> System.out.println(d.getSentencesWith(item)));
                        List<CoreMap> sentences = filteredDocs.stream().map(d -> d.getSentencesWith(item)).flatMap(l -> l.stream()).collect(Collectors.toList());
                        allSentences.addAll(sentences);
                        sentences.forEach(sen -> System.out.println("-> " + sen));
                        System.out.println("============================= " + sentences.size());
                    }

                    System.out.println("Documents: " + filteredDocs.size() + "\tSentences: " + allSentences.size());
                }
            }
            catch (Exception e){
                System.out.println("Exception: "+e.getMessage());
            }
        }


    }
}
