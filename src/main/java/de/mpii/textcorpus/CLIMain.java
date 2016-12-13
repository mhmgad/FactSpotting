package de.mpii.textcorpus;

import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.AnnotatedDocuments;
import de.mpii.containers.Entity;
import de.mpii.containers.Sentence;
import de.mpii.de.mpii.processing.SentenceExtractor;
import de.mpii.de.mpii.processing.entitydisambiguation.AmbiverseDocumentAnnotator;
import de.mpii.de.mpii.processing.entitydisambiguation.DocumentAnnotator;
import eleasticsearch.EleasticSearchRetriever;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 9/7/16.
 */
public class CLIMain {

//    public static void main(String[] args) throws IOException {
//
//        SticsDocumentsParser parser=new SticsDocumentsParser();
//        // load document
//        AnnotatedDocuments annDocs = parser.documentsFromJSON(args[0]/*"Amy_Adams_Academy_Awards.json"*/);
//
//        // writing to file?
//        boolean fileOutput = args.length > 1 && args[1].equals("-f");
//        String prefix = ".";
//        if (fileOutput && args.length > 2)
//            prefix = args[2];
//
//
//        System.out.println(annDocs.size());
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        System.out.print("Entities list should be in format <entity1_id>, <entity2_id>, <entity3_id> ");
//        while (true) {
//            try {
//                System.out.print("Enter Entities: ");
//                String s = br.readLine();
//
//                // exit
//                if (s.equals("q")) {
//                    System.out.println("Exit");
//                    System.exit(0);
//                }
//
//                String line = s.replaceAll(" ", "");
//                String items[] = line.split(">,<");
//                Entity[] entities=new Entity[items.length];
//                if (items.length > 1) {
//                    items[0] = items[0] + ">";
//                    items[items.length - 1] = "<" + items[items.length - 1];
//
//                    for (int i = 1; i < items.length - 1; i++) {
//                        entities[i] = new Entity("<" + items[i] + ">");
//                    }
//                }
//
//                System.out.println(Arrays.toString(items));
//                // get documents with entities
//                Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(entities/*, "<Academy_Awards>","<France>"*/);
//                System.out.println(filteredDocs.size());
//
//                Set<Sentence> allSentences = new HashSet<>();
//
//
//                BufferedWriter bw = null;
//                String outputFilePath = prefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + ".txt";
//
//                // initialize writer
//                if (fileOutput) {
//                    bw = FileUtils.getBufferedUTF8Writer(outputFilePath);
//                }
//
//                for (String item : items) {
//
//                    // collect sentences
//                    List<Sentence> sentences = filteredDocs.stream().map(d -> d.getSentencesWith(entities)).flatMap(l -> l.stream()).collect(Collectors.toList());
//                    allSentences.addAll(sentences);
//
//                    if (fileOutput) {
//                        for (Sentence sen : sentences) {
//                            bw.write(sen.getSentence().toString());
//                            bw.newLine();
//                        }
//                        // for new document
//                        bw.newLine();
//                    }
//                }
//
//
//                String stats = "Documents: " + filteredDocs.size() + "\tSentences: " + allSentences.size();
//                if (fileOutput) {
//                    bw.write(stats);
//                    bw.newLine();
//                    bw.close();
//                    System.out.println("Written to File " + outputFilePath);
//                }
//                System.out.println(stats);
//
//
//            } catch (Exception e) {
//                System.out.println("Exception: " + e.getMessage());
//            }
//        }
//
//
//    }



    public static void main(String[] args) throws IOException {

        SticsDocumentsParser parser=new SticsDocumentsParser();

        String sourceName= args[0];
        boolean fromES=false;
        EleasticSearchRetriever retriever=null;


        // load document
        AnnotatedDocuments annDocs=new AnnotatedDocuments();

        if(sourceName.startsWith("ES_")) {
            sourceName = sourceName.substring(3);
            fromES=true;
            retriever=new EleasticSearchRetriever(sourceName);

        }else
        {
            annDocs =parser.documentsFromJSON(sourceName/*"Amy_Adams_Academy_Awards.json"*/);
            System.out.println(annDocs.size());
        }



        // writing to file?
        boolean fileOutput = args.length > 1 && args[1].equals("-f");
        String prefix = ".";
        if (fileOutput && args.length > 2)
            prefix = args[2];




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
                Arrays.stream(items).map(i->new Entity(Entity.toProperId(i))).collect(Collectors.toList()).toArray(entities);

//                if (items.length > 1) {
//                    items[0] = items[0] + ">";
//                    items[items.length - 1] = "<" + items[items.length - 1];
//
//                    for (int i = 1; i < items.length - 1; i++) {
//                        entities[i] = new Entity(Entity.toProperId(items[i] ));
//                    }

//                }

                System.out.println(Arrays.toString(items)+"\n"+Arrays.toString(entities));

                if(fromES){
                    // list of the items without the '_' in the name
                    String[] itemsMod=new String[entities.length];
                    Arrays.stream(entities).map(e->e.getIdAsTitle()).collect(Collectors.toList()).toArray(itemsMod);
                    List<AnnotatedDocument> docsList=retriever.getDocuments(itemsMod);

                    DocumentAnnotator documentAnnotator = AmbiverseDocumentAnnotator.getInstance();
                    final AnnotatedDocuments annDocsWrap=annDocs;
                    docsList.stream().forEach(d->{
                        try {
//                            d.setSentences(SentenceExtractor.getSentences(d));
                            d.setMentions(documentAnnotator.annotate(d));
                            annDocsWrap.add(d);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                // get documents with entities
                Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(entities/*, "<Academy_Awards>","<France>"*/);

                System.out.print("With coref (t|f)?: ");
                String withCorefS = br.readLine();

                boolean withCoref=(withCorefS.startsWith("t"));



                BufferedWriter bw = null;
                String outputFilePath = prefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + ".txt";
                String outputDocumentsFilePath = prefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + "_docs.json";


                // initialize writer
                if (fileOutput) {
                    bw = FileUtils.getBufferedUTF8Writer(outputFilePath);
                    annDocs.dumpJSON(outputDocumentsFilePath);
                }


                Set<Sentence> allSentences=annDocs.getAllSentencesWithOneOf(filteredDocs,withCoref,entities);

                if (fileOutput) {
                    for (Sentence sen : allSentences) {
                        bw.write(sen.getSentence().toString());
                        bw.newLine();
                    }
                    // for new document
                    bw.newLine();
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
                e.printStackTrace();
//                System.out.println("Exception: " + e.getMessage());
            }
        }


    }
}
