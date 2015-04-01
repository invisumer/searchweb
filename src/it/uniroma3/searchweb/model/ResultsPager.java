package it.uniroma3.searchweb.model;

import java.util.List;

import it.uniroma3.searchweb.engine.searcher.ResultsExtractor;

import org.apache.lucene.search.ScoreDoc;

public class ResultsPager {
	private static int RESULT_PER_PAGE = 10;
	private ResultsExtractor extractor;
	private ScoreDoc[] docs;
	private String originalQuery;
	private String executedQuery;
	private boolean queryCorrected;
	
	public ResultsPager(ResultsExtractor extractor, ScoreDoc[] docs, String startQuery, String queryExecuted, boolean suggestionOccurred) {
		this.extractor = extractor;
		this.docs = docs;
		this.setOriginalQuery(startQuery);
		this.setExecutedQuery(queryExecuted);
		this.setQueryCorrected(suggestionOccurred);
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

	public String getExecutedQuery() {
		return executedQuery;
	}
	
	public void setExecutedQuery(String executedQuery) {
		this.executedQuery = executedQuery;
	}
	
	public String getOriginalQuery() {
		return originalQuery;
	}
	
	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}
	
	public boolean isQueryCorrected() {
		return queryCorrected;
	}
	
	public void setQueryCorrected(boolean queryCorrected) {
		this.queryCorrected = queryCorrected;
	}

}
