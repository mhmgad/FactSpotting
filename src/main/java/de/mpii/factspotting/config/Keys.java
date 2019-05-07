package de.mpii.factspotting.config;

import de.mpii.factspotting.config.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Keys {



    String bingKey;
    /**
     * instance of the keys
     */
    private static Keys keys;

    /**
     * keys filepath
     */
    private static String KEYS_FILE="keys.config";
    private static boolean externalKeysFile =false;

    private static final String BING_KEY = "bing.key";


    public static Keys fromFile(String filename, boolean inResources) {

        Keys keys = new Keys();
        Properties prop = new Properties();
        InputStream input = null;

        try {
            // configuration file loaded in resoruces
            if (inResources) {
                input = Configuration.class.getClassLoader().getResourceAsStream(filename);

            } else {
                // configuration file form user
                input = new FileInputStream(filename);
            }
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return keys;

            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //TODO bing key set
            keys.setBingKey(prop.getProperty(BING_KEY, "null"));


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return keys;
    }

    public static Keys fromFile(String filename){
        return fromFile(filename,!externalKeysFile);
    }



    public  synchronized static Keys getInstance(){
        if (keys==null) {
            keys = Keys.fromFile(KEYS_FILE);
        }

        return keys;
    }


    public void setBingKey(String bingKey) {
        this.bingKey = bingKey;
    }

    public String getBingKey() {
        return this.bingKey;
    }
}
