package it.uniroma3.searchweb.engine;

import it.uniroma3.searchweb.model.Result;

import java.util.List;

public interface SearchEngine {
	
	public List<Result> getResults(String query);

}
