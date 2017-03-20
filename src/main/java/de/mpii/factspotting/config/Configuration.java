package de.mpii.factspotting.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by gadelrab on 3/17/17.
 */
public class Configuration {

    private static Configuration config;

        static final String PREDICATES_DICTS ="predicatesDictionaries";
        static final String ARGUMENTS_DICTS ="argumentsDictionaries";


        List<String> predicatesDictionariesFiles;
        List<String> argumentsMentionsFiles;

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
                conf.setPredicatesDictionariesFiles(Arrays.asList(prop.getProperty(PREDICATES_DICTS).split(",")));
                conf.setPredicatesDictionariesFiles(Arrays.asList(prop.getProperty(ARGUMENTS_DICTS).split(",")));


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

        public static Configuration fromFile(String filename){
            return fromFile(filename,true);
        }



        public  synchronized static Configuration getInstance(){
            if (config==null) {
                config = Configuration.fromFile("factchecking.properities");
            }
            return config;
        }








}

