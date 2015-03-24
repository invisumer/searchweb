package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.model.Result;

import java.util.List;

public interface SearchEngine {
	
	public List<Result> getResults(String query, String[] fields);

}
