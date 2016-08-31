package de.mpii.sticsAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gadelrab on 8/31/16.
 */
public class Mentions  {

    List<Mention> mentions;


    public Mentions() {
        this.mentions = new ArrayList<Mention>();
    }

    public Mentions(List<Mention> mentions) {
        this.mentions = mentions;
    }


}
