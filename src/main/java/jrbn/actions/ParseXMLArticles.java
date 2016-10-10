package jrbn.actions;

import java.io.StringReader;

import info.bliki.wiki.dump.WikiXMLParser;
import jrbn.wikipedia.PageExtractor;
import jrbn.wikipedia.WikipediaPage;
import nl.vu.cs.ajira.actions.Action;
import nl.vu.cs.ajira.actions.ActionContext;
import nl.vu.cs.ajira.actions.ActionOutput;
import nl.vu.cs.ajira.data.types.TString;
import nl.vu.cs.ajira.data.types.Tuple;
import nl.vu.cs.ajira.data.types.TupleFactory;

public class ParseXMLArticles extends Action {

	private boolean encodingOn;
	private String article;

	TString id;
	TString title;
	TString text;
	Tuple t;

	@Override
	public void startProcess(ActionContext context) throws Exception {
		encodingOn = false;
		article = "";
		id = new TString();
		title = new TString();
		text = new TString();
		t = TupleFactory.newTuple(id, title, text);
	}

	private WikipediaPage parseArticle(String article, ActionContext context) throws Exception {
		PageExtractor extractor = new PageExtractor();
		StringReader reader = new StringReader(article);
		WikiXMLParser parser = new WikiXMLParser(reader, extractor);
		try {
			parser.parse();
		} catch (Exception e) {
		}

		if (extractor.isValid()) {
			return extractor.getPage();
		} else if (extractor.isEmpty()) {
			context.incrCounter("EMPTY_PAGES", 1);
		}
		return null;
	}

	@Override
	public void process(Tuple input, ActionContext context, ActionOutput output) throws Exception {
		TString line = (TString) input.get(0);
		String sLine = line.getValue();
		if (sLine.indexOf("<page>") != -1) {
			encodingOn = true;
			article = sLine;
		} else if (sLine.indexOf("</page>") != -1) {
			article += sLine;
			WikipediaPage page = parseArticle(article, context);
			if (page != null) {
				context.incrCounter("PROCESSED_VALID_PAGES", 1);
				// Output a record
				id.setValue(page.getId());
				title.setValue(page.getTitle());
				text.setValue(page.getText());
				output.output(t);
			} else {
				context.incrCounter("PROCESSED_INVALID_PAGES", 1);
			}
			encodingOn = false;
			context.incrCounter("PROCESSED_PAGES", 1);
		} else if (encodingOn) {
			article += sLine;
		}
	}

	@Override
	public void stopProcess(ActionContext context, ActionOutput actionOutput) throws Exception {
		id = null;
		title = null;
		text = null;
		t = null;
	}
}