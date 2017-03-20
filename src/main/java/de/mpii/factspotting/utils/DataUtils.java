package de.mpii.factspotting.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.mpii.factspotting.text.IDictionary;
import de.mpii.factspotting.text.PatternParaphrase;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 3/19/17.
 */
public class DataUtils {



    public static SetMultimap<String,String> loadDictionaries(List<String> filenames){
        SetMultimap<String,String> dict= HashMultimap.create();

        for (String filename:filenames) {

            try {
                BufferedReader br = FileUtils.getBufferedUTF8Reader(filename);

               for( String line=br.readLine();line!=null;line=br.readLine()){

                   String[] parts=line.split("\t");

                   dict.put(parts[0],parts[1]);

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return dict;
    }


}
