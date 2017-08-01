package de.mpii.datastructures;

import java.util.ArrayList;

/**
 * Created by gadelrab on 3/14/17.
 */
public class BinaryFact  extends Fact{




    public BinaryFact (String subject,String predicate, String object){
        super(predicate,new ArrayList(2));
        super.addArgument(subject);
        super.addArgument(object);
    }

    public String getSubject() {
        return  super.getArgument(0);
    }

    public String getObject(){
        return  super.getArgument(1);
    }

    public String toSearchableString(){
        return (getSubject()+"\t"+getPredicate() +"\t" +getObject()).trim();
    }
}
