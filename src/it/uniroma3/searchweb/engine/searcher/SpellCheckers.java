package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spell.HighFrequencyDictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SpellCheckers {
	EngineConfig engineConfig;
	Directory spellCheckerDir;
	SpellChecker spellchecker;
	
	public SpellCheckers() throws IOException {
		engineConfig = EngineConfig.getInstance();
		spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()));
		spellchecker = new SpellChecker(spellCheckerDir);
	}
	
	public void populateLuceneDictionary() throws CorruptIndexException, IOException {
		Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
		IndexReader reader = DirectoryReader.open(index);
		// To index a field of a user index:
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		spellchecker.indexDictionary(new LuceneDictionary(reader, "body"),config,true);
		// To index a file containing words:
//		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/uk.txt")),config,true);
//		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/it.txt")),config,true);
		spellchecker.close();
	}
	
	public String[] getBasicSuggestions(String query, int numSug, float similarity) throws IOException {
		StringDistance distance = spellchecker.getStringDistance();
		StringTokenizer tokenizer = new StringTokenizer(query);
		String[] result = new String[1];
		result[0] = "";
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			if (spellchecker.exist(currentToken)) {
				for (int i=0; i<result.length;i++) {
					result[i] = result[i]+" "+currentToken;
				}
			} else {
				String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
				if (suggestions!=null && suggestions.length>0) {
					result = createSuggestions(suggestions, result);
				} else {
					for (int i=0; i<result.length;i++) {
						result[i] = result[i]+" "+currentToken;
					}
				}
			}
		}
		for (String s : result) {
			System.out.println("Distance : "+distance.getDistance(s,query));
		}
		return result;
	}
	
	public String[] createSuggestions(String[]suggestions, String[] currentSuggestions) {
		String[] result = new String[suggestions.length*currentSuggestions.length];
		int counter = 0;
		int i,j;
		
		for (i=0; i<currentSuggestions.length;i++) {
			String current = currentSuggestions[i];
			int x = 0;
			for (j=counter;j<counter+suggestions.length;j++) {
				if (!current.equals(""))
					result[j] = current+" "+suggestions[x];
				else
					result[j] = current+suggestions[x];
				x++;
			}
			counter+=x;
		}
		return result;
	}
	
}
