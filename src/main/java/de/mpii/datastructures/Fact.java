package de.mpii.datastructures;

import com.google.common.base.Joiner;
import de.mpii.dataprocessing.util.FactUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/14/17.
 */
public class Fact implements IFact {



    List<String> arguments;



    String predicate;





    public Fact(String predicate, List<String> arguments) {
        this.arguments = arguments;
        this.predicate = predicate;
    }

    //public List<String> getArguments() {
//        return arguments;
//    }

    //public void setArguments(List<String> arguments) {
//        this.arguments = arguments;
//    }

    public String getPredicate() {
        return predicate;
    }



    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toSearchableString(){
        return getPredicate() +"\t" +Joiner.on('\t').join(arguments).trim();
    }

    public void addArgument(String arg) {
        arguments.add(arg);
    }

    public String getArgument(int index){
        return arguments.get(index);
    }

    public String getIRISRepresenation(){
        return FactUtils.getCleanPredicateName(getPredicate())+"("+Joiner.on(",").join(arguments.stream().map(arg-> '\''+arg+'\'').collect(Collectors.toList()))+").";
    }

    public String getIRISQueryRepresenation() {
        return "?- "+getIRISRepresenation();
    }


}
