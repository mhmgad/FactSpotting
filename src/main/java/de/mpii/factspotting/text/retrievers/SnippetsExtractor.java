package de.mpii.factspotting.text.retrievers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class is adapted from
 * Kashyap Popat, Subhabrata Mukherjee, Jannik Strötgen, and Gerhard Weikum. 2016. Credibility Assessment of Textual Claims on the Web. In Proceedings of the 25th ACM International on Conference on Information and Knowledge Management (CIKM '16). ACM, New York, NY, USA, 2173-2178. DOI: https://doi.org/10.1145/2983323.2983661
 */

public class SnippetsExtractor {



    static StopWordRemover sw = new StopWordRemover();


    /**
     * This function cleans the text.
     *
     * @param text
     *            - Input text to be cleaned
     * @return cleaned text
     */
    public static String cleanText(String text) {
        // Replace all the non readable characters
        text = text.replaceAll("[^\\x00-\\x7F]", " ")
                // .replaceAll("\\s+|\\xA0+|\\xc2+", " ")
                .replaceAll("\\xA0+|\\xc2+", " ").trim();
        text = text.replace("\u0000", "");

        // text = text.replaceAll("\\s+", " ");

        // Replace all the multiple white spaces by one space
        text = text.replaceAll(" +", " ");

        // return cleaned text
        return text;

    }

    public static String preProcessData(String ipString) {
        String review = ipString;

        review = cleanText(review);
        review = review.replaceAll("n't", " not");
        review = review.replaceAll("-", "");
        review = review.replaceAll("[\\[\\],$();:<>=@\\+\\#$|^\\~`^*/\\.'\t\"']", " ");
        review = review.replaceAll("[\"?!]", " $0 ");
        // System.out.println(review);

        review = sw.removeAllStopWords(review.toLowerCase(), false);
        // review = lem.lemmatizeWords(review.trim());

        return review;

    }

    String getWebpage(String url) {
        // System.out.println("\t\t\tParsing file");
        // Parse the html and create the document
        Document doc;
        try {
            doc = Jsoup.connect(url).userAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .validateTLSCertificates(false).get();

            // Replace all the "br" and "p" with new line
            doc.select("br").append("\\n");
            doc.select("p").prepend("\\n\\n");
            doc.select("div").prepend("\\n");

            // Clean the text

            String text = cleanText(doc.select("body div p, body p, h1, h2, h3").text()).replaceAll("\\\\n", "\n");
            text = text.replaceAll("[ ]*[\r\n]+[ ]*", "\n").trim();
            text = text.replaceAll("[\r\n]+", "\n").trim();

            return text;
        } catch (Exception e) {

            System.out.println("ERROR: Could not parse: " + url);
            return "";
        }

    }

    public Map<String, Double> getSimilatrSnippet(String url, String claimText) {

        String docText = getWebpage(url);

        Map<String, Double> snippetSimilarities;
        ArrayList<String> snippetCombinations;

        snippetCombinations = generateCombinations(docText);

        snippetSimilarities = findSnippetSimilarity(snippetCombinations, claimText);

        return snippetSimilarities;

    }

    public static Double percentOverlape(String a, String b) {
        Double overlap;
        a = a.toLowerCase();
        b = b.toLowerCase();

        a = a.replaceAll("[^a-zA-Z0-9\\s]", "");
        b = b.replaceAll("[^a-zA-Z0-9\\s]", "");

        List<String> aTokens = Arrays.asList(a.split("( )+"));
        aTokens = new ArrayList<String>(new LinkedHashSet<String>(aTokens));

        List<String> bTokens = Arrays.asList(b.split("( )+"));
        bTokens = new ArrayList<String>(new LinkedHashSet<String>(bTokens));

        List<String> intersection = new ArrayList<String>(aTokens);
        intersection.retainAll(bTokens);

        overlap = (double) intersection.size() / (aTokens.size());
        return overlap;
    }

    public static Map<String, Double> findSnippetSimilarity(ArrayList<String> snippetCombinations, String claimText) {
        Map<String, Double> snippetSimilarities = new HashMap<String, Double>();

        Double similarity = 0d;
        // System.out.println(claimText);
        // System.out.println("============");
        claimText = preProcessData(claimText);
        for (String snippet : snippetCombinations) {

            String cleaned = preProcessData(snippet);

            similarity = percentOverlape(claimText, cleaned);

            if (similarity.compareTo(0d) > 0)
                snippetSimilarities.put(snippet, similarity);

        }

        return snippetSimilarities;
    }

    /**
     * @param docText
     * @return
     */
    public static ArrayList<String> generateCombinations(String docText) {

        ArrayList<String> combinations = new ArrayList<String>();
        // ArrayList<Sentence> sentences = new ArrayList<Sentence>();
        ArrayList<String> sentences = new ArrayList<String>();
        Pattern pattern = Pattern.compile("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|\\!)\\s");

        // docText = docText.replaceAll("[\n\r]", ". ");
        // sentences.addAll(new Document(docText).sentences());

        // sentences = new
        // ArrayList<String>(Arrays.asList(docText.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|\\!)\\s")));
        // sentences = TextOperations.cleanSentences(sentences);

        for (String para : docText.split("[\n\r]"))
            for (String sentence : pattern.split(para))
                sentences.add(sentence);

        for (int i = 0; i < sentences.size(); i++) {

            combinations.add(sentences.get(i));
            if (i + 1 < sentences.size())
                combinations.add(sentences.get(i) + " " + sentences.get(i + 1));
            if (i + 2 < sentences.size())
                combinations.add(sentences.get(i) + " " + sentences.get(i + 1) + " " + sentences.get(i + 2));
            if (i + 3 < sentences.size())
                combinations.add(sentences.get(i) + " " + sentences.get(i + 1) + " " + sentences.get(i + 2) + " "
                        + sentences.get(i + 3));
        }

        return combinations;
    }
}
