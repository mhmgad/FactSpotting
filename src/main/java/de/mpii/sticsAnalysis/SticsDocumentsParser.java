package de.mpii.sticsAnalysis;

import de.mpii.containers.*;
import mpi.tools.javatools.util.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by gadelrab on 11/16/16.
 */
public class SticsDocumentsParser {


    public static AnnotatedDocument documentFromJSON(String jsonDoc){

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
    public static AnnotatedDocuments documentsFromJSON(String filePath) throws IOException {
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






    public static Mentions mentionsFromJSONArray(JSONArray mentionsJSONArr) {

        Mentions mentions=new Mentions();

        Iterator<JSONObject> iterator = mentionsJSONArr.iterator();
        while (iterator.hasNext()) {
            JSONObject current= iterator.next();
//            System.out.println(current);
            mentions.add(mentionFromJSON(current));
        }
        return mentions;
    }


    public static Mention mentionFromJSON(JSONObject mentionJSON) {

        long length=(Long) mentionJSON.get("length");
        long offset=(Long) mentionJSON.get("offset");
        String mentionText= (String) mentionJSON.get("name");

        JSONObject bestEntity= (JSONObject)mentionJSON.get("bestEntity");
        String entityId= (String) bestEntity.get("kbIdentifier");


        return new Mention( mentionText,  offset,  length,  entityId);
    }


    public static void main(String[]args) throws IOException {

        AnnotatedDocuments annDocs = SticsDocumentsParser.documentsFromJSON("Amy_Adams_Academy_Awards.json");

        System.out.println(annDocs.size());
        Set<AnnotatedDocument> filteredDocs = annDocs.getDocsWith(new Entity("<Amy_Adams>"), new Entity("<Academy_Awards>")/*,"<France>"*/ );
        System.out.println(filteredDocs.size());
        // filteredDocs.forEach(d-> System.out.println(d.getSentences()));
        filteredDocs.stream().map(d-> d.getSentencesWith(new Entity("<Amy_Adams>" ))).flatMap(s->s.stream()).forEach(s-> System.out.println("-> "+s));
        //filteredDocs.forEach(d-> System.out.println(d.getMentionsWith("<Amy_Adams>")));

    }







}
