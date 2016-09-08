package de.mpii.sticsAnalysis;

import edu.stanford.nlp.util.CoreMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 9/7/16.
 */
public class CLIMain {

    public static void main(String[] args) throws IOException {

        AnnotatedDocuments annDocs = AnnotatedDocuments.fromJSONFile(args[0]/*"Amy_Adams_Academy_Awards.json"*/);

        // writing to file
        boolean fileOutput=args.length>1&& args.equals("-f");


        System.out.println(annDocs.size());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("Enter Entities: ");
                String s = br.readLine();

                // exit
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
                    BufferedWriter bw=null;
                    if(fileOutput){
                        bw=FileUtils.getBufferedUTF8Writer(line.substring(1,line.length()-1).replaceAll(">,<",""));
                    }
                    for (String item : items) {
//                filteredDocs.forEach(d -> System.out.println(d.getSentencesWith(item)));
                        List<CoreMap> sentences = filteredDocs.stream().map(d -> d.getSentencesWith(item)).flatMap(l -> l.stream()).collect(Collectors.toList());
                        allSentences.addAll(sentences);
                        sentences.forEach(sen -> System.out.println("-> " + sen));
                        System.out.println("============================= " + sentences.size());
                        if(fileOutput)
                        {
                            for (CoreMap sen:sentences) {
                                 bw.write(sen.toString());
                                bw.newLine();
                            }
                            // for new document
                            bw.newLine();
                        }
                    }


                    String stats="Documents: " + filteredDocs.size() + "\tSentences: " + allSentences.size();
                if (fileOutput){
                    bw.write(stats);
                    bw.newLine();
                    bw.close();
                }
                    System.out.println(stats);



                }
            }
            catch (Exception e){
                System.out.println("Exception: "+e.getMessage());
            }
        }


    }
}
