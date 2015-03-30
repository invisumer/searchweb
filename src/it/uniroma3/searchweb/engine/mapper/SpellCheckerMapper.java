package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SpellCheckerMapper {
	private SpellChecker spellChecker;
	
	public SpellCheckerMapper() throws IOException {
		this.open();
	}
	
	private void open() throws IOException	{
		EngineConfig engineConfig = EngineConfig.getInstance();
		Directory spellCheckerIndexDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/index"));
		this.spellChecker = new SpellChecker(spellCheckerIndexDir);
	}
	
	public SpellChecker getSpellChecker() {
		return this.spellChecker;
	}
}
