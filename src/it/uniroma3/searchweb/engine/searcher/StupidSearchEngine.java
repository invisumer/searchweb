package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
	
	public StupidSearchEngine(boolean debugMode, int topScores) {
		super(debugMode, topScores);

		try {
			EngineConfig engineConfig = EngineConfig.getInstance();
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			this.setSearcher(searcher);
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
		QueryParser qp = new QueryParser(Version.LUCENE_46, "body", analyzer);
		Query q = qp.parse(query);
		return q;
	}

	@Override
	public ScoreDoc[] search(Query query) throws IOException {
		int maxHits = 10;
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		this.getSearcher().search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		return hits;
	}

	@Override
	public List<Result> extract(Analyzer a, Query q, ScoreDoc[] hits,
			String field) {
		ResultsExtractor extractor = new ResultsExtractor(this.getSearcher(), a, q);
		return extractor.getResults(hits, field);
	}
	
}
