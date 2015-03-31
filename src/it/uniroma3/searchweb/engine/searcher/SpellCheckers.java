package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

public interface SpellCheckers {
	
	public List<String> getSuggestions(String query) throws IOException;
}
