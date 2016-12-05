package de.mpii.textcorpus;


import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.AnnotatedDocuments;
import de.mpii.de.mpii.processing.SentenceExtractor;
import de.mpii.de.mpii.processing.entitydisambiguation.AmbiverseDocumentAnnotator;
import de.mpii.de.mpii.processing.entitydisambiguation.DocumentAnnotator;
import mpi.tools.javatools.util.FileUtils;


import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;


/**
 * Created by gadelrab on 12/5/16.
 */
public class WikiDocumentsParser {

    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    DocumentAnnotator documentAnnotator ;

    public WikiDocumentsParser() throws Exception {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        documentAnnotator = AmbiverseDocumentAnnotator.getInstance();
    }

    public AnnotatedDocuments parseFiles(String ... filePaths) throws IOException, SAXException {


        AnnotatedDocuments docs=new AnnotatedDocuments();

        for (String filePath:filePaths) {

//            BufferedReader br= FileUtils.getBufferedUTF8Reader(filePath);
//
//
//            Enumeration<InputStream> streams = Collections.enumeration(
//                    Arrays.asList(new InputStream[] {
//                            new ByteArrayInputStream("<root>".getBytes()),
//                            br,
//                            new ByteArrayInputStream("</root>".getBytes()),
//                    }));

//            SequenceInputStream seqStream = new SequenceInputStream(streams);

//            Document doc = Jsoup.parse(new File(filePath),"UTF-8");
////            System.out.println(doc);
//            Elements docsInFile= doc.select("doc");
//            System.out.println(docsInFile.size());


//            StringBuilder xmlStringBuilder = new StringBuilder();
//            xmlStringBuilder.append("<?xml version="1.0"?> <class> </class>");
//            ByteArrayInputStream input =  new ByteArrayInputStream(
//                    xmlStringBuilder.toString().getBytes("UTF-8"));

//            ByteArrayInputStream input =  new ByteArrayInputStream();

//            Enumeration<InputStream> streams = Collections.enumeration(
//                    Arrays.asList(new InputStream[] {
//                            new ByteArrayInputStream("<root>".getBytes()),
//                            new FileInputStream(filePath),
//                            new ByteArrayInputStream("</root>".getBytes()),
//                    }));
//
//            SequenceInputStream seqStream = new SequenceInputStream(streams);
//
//            Document doc = builder.parse(seqStream);
//
//            Element root = doc.getDocumentElement();
//            NodeList nodes = root.getChildNodes();
//            System.out.println(nodes.getLength());


            String allContent=FileUtils.getFileContent(new File(filePath));
            String[] docsString=allContent.split("</doc>\n");
            System.out.println(docsString.length);

//            System.out.println(parseDoc(docsString[0]));

            Arrays.stream(docsString).limit(10).forEach(d->{
                AnnotatedDocument doc=parseDoc(d);

                doc.setSentences(SentenceExtractor.getSentences(doc));
                try {
//                    doc.setMentions(documentAnnotator.annotate(doc));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                docs.add(doc);

            });


        }

        return docs;

    }

    private  AnnotatedDocument parseDoc(String dString)  {

//        System.out.println(dString);
        String header= dString.substring(0,dString.indexOf('\n')+1);

        StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            xmlStringBuilder.append(header);//.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" ));
            xmlStringBuilder.append("</doc>");

        try {
            ByteArrayInputStream input =  new ByteArrayInputStream(
                    xmlStringBuilder.toString().getBytes("UTF-8"));

            Document docXML = builder.parse(input);

            Element root = docXML.getDocumentElement();

//            System.out.println(root.getTextContent());
            String content=dString.substring(dString.indexOf('\n')+1);
            System.out.println(content.length());
            int id=Integer.parseInt(root.getAttribute("id"));
            String url=root.getAttribute("url");



            AnnotatedDocument doc=new AnnotatedDocument(id,content,url);

            return doc;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(dString);
        }

        return null;



    }


    public static void main(String[] args) throws Exception {

        WikiDocumentsParser wikiDocumentsParser=new WikiDocumentsParser();
        AnnotatedDocuments docs=wikiDocumentsParser.parseFiles("wiki_00");

        System.out.println(docs.size());


    }


}
