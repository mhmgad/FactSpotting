package de.mpii.containers;

import org.json.simple.JSONObject;

/**
 * Created by gadelrab on 11/2/16.
 */
public class Entity {


    private String id;
    private double score;

    public Entity(String id, double score) {
        this.id=Entity.fixEntityId(id);
        this.score=score;
    }

    public Entity(String id){
        this(id,0.0);
    }

    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
//                ", score=" + score +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        return ((Entity) obj).getId().equals(this.getId());
    }

    public static String fixEntityId(String kbIdentifier) {
        String text= kbIdentifier.replace("YAGO3:","");

//        if(text.startsWith("<"))
//            text=  text.replace("<","");
//        if (text.endsWith(">"))
//            text=  text.replace(">","");


        return text;

    }


    public JSONObject toJSON() {
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("id",id);
        jsonObj.put("score",score);
        return jsonObj;
    }

    /**
     * returns the possible wikipedia title
     * @return
     */
    public String getIdAsTitle(){
        String fixedId=id.substring(1,id.length()-1).replace('_',' ');
        return fixedId;
    }

    public static String toProperId(String item) {
        String properId=item;
        if(!properId.startsWith("<"))
            properId="<"+properId;
        if(!properId.endsWith(">"))
            properId=properId+">";

        return properId;
    }
}
