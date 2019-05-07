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
        if(arguments.size()> (index-1))
            return arguments.get(index);
        else
            return null;
    }

    public String getIRISRepresenation(){
        return FactUtils.getCleanPredicateName(getPredicate())+"("+Joiner.on(",").join(arguments.stream().map(arg-> '\''+arg+'\'').collect(Collectors.toList()))+").";
    }


    public String getSubject() {
        return  getArgument(0);
    }

    public String getObject(){
            return  getArgument(1);
    }

    public String getIRISQueryRepresenation() {
        return "?- "+getIRISRepresenation();
    }

    @Override
    public String toReadableString(){
        return (FactUtils.getCleanName(arguments.get(0))+" "+FactUtils.getReadablePredicateName(getPredicate()) +" " +FactUtils.getCleanName(arguments.get(1))).trim();
    }

    public String getReadableSubject(){
        return FactUtils.getCleanName(getSubject());
    }

    public String getReadableObject(){
        return FactUtils.getCleanName(getObject());
    }

    public String getReadablePredicateName(){
        return FactUtils.getReadablePredicateName(getPredicate());
    }





}
