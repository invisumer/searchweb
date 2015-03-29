package it.uniroma3.searchweb.model;

import java.util.List;

import it.uniroma3.searchweb.engine.searcher.ResultsExtractor;
import org.apache.lucene.search.ScoreDoc;

public class ResultsPager {
	private static int RESULT_PER_PAGE = 10;
	private ResultsExtractor extractor;
	private ScoreDoc[] docs;
	private String suggestion; // TODO if there were no suggestion, it is ""
	
	public ResultsPager(ResultsExtractor extractor, ScoreDoc[] docs, String suggestion) {
		this.extractor = extractor;
		this.docs = docs;
		this.suggestion = suggestion;
	}
	
	public List<Result> getPage(int i) {
		if (i<1)
			return null;
		
		i = i - 1;
		if (i * RESULT_PER_PAGE > this.docs.length)
			return null;
		
		int start = i * RESULT_PER_PAGE;
		int end = start + RESULT_PER_PAGE;
		
		if (end > this.docs.length)
			end = this.docs.length;
		
		List<Result> results;
		results = this.extractor.getResults(this.docs, start, end);
		
		return results;
	}
	
	public int getPages() {
		return (int) Math.ceil(this.docs.length / (RESULT_PER_PAGE + 0.0));
	}
	
	public ResultsExtractor getExtractor() {
		return extractor;
	}
	
	public void setExtractor(ResultsExtractor extractor) {
		this.extractor = extractor;
	}
	
	public ScoreDoc[] getDocs() {
		return docs;
	}
	
	public void setDocs(ScoreDoc[] docs) {
		this.docs = docs;
	}

}
