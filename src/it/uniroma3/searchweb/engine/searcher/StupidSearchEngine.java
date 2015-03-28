package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.model.QueryResults;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
	
	public StupidSearchEngine() {
		super();

		try {
			EngineConfig engineConfig = this.getConfig();
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			this.setSearcher(searcher);
			// TODO set spellchecker? we should reuse the same spellchecker instance
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
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(Version.LUCENE_46, fields, analyzer);
		mfqp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query q = mfqp.parse(query);
		return q;
	}

	@Override
	public ScoreDoc[] search(Query query) throws IOException {
		int maxHits = this.getConfig().getMaxHits();
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
		TopScoreDocCollector.create(maxHits, true);
		this.getSearcher().search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
	}

	@Override
	public ResultsExtractor getExtractor(IndexSearcher s, Analyzer a, Query q,
			String snippetField) {
		ResultsExtractor e = new ResultsExtractor(s, a, q, snippetField);
		return e;
	}
	
	public QueryResults searchForBetterQuery(String query,QueryResults queryResults) throws IOException, ParseException {
		ScoreDoc[] newHits;
		NaiveSpellCheckers spellChecker = new NaiveSpellCheckers();
		Query tmp;
		List<String> corrections = spellChecker.getBasicSuggestions(query, 
				this.getConfig().getMaxCorrection(), this.getConfig().getSimilarity());
		for (int i=0; i<corrections.size();i++) {
			tmp = this.parseQuery(queryResults.getFields(), getAnalyzer(), corrections.get(i));
			newHits = this.search(tmp);
			if (queryResults.getDocs().length<newHits.length) {
				queryResults.setDocs(newHits);
				queryResults.setQuery(tmp);
			}
		}
		return queryResults;
	}
	
}
