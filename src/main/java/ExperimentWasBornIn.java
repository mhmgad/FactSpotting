
import de.mpii.containers.*;
import de.mpii.de.mpii.processing.SentenceExtractor;
import de.mpii.de.mpii.processing.entitydisambiguation.AmbiverseDocumentAnnotator;
import de.mpii.de.mpii.processing.entitydisambiguation.DocumentAnnotator;
import eleasticsearch.EleasticSearchRetriever;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 1/3/17.
 */
public class ExperimentWasBornIn {

    private Options helpOptions;
    private DefaultParser parser;
    private Options options;


    private Option helpOp;
    private Option inputFileOp;
    private Option esResultSizeOp;
    private Option inputRelationsFileOp;
    private Option outputFileOp;
    private Option relationOp;
    private Option bothOp;

    public ExperimentWasBornIn() {
        options= new Options();
        helpOptions = new Options();
        parser = new DefaultParser();


    }


    public void defineOptions(){

        //help option
        helpOp =new Option("h",false,"Show Help");
        helpOp.setLongOpt("help");
        helpOptions.addOption(helpOp);

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


        bothOp =new Option("b",false,"Both subject and object pages");
        options.addOption(bothOp);

    }



    public void run(CommandLine cmd) throws Exception{

        int esResultSize=5;

        if(cmd.hasOption(esResultSizeOp.getOpt())){
            esResultSize = Integer.valueOf(cmd.getOptionValue(esResultSizeOp.getOpt()));
        }


        String inputFile=cmd.getOptionValue(inputFileOp.getOpt());
        String outputFilePath=cmd.getOptionValue(outputFileOp.getOpt());

        //Relation
        String relation=cmd.getOptionValue(relationOp.getOpt());
        String relationPraphrasesFile=cmd.getOptionValue(inputRelationsFileOp.getOpt(),null);

        List<String> relationParaphrases=new ArrayList<>();
        relationParaphrases.add(relation);
        if(relationPraphrasesFile!=null) {
            relationParaphrases.addAll(FileUtils.getFileContentasList(relationPraphrasesFile));

        }


        boolean bothSides = cmd.hasOption(bothOp.getOpt());


        process(esResultSize,bothSides,inputFile,relation,relationParaphrases,outputFilePath);



    }




    private CommandLine parse(String[] args) throws ParseException {
//        CommandLine cmdHelp=parser.parse(helpOptions,args);
//        if(cmdHelp.hasOption(helpOp.getOpt())){
//            HelpFormatter formatter = new HelpFormatter();
//            formatter.printHelp( "mine_rules.sh", options );
//            System.exit(0);
//        }


        return parser.parse(options,args);
    }


    public static void main(String[] args) throws Exception {

        ExperimentWasBornIn instance=new ExperimentWasBornIn();
        instance.defineOptions();
        instance.run(instance.parse(args));






    }

    private void process(int esResultSize, boolean bothSides , String inputFile, String relation, List<String> relationParaphrases, String outputFilePath) throws Exception {

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

            Entity entity=new Entity(parts[0]);


            entitiesToCheck.add(entity);
            pagesToCheck.add(entity.getIdAsTitle());

            if(bothSides) {
                Entity object = new Entity(parts[2]);
                pagesToCheck.add(object.getIdAsTitle());
                entitiesToCheck.add(object);
            }

            List<SentAnnotatedDocument> docsList=ret.getByTitle(esResultSize,pagesToCheck,relationParaphrases);


            AnnotatedDocuments annDocs = new AnnotatedDocuments();

            // To remain within ambiverse qouta
            if(factCount%50==0)
                Thread.sleep(50000);

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

            boolean fullEvidenceFound=annDocs.getDocs().stream().anyMatch(d->d.hasEntities(entitiesToCheck));

            if(fullEvidenceFound)
                fullEvidenceFoundCount++;

            if(annDocs.size()>0)
                evidencesFound++;



            System.out.println("target Entities:" + pagesToCheck);
            System.out.println("Entities in Docs: "+annDocs.getEntities());

            Set<AnnotatedDocument> docsFiltered=annDocs.getDocsWith(entity);
            System.out.println("Filtered Docs: "+docsFiltered.size());

//            Set<Sentence> allSentences=annDocs.getAllSentencesWithOneOf(docsFiltered,false,entity);


//            for (Sentence sen : allSentences) {
//                bw.write(sen.toStringWithDetails(entity));
//                bw.newLine();
//            }
            bw.write(line+"\t"+fullEvidenceFound);
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

        bw.write("Sentences Found for "+evidencesFound+" facts (full: "+fullEvidenceFoundCount+") out of "+ factCount);
        bw.flush();
        bw.close();
    }

}
