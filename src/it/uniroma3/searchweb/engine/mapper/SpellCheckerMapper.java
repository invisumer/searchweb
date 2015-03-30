package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SpellCheckerMapper {
	private Map<String, SpellChecker> spellCheckers;
	
	public SpellCheckerMapper() throws IOException {
		this.spellCheckers = new HashMap<String, SpellChecker>();
		this.open();
	}
	
	private void open() throws IOException	{
		EngineConfig engineConfig = EngineConfig.getInstance();
		Directory spellCheckerDictionaryDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-en"));
		Directory spellCheckerIndexDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/index"));
		SpellChecker spellcheckerIndex = new SpellChecker(spellCheckerIndexDir);
		spellCheckers.put("index", spellcheckerIndex);
		SpellChecker englishSpellChecker = new SpellChecker(spellCheckerDictionaryDir);
		spellCheckers.put("en", englishSpellChecker);
		spellCheckerDictionaryDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-it"));
		SpellChecker italianSpellChecker = new SpellChecker(spellCheckerDictionaryDir);
		spellCheckers.put("it", italianSpellChecker);
		spellCheckerDictionaryDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-fr"));
		SpellChecker frenchSpellChecker = new SpellChecker(spellCheckerDictionaryDir);
		spellCheckers.put("fr", frenchSpellChecker);
		spellCheckerDictionaryDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-de"));
		SpellChecker germanSpellChecker = new SpellChecker(spellCheckerDictionaryDir);
		spellCheckers.put("de", germanSpellChecker);
		spellCheckerDictionaryDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-es"));
		SpellChecker spanishSpellChecker = new SpellChecker(spellCheckerDictionaryDir);
		spellCheckers.put("es", spanishSpellChecker);
	}
	
	public SpellChecker getSpellChecker(String key) {
		return this.spellCheckers.get(key);
	}
}
