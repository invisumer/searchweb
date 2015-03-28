package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.model.ResultsPager;

public interface SearchEngine {
	
	public ResultsPager getResults(String query, String[] fields, String lang);

}
