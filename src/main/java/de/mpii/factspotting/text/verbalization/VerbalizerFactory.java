package de.mpii.factspotting.text.verbalization;

import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.dictionarybased.DictionaryBasedVerbalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gadelrab on 3/20/17.
 */
public class VerbalizerFactory {

    public enum VerbalizerType{DEFAULT, DICTIONARY};
    private static Map<VerbalizerType,IFactVerbalizer> verbalizers=new HashMap<>();


    public static IFactVerbalizer getInstance(){
        return getInstance(Configuration.getInstance());
    }

    private synchronized static IFactVerbalizer getInstance(Configuration configuration) {
        System.out.println(configuration.getVerbalizerType());

        if(!verbalizers.containsKey(configuration.getVerbalizerType())) {
            switch (configuration.getVerbalizerType()) {
                case DICTIONARY:
                    verbalizers.put(configuration.getVerbalizerType(), new DictionaryBasedVerbalizer(configuration));
                    break;
                default:
                    verbalizers.put(configuration.getVerbalizerType(), new DefaultVerbalizer());
            }
        }
        return verbalizers.get(configuration.getVerbalizerType());

    }


}
