package de.mpii.factspotting.text.retrievers;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;


/**
 * 
 * Removes the stop word passed as a token
 * 
 */
/**
 * This class is adapted from
 * Kashyap Popat, Subhabrata Mukherjee, Jannik Strötgen, and Gerhard Weikum. 2016. Credibility Assessment of Textual Claims on the Web. In Proceedings of the 25th ACM International on Conference on Information and Knowledge Management (CIKM '16). ACM, New York, NY, USA, 2173-2178. DOI: https://doi.org/10.1145/2983323.2983661
 */
public class StopWordRemover {

	public HashMap<String, String> stopwords;

	public StopWordRemover() {
		this.stopwords = new HashMap<String, String>();
		// populateStopWords(ApplicationProperty.dataPath+ "stopwords.txt");
		populateStopWords(getClass().getClassLoader().getResource("smallStopwords.txt").getFile());
	}
	
	public StopWordRemover(String stopWordsFile) {
		this.stopwords = new HashMap<String, String>();
		// populateStopWords(ApplicationProperty.dataPath+ "stopwords.txt");
		populateStopWords(stopWordsFile);
	}


	/**
	 * @param word
	 *            the given token
	 * @return if the word is a stopword, an empty string will be returned; or
	 *         else the word itself will be returned
	 */
	public String removeIfStopWord(String word) {
		if (this.stopwords.containsKey(word)) {
			return "";
		} else {
			return word;
		}

	}

	boolean isNegation(String str) {

		if (str.equals("not") || str.equals("never") || str.equals("neither")
				|| str.equals("nor") || str.equals("no"))
			return true;
		return false;
	}

	/**
	 * @param text
	 *            the given sentence
	 * @return the sentence with all stopwords removed
	 */
	public String removeAllStopWords(String text, boolean keepNegation) {

		String tok[] = text.split("( )+");
//		String buf = "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tok.length; i++) {
			if (keepNegation && isNegation(tok[i])) {
				sb.append(tok[i]);
				sb.append(" ");
				continue;
			}
			if (this.stopwords.containsKey(tok[i]))
				continue;
			sb.append(tok[i]);
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * Populate the stop words
	 * 
	 * @param sourceFile
	 *            file containing stop words
	 */
	public void populateStopWords(String sourceFile) {
		try {

			// System.out.println("POPULATING STOP WORD REMOVER");
			BufferedReader in = new BufferedReader(new FileReader(sourceFile));
			String line = "";
			while ((line = in.readLine()) != null) {
				this.stopwords.put(line.toLowerCase().trim(), "");

			}
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
