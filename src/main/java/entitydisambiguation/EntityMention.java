package entitydisambiguation;

import com.ambiverse.api.model.EntityReference;

import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 11/2/16.
 */
public class EntityMention {


//    String name;             // Name
//
//    Set<String> categories;    // Categories
//    List<String> links ;              // Links, e.g. to Wikipedia articles

    String text;
    Integer charOffset;
    Integer charLength;
    Entity entity;
    double confidence;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCharOffset() {
        return charOffset;
    }

    public void setCharOffset(Integer charOffset) {
        this.charOffset = charOffset;
    }

    public Integer getCharLength() {
        return charLength;
    }

    public void setCharLength(Integer charLength) {
        this.charLength = charLength;
    }



    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public int hashCode() {
        return text.hashCode()^charOffset.hashCode()^charLength.hashCode();
    }

    @Override
    public String toString() {
        return "EntityMention{" +
                "text='" + text + '\'' +
                ", charOffset=" + charOffset +
                ", charLength=" + charLength +
                ", entity=" + entity +
                ", confidence=" + confidence +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        EntityMention em= (EntityMention) obj;
        return (em.getCharOffset().equals(this.getCharOffset()))&&(em.getCharLength().equals(this.getCharLength()))&&(em.getText().equals(this.getText()));
    }
}
