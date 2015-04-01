package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.model.QueryResults;
import it.uniroma3.searchweb.model.ResultsPager;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;

public abstract class DebuggerSearchEngine implements SearchEngine {
	private static final Logger logger = Logger.getLogger(DebuggerSearchEngine.class.getName());
	private static int NUM_EXPLANATIONS = 10;
	private static String SNIPPET_FIELD = "body";
	private EngineConfig config = EngineConfig.getInstance();
	private boolean debugMode = false;
	
	public DebuggerSearchEngine() {
		this.debugMode = this.config.isDebugMode();
		NUM_EXPLANATIONS = config.getNumTopScoreExplantion();
	}

	@Override
	public ResultsPager getResults(String stringQuery, String[] fields, String contentType, String lang, boolean spellCheckerEnabled) {
		ResultsPager pager = null;
		
		try {
			SearcherManager manager = this.getSearcherManager(contentType);
			Analyzer analyzer = this.getAnalyzer(lang);
			QueryResults queryResults = this.makeQuery(stringQuery, fields, analyzer, manager, lang, spellCheckerEnabled);
			ResultsExtractor e = this.getExtractor(manager, 
					analyzer, queryResults.getQuery(), SNIPPET_FIELD);
			pager = new ResultsPager(e, queryResults.getDocs(),queryResults.getStartQuery(),queryResults.getQueryExecuted(),queryResults.isSuggestionOccurred());
			if (debugMode) {
				IndexSearcher searcher = manager.acquire();
				try {
					logger.info("Selected searcher: " + searcher.getIndexReader().numDocs());
					logger.info("Selected analyzer: " + analyzer.getClass().getName());
					this.explain(searcher, queryResults.getQuery(), queryResults.getDocs());
				} finally  {
					manager.release(searcher);
				}
			}
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (ParseException e) {
			logger.severe(e.getMessage());
		}
			
		return pager;
	}
	
	public abstract SearcherManager getSearcherManager(String lang);
	
	public abstract Analyzer getAnalyzer(String lang);
	
	public abstract QueryResults makeQuery(String query, String[] fields, Analyzer analyzer, SearcherManager manager, 
			String lang, boolean spellCheckerEnabled) throws IOException, ParseException;
	
	public abstract ResultsExtractor getExtractor(SearcherManager manager, Analyzer a, Query q, String snippetField);
	
	public void explain(IndexSearcher searcher, Query query, ScoreDoc[] hits) throws IOException {
		for (int i=0; i<NUM_EXPLANATIONS && i< hits.length; i++) {
			Explanation expl = searcher.explain(query, hits[i].doc);
			logger.info("Match " + (i+1) + " explanation:\n" + expl.toString());
		}
	}
	
	public EngineConfig getConfig() {
		return config;
	}
	
	public void setConfig(EngineConfig config) {
		this.config = config;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

}
