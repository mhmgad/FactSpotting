
import de.mpii.containers.*;
import de.mpii.de.mpii.processing.SentenceExtractor;
import de.mpii.de.mpii.processing.entitydisambiguation.AmbiverseDocumentAnnotator;
import de.mpii.de.mpii.processing.entitydisambiguation.DocumentAnnotator;
import eleasticsearch.EleasticSearchRetriever;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 1/3/17.
 */
public class ExperimentWasBornIn {


    public static void main(String[] args) throws Exception {


//        System.out.println(FactComponent.("<Albert_Einstein>"));


        EleasticSearchRetriever ret=new EleasticSearchRetriever("wiki_sent");

        BufferedReader br = FileUtils.getBufferedUTF8Reader(args[0]);

        BufferedWriter bw= FileUtils.getBufferedUTF8Writer(args[1]);
        DocumentAnnotator documentAnnotator = AmbiverseDocumentAnnotator.getInstance();
        int factCount=0;
        int evidencesFound=0;

        for (String line=br.readLine();line!=null;line=br.readLine()){
            factCount++;
            String parts[]=line.split("\t");



            Entity entity=new Entity(parts[0]);
            String pageTile= entity.getIdAsTitle();

            List<SentAnnotatedDocument> docsList=ret.getByTitle(5,pageTile,"born in", "birthplace", "place of birth");


            AnnotatedDocuments annDocs = new AnnotatedDocuments();

            // To remain within ambiverse qouta
            if(factCount%50==0)
                Thread.sleep(30000);

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

            if(annDocs.size()>0)
                evidencesFound++;

            System.out.println(annDocs.getEntities());
            System.out.println(entity);
            Set<AnnotatedDocument> docsFiltered=annDocs.getDocsWith(entity);
            System.out.println("Filtered Docs: "+docsFiltered.size());

//            Set<Sentence> allSentences=annDocs.getAllSentencesWithOneOf(docsFiltered,false,entity);


//            for (Sentence sen : allSentences) {
//                bw.write(sen.toStringWithDetails(entity));
//                bw.newLine();
//            }
            bw.write(line);
            bw.newLine();
            bw.newLine();
            for (AnnotatedDocument doc:docsList) {
                for (Sentence sent: doc.getSentences()) {
                    System.out.println(sent.toStringWithDetails());



                    bw.write(sent.toStringWithDetails());
                    bw.newLine();

                }
            }

            // for new document
            bw.write("--------------------");
            bw.newLine();






        }

        bw.write("Sentences Found for "+evidencesFound+" facts out of "+ factCount);
        bw.flush();
        bw.close();



    }

}
