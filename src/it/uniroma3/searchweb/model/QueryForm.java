package it.uniroma3.searchweb.model;

import javax.validation.constraints.NotNull;

public class QueryForm {
	@NotNull
	private String query;
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
}
