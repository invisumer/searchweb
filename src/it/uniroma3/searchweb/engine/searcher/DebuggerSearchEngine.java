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

public abstract class DebuggerSearchEngine implements SearchEngine {
	private static final Logger logger = Logger.getLogger(DebuggerSearchEngine.class.getName());
	private static int NUM_EXPLANATIONS = 10;
	private static String SNIPPET_FIELD = "body";
	private EngineConfig config = EngineConfig.getInstance();
	private IndexSearcher searcher = null;
	private boolean debugMode = false;
	
	public DebuggerSearchEngine() {
		this.debugMode = this.config.isDebugMode();
		NUM_EXPLANATIONS = config.getNumTopScoreExplantion();
	}

	@Override
	public ResultsPager getResults(String stringQuery, String[] fields) {
		ResultsPager pager = null;
		
		try {
			Analyzer analyzer = this.getAnalyzer();
			Query query  = this.parseQuery(fields, analyzer, stringQuery);
			ScoreDoc[] docs = this.search(query);
			
			QueryResults queryResults = new QueryResults(query, docs, fields);
			if (docs.length<config.getScoreThreshold())
				queryResults = this.searchForBetterQuery(stringQuery, queryResults); 
			
			ResultsExtractor e = this.getExtractor(this.getSearcher(), 
					analyzer, queryResults.getQuery(), SNIPPET_FIELD);
			pager = new ResultsPager(e, queryResults.getDocs());
			
			if (debugMode)
				this.explain(queryResults.getQuery(), queryResults.getDocs());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (ParseException e) {
			logger.severe(e.getMessage());
		}
			
		return pager;
	}
	
	public abstract Analyzer getAnalyzer();
	
	public abstract Query parseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException;
	
	public abstract ScoreDoc[] search(Query query) throws IOException;
	
	public abstract ResultsExtractor getExtractor(IndexSearcher s, Analyzer a, Query q, String snippetField);

	public abstract QueryResults searchForBetterQuery(String query, QueryResults queryResults) throws IOException, ParseException;
	
	public void explain(Query query, ScoreDoc[] hits) throws IOException {
		for (int i=0; i<NUM_EXPLANATIONS && i< hits.length; i++) {
			Explanation expl = this.searcher.explain(query, hits[i].doc);
			logger.info("Match " + (i+1) + " explanation:\n" + expl.toString());
		}
	}

	public IndexSearcher getSearcher() {
		return searcher;
	}
	
	public void setSearcher(IndexSearcher searcher) throws IOException {
		this.searcher = searcher;
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
