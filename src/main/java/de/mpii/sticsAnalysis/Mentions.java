package de.mpii.sticsAnalysis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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


    public static Mentions fromJSONArray(JSONArray mentionsJSONArr) {

        Mentions mentions=new Mentions();

        Iterator<JSONObject> iterator = mentionsJSONArr.iterator();
        while (iterator.hasNext()) {
            JSONObject current= iterator.next();
//            System.out.println(current);

            mentions.add(Mention.fromJSON(current));
        }



        return mentions;

    }

    @Override
    public String toString() {
        return "Mentions{" +
                "mentions=" + mentions +
                '}';
    }

    private void add(Mention mention) {
        mentions.add(mention);
    }
}
