package it.uniroma3.searchweb.engine;

import it.uniroma3.searchweb.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class StupidSearchEngine implements SearchEngine {
	
	@Override
	public List<Result> getResults(String query) {
		List<Result> results = null;
		
		try {
			/* create the index in the pathToFolder or in RAM (choose one) */
			Directory index = FSDirectory.open(new File("/home/redox/index"));
			/* create a standard analyzer */
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
			/* set the maximum number of results */
			int maxHits = 10;
			/* open a directory reader and create searcher and topdocs */
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
			TopScoreDocCollector.create(maxHits, true);
			/* create the query parser */
			QueryParser qp = new QueryParser(Version.LUCENE_46, "body", analyzer);
			/* query string */
			Query q = qp.parse(query);
			/* search into the index */
			searcher.search(q, collector);
			
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			ResultsExtractor extractor = new ResultsExtractor(searcher, analyzer, q);
			results = extractor.getResults(hits, "body");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
	
}
