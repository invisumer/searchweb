package it.uniroma3.searchweb.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public class QueryResults {
	private Query query;
	private ScoreDoc[] docs;
	private String[] fields;
	
	public QueryResults(Query query, ScoreDoc[] docs, String[] fields) {
		this.query = query;
		this.docs = docs;
		this.fields = fields;
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
	
	public String QueryToString() {
		Set<Term> terms = new HashSet<Term>();
		query.extractTerms(terms);
		String result = "";
		int i=0;
		for (Term t : terms) {
			if (i%2==0)
				result = result.concat(t.text()+ " ");
			i++;
		}
		return result;
	}
}
