package jrbn.wikipedia;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.template.ITemplateFunction;

public class MyWikiModel extends WikiModel {

	public Map<String, Map<String, String>> templates;

	public MyWikiModel(String imageBaseURL, String linkBaseURL) {
		super(imageBaseURL, linkBaseURL);
		templates = new HashMap<String, Map<String, String>>();
	}

	@Override
	public void substituteTemplateCall(String templateName, Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		templates.put(templateName, parameterMap);
	}

	// @Override
	/*
	 * public void appendInternalLink(String topic, String hashSection, String
	 * topicDescription, String cssClass, boolean parseRecursive) { //
	 * super.appendInternalLink(topic, hashSection, topicDescription, //
	 * cssClass, parseRecursive); append(new ContentToken(topicDescription)); }
	 */

	@Override
	public ITemplateFunction addTemplateFunction(String key, ITemplateFunction value) {
		// Ignore.
		return null;
	}

}