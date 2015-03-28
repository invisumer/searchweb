package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;

public interface SpellCheckers {
	
	public void initialize() throws CorruptIndexException, IOException;
	public List<String> getBasicSuggestions(String query, int numSug, float similarity) throws IOException;
}
