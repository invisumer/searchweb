package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import it.uniroma3.searchweb.model.ResultsPager;

public class PagingSearchEngine extends StupidSearchEngine {
	private static final Logger logger = Logger.getLogger(StupidSearchEngine.class.getName());
	
	public PagingSearchEngine() {
		super();
	}
	
	public ResultsPager getPager(String query, String[] fields) {
		ResultsPager pager = null;
		
		try {
			Analyzer analyzer = this.getAnalyzer();
			Query queryx  = this.parseQuery(fields, analyzer, query);
			ScoreDoc[] docs = this.search(queryx);
			if (docs.length<this.getConfig().getScoreThreshold()) {
				docs = this.searchForBetterQuery(docs, query, fields); 
			}
			
			// TODO change body field
			ResultsExtractor e = new ResultsExtractor(this.getSearcher(), analyzer, queryx);
			pager = new ResultsPager(e, docs);
			// TODO set configurations
			pager.setDocs(docs);
			pager.setField("body");
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (ParseException e) {
			logger.severe(e.getMessage());
		}
			
		return pager;
	}

}
