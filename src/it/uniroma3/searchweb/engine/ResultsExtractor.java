package it.uniroma3.searchweb.engine;

import it.uniroma3.searchweb.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;

public class ResultsExtractor {
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private Query query;

	public ResultsExtractor(IndexSearcher searcher, Analyzer analyzer,
			Query query) {
		this.searcher = searcher;
		this.analyzer = analyzer;
		this.query = query;
	}

	public List<Result> getResults(ScoreDoc[] docs, String field) {
		List<Result> results = new ArrayList<Result>();

		try {
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<b>", "</b>");
			Highlighter highlighter = new Highlighter(htmlFormatter,
					new QueryScorer(query));
			
			for (int i = 0; i < docs.length; i++) {
				int id = docs[i].doc;
				Document doc = searcher.doc(id);
				String text = doc.get(field);
				TokenStream tokenStream = TokenSources.getAnyTokenStream(
						searcher.getIndexReader(), id, field, analyzer);
				TextFragment[] frag = highlighter.getBestTextFragments(
						tokenStream, text, false, 2);
				String snippet = "";
				for (int j = 0; j < frag.length; j++) {
					if ((frag[j] != null) && (frag[j].getScore() > 0)) {
						snippet += frag[j].toString() + "... ";
					}
				}
				
				Result result = new Result(doc.get("title"), doc.get("url"), null, snippet);
				results.add(result);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}

}
