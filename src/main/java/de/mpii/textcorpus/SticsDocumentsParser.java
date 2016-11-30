package de.mpii.textcorpus;

import de.mpii.containers.*;
import de.mpii.de.mpii.processing.CoreferenceResolver;
import de.mpii.de.mpii.processing.SentenceExtractor;
import mpi.tools.javatools.util.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 11/16/16.
 */
public class SticsDocumentsParser extends CorpusParser{





    public  AnnotatedDocument documentFromJSON(String jsonDoc){

        JSONParser parser=new JSONParser();

        try {

            Object obj = parser.parse(jsonDoc);

            JSONObject jsonObject = (JSONObject) obj;

            //since we care only about the sentences we can skip till originaltext field in AIDA output

            JSONObject aidaOutput = (JSONObject) jsonObject.get("aida");
//            System.out.println(aidaOutput);

            String originalText = (String) aidaOutput.get("originalText");
//            System.out.println(originalText);

            // loop array
            JSONArray mentionsJSONArr = (JSONArray) aidaOutput.get("mentions");
//            System.out.println(mentionsJSONArr);

            Mentions mentions= mentionsFromJSONArray(mentionsJSONArr);

            AnnotatedDocument doc= new AnnotatedDocument(originalText);
            doc.setMentions(mentions);

//            return new AnnotatedDocument(originalText,mentions);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     *
     * Creates AnnotatedDocuments object from stics dump.
     */
    public  AnnotatedDocuments documentsFromJSON(String filePath) throws IOException {
        System.out.println("=============== Load Annotated Documents ");
        AnnotatedDocuments docs=new AnnotatedDocuments();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String line=br.readLine();line!=null;line=br.readLine()){
            try {
                AnnotatedDocument doc = documentFromJSON(line);
                docs.add(doc);
            }catch (Exception e){

            }
        }

        System.out.println("=============== Done! ");
        return docs;


    }






    public  Mentions mentionsFromJSONArray(JSONArray mentionsJSONArr) {

        Mentions mentions=new Mentions();

        Iterator<JSONObject> iterator = mentionsJSONArr.iterator();
        while (iterator.hasNext()) {
            JSONObject current= iterator.next();
//            System.out.println(current);
            mentions.add(mentionFromJSON(current));
        }
        return mentions;
    }


    public  Mention mentionFromJSON(JSONObject mentionJSON) {

//        System.out.println(mentionJSON);
        long length=(Long) mentionJSON.get("length");
        long offset=(Long) mentionJSON.get("offset");
        String mentionText= (String) mentionJSON.get("name");

        JSONObject bestEntity= (JSONObject)mentionJSON.get("bestEntity");
//        System.out.println(bestEntity);
        String entityId= bestEntity==null? null:(String) bestEntity.get("kbIdentifier");


        return new Mention( mentionText,  offset,  length,  entityId);
    }


    public static void main(String[]args) throws IOException {
        SticsDocumentsParser parser=new SticsDocumentsParser();

        Entity [] entities=new Entity[]{new Entity("<Amy_Adams>"), new Entity("<Academy_Awards>")/*, new Entity("<France>")*/};

        AnnotatedDocuments annDocs = parser.documentsFromJSON("Amy_Adams_Academy_Awards.json");
        annDocs.findSentences();
        System.out.println("All Documents:" + annDocs.size());



        //Get all sentences before Coref
        Set<AnnotatedDocument> filteredDocs=annDocs.getDocsWith(entities);//new Entity("<Amy_Adams>"), new Entity("<Academy_Awards>"), new Entity("<France>"));
        System.out.println("Filtered Documents:" + filteredDocs.size());

        Set<Sentence> allSentences=annDocs.getAllSentencesWithOneOf(entities);//new Entity("<Amy_Adams>"), new Entity("<Academy_Awards>"), new Entity("<France>"));
        System.out.println("Sentences Size: "+allSentences.size());

        // Coref and get all sentences
//        annDocs.resolveCoreferences();
        filteredDocs.parallelStream().forEach(d-> d.resolveCoreferences(CoreferenceResolver.getCoreferenceChains(d.getText())));
        Set<Sentence> allSentencesCoref=annDocs.getAllSentencesWithOneOf(entities);//new Entity("<Amy_Adams>"), new Entity("<Academy_Awards>"), new Entity("<France>"));
        System.out.println("After Coref Sentences Size: "+allSentencesCoref.size());


    }


//    @Override
//    protected Mentions parseMentions(String line) {
//        return null;
//    }
//
//    @Override
//    protected AnnotatedDocument parseDocument(String line) {
//        return null;
//    }
}
