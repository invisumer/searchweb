package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;

public interface SpellCheckers {
	
	public void initialize(String lang) throws CorruptIndexException, IOException;
	public List<String> getBasicSuggestions(String query, String lang) throws IOException;
}
