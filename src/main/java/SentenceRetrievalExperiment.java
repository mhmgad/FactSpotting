
import de.mpii.datastructures.*;
import de.mpii.de.mpii.processing.SentenceExtractor;
import de.mpii.de.mpii.processing.entitydisambiguation.AmbiverseDocumentAnnotator;
import de.mpii.de.mpii.processing.entitydisambiguation.DocumentAnnotator;
import de.mpii.factspotting.text.retrievers.EleasticSearchRetriever;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by gadelrab on 1/3/17.
 */
public class SentenceRetrievalExperiment {

//    private Options helpOptions;
    private DefaultParser parser;
    private Options options;


    private Option helpOp;
    private Option inputFileOp;
    private Option esResultSizeOp;
    private Option inputRelationsFileOp;
    private Option outputFileOp;
    private Option relationOp;
    private Option bothOp;

    public enum PageMode {SUBJECT_ONLY,OBJECT_ONLY,BOTH,AUTO};


    public SentenceRetrievalExperiment() {
        options= new Options();
//        helpOptions = new Options();
        parser = new DefaultParser();


    }


    public void defineOptions(){

        //help option
        helpOp =new Option("h",false,"Show Help");
        helpOp.setLongOpt("help");
        options.addOption(helpOp);

        //input file
        inputFileOp=Option.builder("i").longOpt("input").hasArg().desc("Input file inform of RDF").argName("file").required().build();
        options.addOption(inputFileOp);

        //output
        outputFileOp=Option.builder("o").longOpt("output").hasArg().desc("Output file, where sentences are saved").argName("file").required().build();
        options.addOption(outputFileOp);

        //Eleasticsearch result size
        esResultSizeOp= Option.builder("es").hasArg().desc("Number of retrieved documents from ES, defualt 5").argName("es-size").build();
        options.addOption(esResultSizeOp);


        // relations paraphrases input file
        inputRelationsFileOp=Option.builder("rp").longOpt("rel-paraphrase-input").hasArg().desc("Input file of relation and its paraphrases").argName("file").build();
        options.addOption(inputRelationsFileOp);

        // relations paraphrases input file
        relationOp=Option.builder("r").longOpt("relation").hasArg().desc("Target Relation/Predicate. ").argName("relationName").required().build();
        options.addOption(relationOp);


        bothOp = Option.builder("c").longOpt("check-pages").hasArg().desc("Determine the pages to be checked <S:Subject | O: Object | A: automatic(Not implemented)>").argName("relationName").required().build();
        options.addOption(bothOp);

    }



    public void run(CommandLine cmd) throws Exception{

        int esResultSize=5;
        PageMode pageMode= PageMode.BOTH;


        if(cmd.hasOption(esResultSizeOp.getOpt())){
            esResultSize = Integer.valueOf(cmd.getOptionValue(esResultSizeOp.getOpt()));
        }


        String inputFile=cmd.getOptionValue(inputFileOp.getOpt());
        String outputFilePath=cmd.getOptionValue(outputFileOp.getOpt());

        //Relation
        String relation=cmd.getOptionValue(relationOp.getOpt());
        String relationParaphrasesFile=cmd.getOptionValue(inputRelationsFileOp.getOpt(),null);

        List<String> relationParaphrases=new ArrayList<>();
        relationParaphrases.add(relation);
        if(relationParaphrasesFile!=null) {
            relationParaphrases.addAll(FileUtils.getFileContentasList(relationParaphrasesFile));

        }


        if(cmd.hasOption(bothOp.getOpt())) {
            pageMode = getPageMode(cmd.getOptionValue(inputRelationsFileOp.getOpt(),"SO"));
        }


        process(esResultSize,pageMode,inputFile,relation,relationParaphrases,outputFilePath);



    }

    private PageMode getPageMode(String stringOptions) {

        PageMode pageMode=PageMode.BOTH;
        if(stringOptions.equalsIgnoreCase("S"))
            pageMode=PageMode.SUBJECT_ONLY;
        else  if(stringOptions.equalsIgnoreCase("O"))
            pageMode=PageMode.OBJECT_ONLY;
        return pageMode;
    }


    private CommandLine parse(String[] args) throws ParseException {
        return parser.parse(options,args);
    }


    public static void main(String[] args) throws Exception {

        SentenceRetrievalExperiment instance=new SentenceRetrievalExperiment();
        instance.defineOptions();
        instance.run(instance.parse(args));






    }

    private void process(int esResultSize, PageMode pageMode , String inputFile, String relation, List<String> relationParaphrases, String outputFilePath) throws Exception {

        EleasticSearchRetriever ret=new EleasticSearchRetriever("wiki_sent");

        BufferedReader br = FileUtils.getBufferedUTF8Reader(inputFile);

        BufferedWriter bw= FileUtils.getBufferedUTF8Writer(outputFilePath);
        DocumentAnnotator documentAnnotator = AmbiverseDocumentAnnotator.getInstance();
        int factCount=0;
        int evidencesFound=0;
        int fullEvidenceFoundCount=0;



        for (String line=br.readLine();line!=null;line=br.readLine()){
            factCount++;
            String parts[]=line.split("\t");

            List<String> pagesToCheck=new ArrayList<>();
            Set<Entity> entitiesToCheck=new HashSet<>();


            Entity entity = new Entity(parts[0]);
            entitiesToCheck.add(entity);

            Entity object = new Entity(parts[2]);
            entitiesToCheck.add(object);


            if(pageMode!=PageMode.OBJECT_ONLY) {
                pagesToCheck.add(entity.getIdAsTitle());
            }
            if(pageMode!=PageMode.SUBJECT_ONLY) {
                pagesToCheck.add(object.getIdAsTitle());
            }

            List<AnnotatedDocument> docsList=ret.getByTitle(esResultSize,pagesToCheck,relationParaphrases);


            AnnotatedDocuments annDocs = new AnnotatedDocuments();

            // To remain within ambiverse qouta
            if(factCount%20==0)
                Thread.sleep(60000);

            final AnnotatedDocuments annDocsWrap=annDocs;
            docsList.forEach(d -> {
                try {
                    System.out.println(d.getTitle());
                    d.setSentences(SentenceExtractor.getSentences(d));
                    d.setMentions(documentAnnotator.annotate(d));
//                    d.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(d.getText()));
                    annDocsWrap.add(d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            boolean fullEvidenceFound=annDocs.getDocs().stream().anyMatch(d->d.hasEntities(entitiesToCheck));

            if(fullEvidenceFound)
                fullEvidenceFoundCount++;

            if(annDocs.size()>0)
                evidencesFound++;



            System.out.println("target Entities:" + pagesToCheck);
            System.out.println("Entities in Docs: "+annDocs.getEntities());


            bw.write(line+"\t"+fullEvidenceFound);
            bw.newLine();
            bw.newLine();
            docsList.stream().sorted(Comparator.comparing(AnnotatedDocument::getOrder)).forEach(doc->
            /*for (AnnotatedDocument doc:docsList)*/ {
                for (Sentence sent: doc.getSentences()) {
                    System.out.println(sent.toStringWithDetails());


                    try {
                        bw.write(sent.toStringWithDetails());
                        bw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });

            // for new document
            bw.write("--------------------");
            bw.newLine();

        }

        bw.write("Sentences found for "+evidencesFound+" facts (fully found: "+fullEvidenceFoundCount+") out of "+ factCount);
        bw.newLine();

        bw.flush();
        bw.close();
    }

}
