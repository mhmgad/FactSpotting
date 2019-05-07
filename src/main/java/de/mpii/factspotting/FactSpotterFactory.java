package de.mpii.factspotting;


import de.mpii.factspotting.text.BingFactSpotter;
import de.mpii.factspotting.text.ElasticSearchFactSpotter;

/**
 * Created by gadelrab on 3/22/17.
 */
public class FactSpotterFactory {


    public enum SpottingMethod{ELASTIC, NONE,BING};



    public static IFactSpotter create(SpottingMethod method){
        IFactSpotter sp=null;
        switch (method){

            case BING:
                sp= new BingFactSpotter();
                break;
            case ELASTIC:
                sp= new ElasticSearchFactSpotter();
            break;
            case NONE:
            default:
                sp= new DefualtFactSpotter();
                break;
        }

        System.out.println("*********************\nSpotterConfiguration:\n\n"+sp.toString()+"\n****************************************");
        return sp;

    }





}
