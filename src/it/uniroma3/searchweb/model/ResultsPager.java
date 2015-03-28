package it.uniroma3.searchweb.model;

import java.util.List;

import it.uniroma3.searchweb.engine.searcher.ResultsExtractor;
import org.apache.lucene.search.ScoreDoc;

public class ResultsPager {
	private ResultsExtractor extractor;
	private ScoreDoc[] docs;
	private int resultsPerPage = 10;
	private String field;
	
	public ResultsPager(ResultsExtractor extractor, ScoreDoc[] docs) {
		this.extractor = extractor;
		this.docs = docs;
	}
	
	public void setDocs(ScoreDoc[] docs) {
		this.docs = docs;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public List<Result> getPage(int i) {
		if (i<1)
			return null;
		
		i = i - 1;
		if (i * this.resultsPerPage > this.docs.length)
			return null;
		
		int start = i * this.resultsPerPage;
		int end = start + this.resultsPerPage;
		
		if (end > this.docs.length)
			end = this.docs.length;
		
		List<Result> results;
		results = this.extractor.getResults(this.docs, field, start, end);
		
		return results;
	}
	
	public int getPages() {
		return (int) Math.ceil(this.docs.length / (this.resultsPerPage + 0.0));
	}

}
