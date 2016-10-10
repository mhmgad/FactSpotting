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
					String rawContent = Jsoup.clean(htmlContent, Whitelist.none());
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