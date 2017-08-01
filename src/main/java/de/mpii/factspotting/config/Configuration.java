package de.mpii.factspotting.config;

import de.mpii.de.mpii.processing.NEExtractor;
import de.mpii.factspotting.FactSpotterFactory;
import de.mpii.factspotting.text.ElasticSearchFactSpotter;
import de.mpii.factspotting.text.verbalization.VerbalizerFactory;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/17/17.
 */
@Singleton
public class Configuration {


    private static final java.lang.String EVIDENCE_PER_FACT_SIZE ="evidencePerFactSize" ;
    private static final String PREDICATES_DICTS ="predicatesDictionaries";
    private static final String SPOTTING = "SPOTTING";
    private static final String ARGUMENTS_DICTS ="argumentsDictionaries";
    private static final String TOTAL_PARAPHRASES ="totalParaphrases";
    private static final String PER_ITEM_PARAPHRASES ="perItemParaphrases";
    private static final String VERBALIZER ="verbalizer";
    private static final String TEXT_CORPORA = "textCorpora";
    private static final String DOCUMENT_FIELDS_TO_SEARCH = "fieldsToSearch";
    private static final String MATCHING_THRESHOLD = "matchingThresholdPercentage";
    private static final String ELASTIC_QUERY_STYLE = "elastic.queryStyle";


    /**
     * instance of the configuration
     */
    private static Configuration config;
    private static boolean confFileAlreadySet=false;
    /**
     * configuration filepath
     */
    private static String configurationFile="factchecking.properties";
    /**
     * indicates whether a file is in JAR or not. once filepath is changed this variable is turned to true.
     */
    private static boolean externalConfFile=false;

    /**
     * list of file names containing paraphrases for the predicates
     */
    private List<String> predicatesDictionariesFiles;

    /**
     * list of file names containing mentions for the constants
     */
    private List<String> argumentsMentionsFiles;

    /**
     * Total number of paraphrases used in the spotting process
     */
    private int totalParaphrases;

    /**
     * Number of paraphrases per each of the subject, object, and predicate
     */
    private int perItemParaphrases;


    /**
     * Number of spotted documents/sentences
     */
    private int evidencePerFactSize=5;


    /**
     * fields to search in a document
     */

    private List<String> fieldsToSearch;

    /**
     * The type of verbalization for the fact. 
     */
    private VerbalizerFactory.VerbalizerType verbalizerType= VerbalizerFactory.VerbalizerType.DEFAULT;

    /**
     * List of text Corpora to search for evidences
     */
    private List<String> textCorpora;


    /**
     * Elasticsearch per query minimum matching score
     */
    private String matchingThreshold="0.1";

    /**
     * Method used in spottng the fact
     */
    private FactSpotterFactory.SpottingMethod spottingMethod;

    /**
     * Elastic search query Style either single string or split
     */
    private ElasticSearchFactSpotter.QueryStyle elasticQueryStyle;


    /**
     *
     *
     */
    private NEExtractor neExtractor=NEExtractor.getInstance();

    public String getMatchingThreshold() {
        return matchingThreshold;
    }

    public void setMatchingThreshold(String matchingThreshold) {
        this.matchingThreshold = matchingThreshold;
    }

    public int getEvidencePerFactSize() {
        return evidencePerFactSize;
    }

    public void setEvidencePerFactSize(int evidenceSizePerFact) {
        this.evidencePerFactSize = evidenceSizePerFact;
    }

    public List<String> getPredicatesDictionariesFiles() {
        return predicatesDictionariesFiles;
    }

    public void setPredicatesDictionariesFiles(List<String> predicatesDictionariesFiles) {
        this.predicatesDictionariesFiles = predicatesDictionariesFiles;
    }

    public List<String> getArgumentsMentionsFiles() {
        return argumentsMentionsFiles;
    }



    public void setArgumentsMentionsFiles(List<String> argumentsMentionsFiles) {
        this.argumentsMentionsFiles = argumentsMentionsFiles;
    }

    public Configuration() {
        predicatesDictionariesFiles=new ArrayList<>();
        argumentsMentionsFiles=new ArrayList<>();

    }

    public int getPerItemParaphrases() {
        return perItemParaphrases;
    }

    public void setPerItemParaphrases(int perItemParaphrases) {
        this.perItemParaphrases = perItemParaphrases;
    }

    public int getTotalParaphrases() {
        return totalParaphrases;
    }

    public void setTotalParaphrases(int totalParaphrases) {
        this.totalParaphrases = totalParaphrases;
    }


    public VerbalizerFactory.VerbalizerType getVerbalizerType() {
        return verbalizerType;
    }

    public void setVerbalizerType(VerbalizerFactory.VerbalizerType verbalizerType) {
        this.verbalizerType = verbalizerType;
    }

    public void setTextCorpora(List<String> textCorpora) {
        this.textCorpora = textCorpora;
    }

    public List<String> getTextCorpora() {
        return textCorpora;
    }

    public List<String> getFieldsToSearch() {
        return fieldsToSearch;
    }

    public void setFieldsToSearch(List<String> fieldsToSearch) {
        this.fieldsToSearch = fieldsToSearch;
    }

    public static Configuration fromFile(String filename, boolean inResources){

            Configuration conf=new Configuration();
            Properties prop = new Properties();
            InputStream input = null;

            try {
            // configuration file loaded in resoruces
                if (inResources){
                    input = Configuration.class.getClassLoader().getResourceAsStream(filename);

                }
                else{
                    // configuration file form user
                        input= new FileInputStream(filename);
                    }
                if (input == null) {
                    System.out.println("Sorry, unable to find " + filename);
                    return conf;

            }

                //load a properties file from class path, inside static method
                prop.load(input);

                //get the property value
                conf.setPredicatesDictionariesFiles(asList(prop.getProperty(PREDICATES_DICTS,"")));
                conf.setArgumentsMentionsFiles(asList(prop.getProperty(ARGUMENTS_DICTS,"")));
                conf.setTextCorpora(Arrays.asList(prop.getProperty(TEXT_CORPORA,"wiki").split(",")));
                conf.setFieldsToSearch(Arrays.asList(prop.getProperty(DOCUMENT_FIELDS_TO_SEARCH,"text,title").split(",")));
                conf.setTotalParaphrases(Integer.parseInt(prop.getProperty(TOTAL_PARAPHRASES,"50")));
                conf.setPerItemParaphrases(Integer.parseInt(prop.getProperty(PER_ITEM_PARAPHRASES,"50")));
                conf.setVerbalizerType(VerbalizerFactory.VerbalizerType.valueOf( prop.getProperty(VERBALIZER,"DEFAULT")));
                conf.setEvidencePerFactSize(Integer.parseInt(prop.getProperty(EVIDENCE_PER_FACT_SIZE,"5")));
                conf.setMatchingThreshold(prop.getProperty(MATCHING_THRESHOLD,"10%"));
                conf.setSpottingMethod(FactSpotterFactory.SpottingMethod.valueOf(prop.getProperty(SPOTTING,"NONE")));
                conf.setElasticQueryStyle(ElasticSearchFactSpotter.QueryStyle.valueOf(prop.getProperty(ELASTIC_QUERY_STYLE, ElasticSearchFactSpotter.QueryStyle.SPLIT_QUERY.toString())));

//                System.out.println(conf);

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                if(input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return conf;

        }
    private static List<String> asList(String property){
        List<String> files=new LinkedList<>();
        if(!property.trim().isEmpty())
            files=Arrays.asList(property.split(",")).stream().map(sf->sf.trim()).collect(Collectors.toList());

        return files;
    }


    public static Configuration fromFile(String filename){
            return fromFile(filename,!externalConfFile);
        }



        public  synchronized static Configuration getInstance(){
            if (config==null) {
                config = Configuration.fromFile(configurationFile);
            }

            return config;
        }


    public synchronized static boolean setConfigurationFile(String file){
        if(file==null||confFileAlreadySet)
            return false;
        else
        {
            configurationFile=file;
            confFileAlreadySet=true;
            externalConfFile=true;
            return true;
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "predicatesDictionariesFiles=" + predicatesDictionariesFiles +
                ", argumentsMentionsFiles=" + argumentsMentionsFiles +
                ", totalParaphrases=" + totalParaphrases +
                ", perItemParaphrases=" + perItemParaphrases +
                ", verbalizerType=" + verbalizerType +
                '}';
    }


    public void setSpottingMethod(FactSpotterFactory.SpottingMethod spottingMethod) {
        this.spottingMethod = spottingMethod;
    }

    public void setElasticQueryStyle(ElasticSearchFactSpotter.QueryStyle elasticMatchingMethod) {
        this.elasticQueryStyle = elasticMatchingMethod;
    }

    public ElasticSearchFactSpotter.QueryStyle getElasticQueryStyle() {
        return elasticQueryStyle;
    }
}

