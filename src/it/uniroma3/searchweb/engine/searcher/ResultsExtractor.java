package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;

public class ResultsExtractor {
	private static final Logger logger = Logger.getLogger(ResultsExtractor.class.getName());
	private SearcherManager manager;
	private Analyzer analyzer;
	private Query query;
	private String field;

	public ResultsExtractor(SearcherManager manager, Analyzer analyzer,
			Query query, String field) {
		this.manager = manager;
		this.analyzer = analyzer;
		this.query = query;
		this.field = field;
	}
	
	public List<Result> getResults(ScoreDoc[] docs, int start, int end) {
		List<Result> results = new ArrayList<Result>();
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<b>", "</b>");
			Highlighter highlighter = new Highlighter(htmlFormatter,
					new QueryScorer(this.query));
			
			for (int i = start; i < end; i++) {
				int id = docs[i].doc;
				
				Document doc = searcher.doc(id);
				String text = doc.get(this.field);
				TokenStream tokenStream = TokenSources.getAnyTokenStream(
						searcher.getIndexReader(), id, this.field, this.analyzer);
				TextFragment[] frag = highlighter.getBestTextFragments(
						tokenStream, text, false, 2);
				String snippet = "";
				for (int j = 0; j < frag.length; j++) {
					if ((frag[j] != null) && (frag[j].getScore() > 0)) {
						snippet += frag[j].toString().trim() + "... ";
					}
				}
				Result result = new Result();
				result.setTitle(doc.get("title").trim());
				result.setUrl(doc.get("url").trim());
				result.setSnippet(snippet);
				results.add(result);
			}
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				manager.release(searcher);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	public List<Result> getResults(ScoreDoc[] docs, String field) {
		return this.getResults(docs, 0, docs.length);
	}

}
