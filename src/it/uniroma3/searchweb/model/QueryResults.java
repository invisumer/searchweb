package it.uniroma3.searchweb.model;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public class QueryResults {
	private Query query;
	private ScoreDoc[] docs;
	private String[] fields;
	private String startQuery;
	private String queryExecuted;
	private boolean suggestionOccurred;
	
	public QueryResults(Query query, ScoreDoc[] docs, String[] fields, String startQuery) {
		this.query = query;
		this.docs = docs;
		this.fields = fields;
		this.startQuery = startQuery;
		this.suggestionOccurred = false;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public ScoreDoc[] getDocs() {
		return docs;
	}

	public void setDocs(ScoreDoc[] docs) {
		this.docs = docs;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getStartQuery() {
		return startQuery;
	}

	public void setStartQuery(String startQuery) {
		this.startQuery = startQuery;
	}

	public String getQueryExecuted() {
		return queryExecuted;
	}

	public void setQueryExecuted(String queryExecuted) {
		this.queryExecuted = queryExecuted;
	}

	public boolean isSuggestionOccurred() {
		return suggestionOccurred;
	}

	public void setSuggestionOccurred(boolean hasSuggestion) {
		this.suggestionOccurred = hasSuggestion;
	}
}
