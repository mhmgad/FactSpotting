package de.mpii.factspotting.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.mpii.factspotting.text.verbalization.TextParaphrase;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by gadelrab on 3/19/17.
 */
public class DataUtils {



    public static SetMultimap<String,TextParaphrase> loadDictionaries(List<String> filenames){
        SetMultimap<String,TextParaphrase> dict= HashMultimap.create();


        for (String filename:filenames) {
            System.out.println("loading file: "+filename);
            try {
                BufferedReader br = FileUtils.getBufferedUTF8Reader(filename);

               for( String line=br.readLine();line!=null&&!line.isEmpty();line=br.readLine()){

                   String[] parts=line.split("\t");

                   TextParaphrase tp=new TextParaphrase(parts[1],((parts.length>2)? Integer.parseInt(parts[2]):0));

                   dict.put(parts[0],tp);

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return dict;
    }


}
