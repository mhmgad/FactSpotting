package jrbn.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import nl.vu.cs.ajira.actions.Action;
import nl.vu.cs.ajira.actions.ActionContext;
import nl.vu.cs.ajira.actions.ActionOutput;
import nl.vu.cs.ajira.data.types.TString;
import nl.vu.cs.ajira.data.types.Tuple;

public class ExtractSentences extends Action {

	StanfordCoreNLP pipeline;

	@Override
	public void startProcess(ActionContext context) throws Exception {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	public void process(Tuple input, ActionContext context, ActionOutput output) throws Exception {

		Set<String> entities = new HashSet<>();
		long time = System.currentTimeMillis();

		// Split the text in sentences
		String text = ((TString) input.get(0)).getValue();
		String[] tokens = text.split("\t");
		String article = tokens[2];
		if (tokens.length > 3) {
			// Merge multiple lines
			for (int i = 3; i < tokens.length; ++i)
				article += ' ' + tokens[i];
		}
		Annotation document = new Annotation(article);
		pipeline.annotate(document);
		int nsentences = 0;
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			nsentences++;
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				// String pos = token.get(PartOfSpeechAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);
				// System.out.println("Word=" + word + " ne=" + ne);
				if (ne.equals("LOCATION") || ne.equals("ORGANIZATION") || ne.equals("PERSON"))
					entities.add(word);

			}
		}

		// Print top 10
		String top10Entities = "(";
		int i = 0;
		for (String ent : entities) {
			if (i == 0)
				top10Entities += ent;
			else
				top10Entities += "," + ent;
			if (i++ > 9)
				break;
		}

		top10Entities += ")";

		System.out.println("Page " + tokens[0] + " title=\"" + tokens[1] + "\" nsentences=" + nsentences + " entities="
				+ entities.size() + " time=" + (System.currentTimeMillis() - time) + " " + top10Entities);
	}

	@Override
	public void stopProcess(ActionContext context, ActionOutput actionOutput) throws Exception {
	}
}