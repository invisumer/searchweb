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
	
	public SpellCheckers() {
	}
	
	public static void populateLuceneDictionary() throws CorruptIndexException, IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		Directory spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()));
		SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
		Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
		IndexReader reader = DirectoryReader.open(index);
		// To index a field of a user index:
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellchecker.indexDictionary(new LuceneDictionary(reader, "body"),config,true);
		// To index a file containing words:
		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/en.txt")),config,true);
		spellchecker.close();
	}
	
	public static void main(String[] args) throws Exception {
		EngineConfig engineConfig = EngineConfig.getInstance();
//		populateLuceneDictionary();
		Directory spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()));
		SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
		String wordForSuggestions = "cristiano ronaldo";
		int suggestionsNumber = 5;
		String[] suggestions = spellchecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
		if (suggestions!=null && suggestions.length>0) {
			for (String word : suggestions) {
				System.out.println("Did you mean:" + word);
			}
		} else {
			System.out.println("No suggestions found for word:"+wordForSuggestions);
		}
//		if (spellchecker.exist(wordForSuggestions)) {
//			System.out.println("ok");
//		} else {
//			String[] suggestions = spellchecker.suggestSimilar(wordForSuggestions, suggestionsNumber,2f);
//			if (suggestions!=null && suggestions.length>0) {
//				for (String word : suggestions) {
//					System.out.println("Did you mean:" + word);
//				}
//			} else {
//				System.out.println("No suggestions found for word:"+wordForSuggestions);
//			}
//		}
		spellchecker.close();
	}

}
