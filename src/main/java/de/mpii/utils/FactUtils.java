package de.mpii.utils;

public class FactUtils {


    public static String getCleanPredicateName(String predicateName) {
        if(predicateName.startsWith("<"))
            predicateName= predicateName.replace("<","").replace(">","");


        predicateName=predicateName.replaceAll(":","_").replaceAll("\\.","_");
        return predicateName;
    }
}
