package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SpellCheckers {
	Logger logger;
	EngineConfig engineConfig;
	
	public SpellCheckers() {
		engineConfig = EngineConfig.getInstance();
	}
	
	public String[] PopulateLuceneDictionary() throws CorruptIndexException, IOException {
		Directory spellCheckerDir = FSDirectory.open(new File(engineConfig.getSpellCheckerPath()));
		SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
		Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
		IndexReader reader = DirectoryReader.open(index);
		// To index a field of a user index:
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellchecker.indexDictionary(new LuceneDictionary(reader, "body"),config,true);
		// To index a file containing words:
		spellchecker.indexDictionary(new PlainTextDictionary(new File("dictionary/dictionary.txt")),config,true);
		String[] suggestions = spellchecker.suggestSimilar("misspelt", 5);
		return suggestions;
	}
	
	public static void main(String[] args) throws Exception {
		EngineConfig engineConfig = EngineConfig.getInstance();
		File dir = new File(engineConfig.getSpellCheckerPath());
		Directory directory = FSDirectory.open(dir);
		SpellChecker spellChecker = new SpellChecker(directory);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellChecker.indexDictionary(new PlainTextDictionary(new File("dictionary/dictionary.txt")),config,true);
		String wordForSuggestions = "ronaldo";
		int suggestionsNumber = 1;
		if (spellChecker.exist(wordForSuggestions)) {
			System.out.println("ok");
		} else {
			String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber,2f);
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
