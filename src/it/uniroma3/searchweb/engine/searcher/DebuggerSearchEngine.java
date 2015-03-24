package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.model.Result;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public abstract class DebuggerSearchEngine implements SearchEngine {
	private static final Logger logger = Logger.getLogger(DebuggerSearchEngine.class.getName());
	private static int TOP_SCORES = 10;
	private EngineConfig config = EngineConfig.getInstance();
	private IndexSearcher searcher = null;
	private boolean debugMode = false;
	
	public DebuggerSearchEngine() {
		this.debugMode = this.config.isDebugMode();
		TOP_SCORES = config.getNumTopScoreExplantion();
	}

	@Override
	public List<Result> getResults(String query, String[] fields) {
		List<Result> results = null;
		
		try {
			Analyzer analyzer = this.getAnalyzer();
			Query queryx  = this.parseQuery(fields, analyzer, query);
			ScoreDoc[] docs = this.search(queryx);
			// TODO change body field
			results = this.extract(analyzer, queryx, docs, "body");
			
			if (debugMode)
				this.explain(queryx, docs);
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (ParseException e) {
			logger.severe(e.getMessage());
		}
			
		return results;
	}
	
	public abstract Analyzer getAnalyzer();
	
	public abstract Query parseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException;
	
	public abstract ScoreDoc[] search(Query query) throws IOException;
	
	public abstract List<Result> extract(Analyzer a, Query q, ScoreDoc[] hits, String field);
	
	public void explain(Query query, ScoreDoc[] hits) throws IOException {
		for (int i=0; i<TOP_SCORES && i< hits.length; i++) {
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

}
