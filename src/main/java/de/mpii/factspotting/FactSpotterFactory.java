package de.mpii.factspotting;


import de.mpii.factspotting.text.ElasticSearchFactSpotter;

/**
 * Created by gadelrab on 3/22/17.
 */
public class FactSpotterFactory {


    public enum SpottingMethod{ELASTIC, NONE};



    public static IFactSpotter create(SpottingMethod method){

        switch (method){
            case ELASTIC:
                return new ElasticSearchFactSpotter();
            case NONE:
            default:
                return new DefualtFactSpotter();
        }

    }





}
