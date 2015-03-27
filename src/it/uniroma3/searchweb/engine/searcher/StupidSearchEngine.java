package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class StupidSearchEngine extends DebuggerSearchEngine {
	private static final Logger logger = Logger.getLogger(StupidSearchEngine.class.getName());
	private EngineConfig config = EngineConfig.getInstance();
//	private SpellCheckers spellChecker;
	
	public StupidSearchEngine() {
		super();

		try {
			EngineConfig engineConfig = this.getConfig();
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			this.setSearcher(searcher);
//			this.spellChecker = new SpellCheckers();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}

	}

	@Override
	public Analyzer getAnalyzer() {
		return new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
	}

	@Override
	public Query parseQuery(String[] fields, Analyzer analyzer, String query) throws ParseException {
		//QueryParser qp = new QueryParser(Version.LUCENE_46, "body", analyzer);
		//Query q = qp.parse(query);
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(Version.LUCENE_46, fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query q = mfqp.parse(query);
		return q;
	}

	@Override
	public ScoreDoc[] search(Query query) throws IOException {
		int maxHits = config.getMaxHits();
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		this.getSearcher().search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
//		if (hits.length<config.getScoreThreshold()) {
//			System.out.println("correction");
//			String[] corrections = spellChecker.getBasicSuggestions(query.toString().substring(5), config.getMaxCorrection(), .75f);
//			for (String q : corrections) {
//				Query newQuery = null;
//				try {
//					newQuery = this.parseQuery(new String[3], getAnalyzer(), q);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				collector = TopScoreDocCollector.create(maxHits, true);
//				this.getSearcher().search(newQuery, collector);
//				ScoreDoc[] newHits = collector.topDocs().scoreDocs;
//				if (newHits.length>hits.length) {
//					hits = newHits;
//				}
//			}
//		}
		return hits;
	}

	@Override
	public List<Result> extract(Analyzer a, Query q, ScoreDoc[] hits,
			String field) {
		ResultsExtractor extractor = new ResultsExtractor(this.getSearcher(), a, q);
		return extractor.getResults(hits, field);
	}
	
}
