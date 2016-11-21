package de.mpii.sticsAnalysis;

import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.AnnotatedDocuments;
import de.mpii.containers.Entity;
import de.mpii.containers.Sentence;
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

        // load document
        AnnotatedDocuments annDocs = SticsDocumentsParser.documentsFromJSON(args[0]/*"Amy_Adams_Academy_Awards.json"*/);

        // writing to file?
        boolean fileOutput = args.length > 1 && args[1].equals("-f");
        String prefix = ".";
        if (fileOutput && args.length > 2)
            prefix = args[2];


        System.out.println(annDocs.size());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Entities list should be in format <entity1_id>, <entity2_id>, <entity3_id> ");
        while (true) {
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
                Entity[] entities=new Entity[items.length];
                if (items.length > 1) {
                    items[0] = items[0] + ">";
                    items[items.length - 1] = "<" + items[items.length - 1];

                    for (int i = 1; i < items.length - 1; i++) {
                        entities[i] = new Entity("<" + items[i] + ">");
                    }
                }

                System.out.println(Arrays.toString(items));
                // get documents with entities
                Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(entities/*, "<Academy_Awards>","<France>"*/);
                System.out.println(filteredDocs.size());

                Set<Sentence> allSentences = new HashSet<>();


                BufferedWriter bw = null;
                String outputFilePath = prefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + ".txt";

                // initialize writer
                if (fileOutput) {
                    bw = FileUtils.getBufferedUTF8Writer(outputFilePath);
                }

                for (String item : items) {

                    // collect sentences
                    List<Sentence> sentences = filteredDocs.stream().map(d -> d.getSentencesWith(entities)).flatMap(l -> l.stream()).collect(Collectors.toList());
                    allSentences.addAll(sentences);

                    if (fileOutput) {
                        for (Sentence sen : sentences) {
                            bw.write(sen.getSentence().toString());
                            bw.newLine();
                        }
                        // for new document
                        bw.newLine();
                    }
                }


                String stats = "Documents: " + filteredDocs.size() + "\tSentences: " + allSentences.size();
                if (fileOutput) {
                    bw.write(stats);
                    bw.newLine();
                    bw.close();
                    System.out.println("Written to File " + outputFilePath);
                }
                System.out.println(stats);


            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }


    }
}
