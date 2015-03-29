package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.AnalyzerMapper;
import it.uniroma3.searchweb.engine.mapper.SearcherMapper;
import it.uniroma3.searchweb.model.QueryResults;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class StupidSearchEngine extends DebuggerSearchEngine {
	private SearcherMapper searcherMapper;
	private AnalyzerMapper mapper;
	
	public StupidSearchEngine() {
		super();

		// TODO set spellchecker? we should reuse the same spellchecker instance
		this.mapper = new AnalyzerMapper();
		this.searcherMapper = new SearcherMapper();
	}
	
	@Override
	public IndexSearcher getSearcher(String lang) {
		return this.searcherMapper.pickSearcher(lang);
	}

	@Override
	public Analyzer getAnalyzer(String lang) {
		return this.mapper.pickAnalyzer(lang);
	}
	
	@Override
	public Query parseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException {
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query q = mfqp.parse(query);
		return q;
	}

	@Override
	public Query parsePhraseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException {
		BooleanQuery bq = new BooleanQuery();
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(EngineConfig.getVersion(), fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		for (String field : fields) {
			Query q = mfqp.createPhraseQuery(field, query);
			bq.add(q, Occur.SHOULD);
		}
		return bq;
	}

	@Override
	public ScoreDoc[] search(IndexSearcher searcher, Query query) throws IOException {
		int maxHits = this.getConfig().getMaxHits();
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
	}

	@Override
	public ResultsExtractor getExtractor(IndexSearcher s, Analyzer a, Query q,
			String snippetField) {
		ResultsExtractor e = new ResultsExtractor(s, a, q, snippetField);
		return e;
	}
	
	@Override
	public QueryResults makeQuery(String stringQuery, String[] fields, Analyzer analyzer, IndexSearcher searcher, String lang) throws IOException, ParseException {
		EngineConfig config = EngineConfig.getInstance();
		
		Query query  = this.parsePhraseQuery(fields, analyzer, stringQuery);
		ScoreDoc[] docs = this.search(searcher, query);
		QueryResults queryResults = new QueryResults(query, docs, fields,lang);

		if (docs.length>=config.getScoreThreshold())
			return queryResults;
		boolean flag = true;
		queryResults = this.searchForBetterQuery(searcher, stringQuery, queryResults, flag);
		if (queryResults.getDocs().length>=config.getScoreThreshold())
			return queryResults;
		query = this.parseQuery(fields, analyzer, stringQuery);
		docs = this.search(searcher, query);
		if (docs.length>=config.getScoreThreshold()) {
			queryResults.setDocs(docs);
			queryResults.setQuery(query);
			return queryResults;
		}
		flag = false;
		
		queryResults = this.searchForBetterQuery(searcher, stringQuery, queryResults, flag);
		return queryResults;
	}
	
	public QueryResults searchForBetterQuery(IndexSearcher searcher, String query, QueryResults queryResults, boolean flag) 
			throws IOException, ParseException {
		ScoreDoc[] newHits;
		NaiveSpellCheckers spellChecker = new NaiveSpellCheckers();
		Query tmp;
		List<String> corrections = spellChecker.getBasicSuggestions(query, "en"); //TODO change this value
		for (int i=0; i<corrections.size();i++) {
			if (flag)
				tmp = this.parsePhraseQuery(queryResults.getFields(), getAnalyzer(queryResults.getLang()), corrections.get(i));
			else
				tmp = this.parseQuery(queryResults.getFields(), getAnalyzer(queryResults.getLang()), corrections.get(i));
			newHits = this.search(searcher, tmp);
			if (queryResults.getDocs().length<newHits.length) {
				queryResults.setDocs(newHits);
				queryResults.setQuery(tmp);
			}
		}
		return queryResults;
	}
	
}