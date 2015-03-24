package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SpellCheckers {
	public static void main(String[] args) throws Exception {
		EngineConfig engineConfig = EngineConfig.getInstance();
		File dir = new File(engineConfig.getSpellCheckerPath());
		Directory directory = FSDirectory.open(dir);
		SpellChecker spellChecker = new SpellChecker(directory);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellChecker.indexDictionary(new PlainTextDictionary(new File("dictionary/dictionary.txt")),config,true);
		String wordForSuggestions = "phobe";
		int suggestionsNumber = 1;
		if (spellChecker.exist(wordForSuggestions)) {
			System.out.println("ok");
		} else {
			String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
			if (suggestions!=null && suggestions.length>0) {
				for (String word : suggestions) {
					System.out.println("Did you mean:" + word);
				}
			} else {
				System.out.println("No suggestions found for word:"+wordForSuggestions);
			}
		}
		spellChecker.close();
	}

}
