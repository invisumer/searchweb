package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class NaiveSpellCheckers implements SpellCheckers{
	
	public NaiveSpellCheckers() throws IOException { }
	
	public void initialize(String lang) throws CorruptIndexException, IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		Directory spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-"+lang));
		SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/"+lang+".txt")),config,true);
		spellchecker.close();
	}
	
	public List<String> getBasicSuggestions(String query, int numSug, String lang) throws IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		Directory spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()+"/dictionary-"+lang));
		SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
		StringTokenizer tokenizer = new StringTokenizer(query);
		List<String> result = new ArrayList<String>();
		result.add("");
		int resultSize;
		spellchecker.setStringDistance(new LuceneLevenshteinDistance());
		float similarity = 1.0f;
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			if (currentToken.length()>1)
				similarity = (float) (1-1.0/currentToken.length());
			String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
			resultSize = result.size();
			for (int i=0; i<resultSize;i++) {
				String tmp = result.get(0);
				for (String s : suggestions) {
					result.add(tmp.concat(s+" "));
				}
				result.add(tmp.concat(currentToken+" "));
				result.remove(0);
			}
		}
		spellchecker.close();
		return result;
	}
	
}
