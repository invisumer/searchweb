package it.uniroma3.searchweb.engine.mapper;

import java.io.IOException;

import org.apache.lucene.search.spell.DirectSpellChecker;

public class SpellCheckerMapper {
	private DirectSpellChecker spellChecker;
	
	public SpellCheckerMapper() throws IOException {
		this.spellChecker = new DirectSpellChecker();
	}
	
	public DirectSpellChecker getSpellChecker() {
		return this.spellChecker;
	}
}
