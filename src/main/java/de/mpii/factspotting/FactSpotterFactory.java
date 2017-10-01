package de.mpii.factspotting;


import de.mpii.factspotting.text.BingFactSpotter;
import de.mpii.factspotting.text.ElasticSearchFactSpotter;

/**
 * Created by gadelrab on 3/22/17.
 */
public class FactSpotterFactory {


    public enum SpottingMethod{ELASTIC, NONE,BING};



    public static IFactSpotter create(SpottingMethod method){

        switch (method){

            case BING:
                return new BingFactSpotter();
            case ELASTIC:
                return new ElasticSearchFactSpotter();
            case NONE:
            default:
                return new DefualtFactSpotter();
        }

    }





}
