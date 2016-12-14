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

    EleasticSearchRetriever retriever=null;
    SticsDocumentsParser parser=new SticsDocumentsParser();
    AnnotatedDocuments annDocs=new AnnotatedDocuments();



    public static void main(String[] args) throws IOException {

        CLIMain engine=new CLIMain();


        String sourceName= args[0];
        boolean fromES=false;



        // load document


        if(sourceName.startsWith("ES_")) {
            sourceName = sourceName.substring(3);
            fromES=true;
            engine.retriever=new EleasticSearchRetriever(sourceName);

        }else
        {
            engine.annDocs =engine.parser.documentsFromJSON(sourceName/*"Amy_Adams_Academy_Awards.json"*/);
            System.out.println(engine.annDocs.size());
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
                System.out.print("With coref (t|f)?: ");
                String withCorefS = br.readLine();

                boolean withCoref=(withCorefS.startsWith("t"));

                engine.process(fromES, fileOutput, prefix, withCoref, s);


            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println("Exception: " + e.getMessage());
            }
        }


    }

    private  void process(boolean fromES, boolean fileOutput, String outputPrefix, boolean withCoref,String query) throws Exception {
        String line = query.replaceAll(" ", "");
        String items[] = line.split(">,<");
        Entity[] entities=new Entity[items.length];
        Arrays.stream(items).map(i->new Entity(Entity.toProperId(i))).collect(Collectors.toList()).toArray(entities);


        System.out.println(Arrays.toString(items)+"\n"+Arrays.toString(entities)+"\twithCoref:"+withCoref);

        if(fromES){
            // list of the items without the '_' in the name
            String[] itemsMod=new String[entities.length];
            Arrays.stream(entities).map(Entity::getIdAsTitle).collect(Collectors.toList()).toArray(itemsMod);
            List<AnnotatedDocument> docsList=retriever.getDocuments(10,itemsMod);
            annDocs=new AnnotatedDocuments();
            DocumentAnnotator documentAnnotator = AmbiverseDocumentAnnotator.getInstance();
            final AnnotatedDocuments annDocsWrap=annDocs;
            docsList.forEach(d -> {
                try {
                    System.out.println(d.getTitle());
                    d.setSentences(SentenceExtractor.getSentences(d));
                    d.setMentions(documentAnnotator.annotate(d));
                    annDocsWrap.add(d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // get documents with entities
        Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(entities/*, "<Academy_Awards>","<France>"*/);




        BufferedWriter bw = null;
        String outputFilePath = outputPrefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + ".txt";
        String outputDocumentsFilePath = outputPrefix + File.separator + line.substring(1, line.length() - 1).replaceAll(">,<", "_") + "_docs.json";


        // initialize writer
        if (fileOutput) {
            bw = FileUtils.getBufferedUTF8Writer(outputFilePath);
            AnnotatedDocuments filteredAnnDocs=new AnnotatedDocuments(filteredDocs);
            filteredAnnDocs.dumpJSON(outputDocumentsFilePath);
        }


        Set<Sentence> allSentences=annDocs.getAllSentencesWithOneOf(filteredDocs,withCoref,entities);

        if (fileOutput) {
            for (Sentence sen : allSentences) {
                bw.write(sen.toStringWithDetails(entities));
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

    }
}
