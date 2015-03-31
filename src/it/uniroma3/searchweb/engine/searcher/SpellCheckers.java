package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;

public interface SpellCheckers {
	
	public List<String> getSuggestions(String query) throws IOException;
}
