package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.AnalyzerMapper;
import it.uniroma3.searchweb.engine.mapper.SearcherMapper;
import it.uniroma3.searchweb.engine.mapper.SpellCheckerMapper;
import it.uniroma3.searchweb.model.QueryResults;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopScoreDocCollector;

public class StupidSearchEngine extends DebuggerSearchEngine {
	private static final Logger logger = Logger.getLogger(StupidSearchEngine.class.getName());
	private SearcherMapper searcherMapper;
	private AnalyzerMapper analyzerMapper;
	private SpellCheckerEngine spellCheckers;
	
	public StupidSearchEngine() {
		super();
		
		try {
			this.analyzerMapper = new AnalyzerMapper();
			this.searcherMapper = new SearcherMapper();
			SpellCheckerMapper mapper = new SpellCheckerMapper();
			this.spellCheckers = new NaiveSpellChecker(mapper);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@Override
	public SearcherManager getSearcherManager(String contentType) {
		SearcherManager manager = this.searcherMapper.pickSearcher(contentType);
		return manager;
	}

	@Override
	public Analyzer getAnalyzer(String lang) {
		return this.analyzerMapper.pickAnalyzer(lang);
	}
	
	@Override
	public QueryResults makeQuery(String stringQuery, String[] fields, Analyzer analyzer, SearcherManager manager, 
			boolean spellCheckerEnabled, String lang)
			throws IOException, ParseException {
		EngineConfig config = EngineConfig.getInstance();
		Query query  = this.parseAndQuery(fields, analyzer, stringQuery, lang);
		ScoreDoc[] docs = this.search(manager, query);
		QueryResults queryResults = new QueryResults(query, docs, fields, stringQuery);
		if (docs.length>=config.getScoreThreshold()) {
			queryResults.setQueryExecuted(stringQuery);
			return queryResults;
		}
		boolean flag = true;
		if (!stringQuery.contains("\"") && spellCheckerEnabled) {
			queryResults = this.searchForBetterQuery(manager, stringQuery, queryResults, flag, lang);
			if (queryResults.getDocs().length>=config.getScoreThreshold()) {
				queryResults.setSuggestionOccurred(true);
				return queryResults;
			}
			flag = false;
			queryResults = this.searchForBetterQuery(manager, stringQuery, queryResults, flag, lang);
			if (queryResults.getDocs().length>=config.getScoreThreshold()) {
				if (!queryResults.getQueryExecuted().equals(queryResults.getStartQuery()))
					queryResults.setSuggestionOccurred(true);
				return queryResults;
			}
		}
		
		query = this.parseOrQuery(fields, analyzer, stringQuery, lang);
		docs = this.search(manager, query);
		queryResults.setDocs(docs);
		queryResults.setQuery(query);
		queryResults.setQueryExecuted(stringQuery);
		return queryResults;
	}
	
	public Query parseOrQuery(String[] fields, Analyzer analyzer, String query, String lang) throws ParseException {
		HashMap<String,Float> boosts = new HashMap<String,Float>();
		boosts.put("lang", 100f);
		Map<String, Analyzer> map = new HashMap<String, Analyzer>();
		map.put("domain", new KeywordAnalyzer());
		map.put("domain2", new KeywordAnalyzer());
		map.put("title", analyzer);
		map.put("body", analyzer);
		map.put("lang", new KeywordAnalyzer());
		PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(analyzer, map);
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzerWrapper, boosts);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query q = mfqp.parse(query + " lang:" + lang);
		return q;
	}

	public Query parseAndQuery(String[] fields, Analyzer analyzer, String query, String lang) throws ParseException {
		HashMap<String,Float> boosts = new HashMap<String,Float>();
		boosts.put("lang", 100f);
		Map<String, Analyzer> map = new HashMap<String, Analyzer>();
		map.put("domain", new KeywordAnalyzer());
		map.put("domain2", new KeywordAnalyzer());
		map.put("title", analyzer);
		map.put("body", analyzer);
		map.put("lang", new KeywordAnalyzer());
		PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(analyzer, map);
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzerWrapper, boosts);
		mfqp.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query q = mfqp.parse(query + " lang:" + lang);
		return q;
	}

	public ScoreDoc[] search(SearcherManager manager, Query query) throws IOException {
		int maxHits = this.getConfig().getMaxHits();
		IndexSearcher searcher = manager.acquire();
		try {
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
		} finally {
			manager.release(searcher);
		}
	}
	
	public QueryResults searchForBetterQuery(SearcherManager manager, String query, QueryResults queryResults, boolean flag, String lang) 
			throws IOException, ParseException {
		ScoreDoc[] newHits;
		Query tmp;
		List<String> corrections = spellCheckers.getSuggestions(query);
		for (int i=0; i<corrections.size();i++) {
			if (flag)
				tmp = this.parseAndQuery(queryResults.getFields(), getAnalyzer(lang), corrections.get(i), lang);
			else
				tmp = this.parseOrQuery(queryResults.getFields(), getAnalyzer(lang), corrections.get(i), lang);
			newHits = this.search(manager, tmp);
			if (queryResults.getDocs().length<newHits.length) {
				String q = corrections.get(i);
				if (q.endsWith(" "));
					q = q.substring(0, q.length()-1);
				queryResults.setDocs(newHits);
				queryResults.setQuery(tmp);
				queryResults.setQueryExecuted(q);
			}
		}
		
		return queryResults;
	}

	@Override
	public ResultsExtractor getExtractor(SearcherManager manager, Analyzer a, Query q,
			String snippetField) {
		ResultsExtractor e = new ResultsExtractor(manager, a, q, snippetField);
		return e;
	}
	
}