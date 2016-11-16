package jrbn.wikipedia;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;

public class PageExtractor implements IArticleFilter {

	private WikipediaPage page;
	private boolean isEmpty;

	@Override
	public void process(WikiArticle article, Siteinfo siteinfo) {
		page = null;
		isEmpty = false;
		if (article.isMain()) {
			// Templates are ignored
			MyWikiModel model = new MyWikiModel("", "");
			try {
				model.setUp();
				String htmlContent = model.render(article.getText());
				isEmpty = htmlContent.isEmpty();
				if (!isEmpty) {
					// Remove HTML tags
					Whitelist allowedTags = Whitelist.none();
					allowedTags.addTags("a");
					String rawContent = Jsoup.clean(htmlContent, allowedTags);

					// Remove references
					rawContent = rawContent.replaceAll("\\[[0-9]+\\]", "");
					// Remove ==References== section
					rawContent = rawContent.replaceFirst("==References==", "");
					// Remove everything after "see also"
					int idx = rawContent.indexOf("==See also==");
					if (idx != -1) {
						rawContent = rawContent.substring(0, idx);
					}
					// Remove all sections, subsections, etc.
					rawContent = rawContent.replaceAll("==+[^\\s]+==+", " ");

					// Gad (Just temp fix I have no clue what that should do)
					int newIdx=0;
					// Get links to other pages TODO...
					for (idx = 0; idx < rawContent.length();) {
						int startTag = rawContent.indexOf("<a", idx);
						if (startTag != -1) {
							// Get the title of the link, points to the topic
							int endTag = rawContent.indexOf(">", startTag);
							if (endTag != -1) {
								String tagContent = rawContent.substring(startTag, endTag);

								int startTitle = htmlContent.indexOf("title=\"", newIdx);
								if (startTitle != -1) {
									int endTitle = htmlContent.indexOf("\"", startTitle + 7);
									if (endTitle != -1) {
										String titleEntity = htmlContent.substring(startTitle, endTitle);
										System.out.println(titleEntity);
									}
								}
								idx = newIdx + 1;
							}
						} else {
							idx = rawContent.length();
						}
					}

					// Create the Wikipedia object
					page = new WikipediaPage();
					page.setText(rawContent);
					page.setId(article.getId());
					page.setTitle(article.getTitle());
				}
			} catch (Exception e) {
			} finally {
				model.tearDown();
			}
		}
	}

	public boolean isValid() {
		return page != null;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public WikipediaPage getPage() {
		return page;
	}
}