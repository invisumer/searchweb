package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.AnalyzerMapper;
import it.uniroma3.searchweb.engine.mapper.SearcherMapper;
import it.uniroma3.searchweb.engine.mapper.SpellCheckerMapper;
import it.uniroma3.searchweb.model.QueryResults;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class StupidSearchEngine extends DebuggerSearchEngine {
	private static final Logger logger = Logger.getLogger(StupidSearchEngine.class.getName());
	private SearcherMapper searcherMapper;
	private AnalyzerMapper analyzerMapper;
	private SpellCheckers spellCheckers;
	
	public StupidSearchEngine() {
		super();
		
		try {
			this.analyzerMapper = new AnalyzerMapper();
			this.searcherMapper = new SearcherMapper();
			SpellCheckerMapper mapper = new SpellCheckerMapper();
			this.spellCheckers = new NaiveSpellCheckers(mapper);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@Override
	public IndexSearcher getSearcher(String contentType) {
		return this.searcherMapper.pickSearcher(contentType);
	}

	@Override
	public Analyzer getAnalyzer(String lang) {
		return this.analyzerMapper.pickAnalyzer(lang);
	}
	
	@Override
	public QueryResults makeQuery(String stringQuery, String[] fields, Analyzer analyzer, IndexSearcher searcher, String lang) throws IOException, ParseException {
		EngineConfig config = EngineConfig.getInstance();
		Query query  = this.parsePhraseQuery(fields, analyzer, stringQuery);
		ScoreDoc[] docs = this.search(searcher, query);
		QueryResults queryResults = new QueryResults(query, docs, fields,lang, stringQuery);
		if (docs.length>=config.getScoreThreshold()) {
			queryResults.setQueryExecuted(stringQuery);
			return queryResults;
		}
		boolean flag = true;
		if (!stringQuery.contains("\"")) {
			queryResults = this.searchForBetterQuery(searcher, stringQuery, queryResults, flag);
			if (queryResults.getDocs().length>=config.getScoreThreshold()) {
				queryResults.setHasSuggestion(true);
				return queryResults;
			}
		}
		query = this.parseQuery(fields, analyzer, stringQuery);
		docs = this.search(searcher, query);
		if (docs.length>=config.getScoreThreshold()) {
			queryResults.setDocs(docs);
			queryResults.setQuery(query);
			queryResults.setQueryExecuted(stringQuery);
			return queryResults;
		}
		flag = false;
		if (!stringQuery.contains("\"")) {
			queryResults.setHasSuggestion(true);
			queryResults = this.searchForBetterQuery(searcher, stringQuery, queryResults, flag);
		}
		return queryResults;
	}
	
	public Query parseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException {
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query q = mfqp.parse(query);
		return q;
	}

	public Query parsePhraseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException {
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query q = mfqp.parse(query);
		return q;
	}

	public ScoreDoc[] search(IndexSearcher searcher, Query query) throws IOException {
		int maxHits = this.getConfig().getMaxHits();
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
	}
	
	public QueryResults searchForBetterQuery(IndexSearcher searcher, String query, QueryResults queryResults, boolean flag) 
			throws IOException, ParseException {
		ScoreDoc[] newHits;
		Query tmp;
		List<String> corrections = spellCheckers.getSuggestions(query); //TODO change this value
		for (int i=0; i<corrections.size();i++) {
			if (flag)
				tmp = this.parsePhraseQuery(queryResults.getFields(), getAnalyzer(queryResults.getLang()), corrections.get(i));
			else
				tmp = this.parseQuery(queryResults.getFields(), getAnalyzer(queryResults.getLang()), corrections.get(i));
			newHits = this.search(searcher, tmp);
			if (queryResults.getDocs().length<newHits.length) {
				queryResults.setDocs(newHits);
				queryResults.setQuery(tmp);
				queryResults.setQueryExecuted(corrections.get(i));
			}
		}
		
		return queryResults;
	}

	@Override
	public ResultsExtractor getExtractor(IndexSearcher s, Analyzer a, Query q,
			String snippetField) {
		ResultsExtractor e = new ResultsExtractor(s, a, q, snippetField);
		return e;
	}
	
}