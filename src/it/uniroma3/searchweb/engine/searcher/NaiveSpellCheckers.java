package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.HighFrequencyDictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.SuggestMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class NaiveSpellCheckers implements SpellCheckers{
	EngineConfig engineConfig;
	Directory spellCheckerDir;
	SpellChecker spellchecker;
	
	public NaiveSpellCheckers() throws IOException {
		engineConfig = EngineConfig.getInstance();
		spellCheckerDir = FSDirectory.open(new File(engineConfig.getDictionaryPath()));
		spellchecker = new SpellChecker(spellCheckerDir);
	}
	
	public void initialize() throws CorruptIndexException, IOException {
		Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
		IndexReader reader = DirectoryReader.open(index);
		// To index a field of a user index:
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
//        LuceneDictionary dictionary = new LuceneDictionary(reader, "body");
//        HighFrequencyDictionary dictionary = new HighFrequencyDictionary(reader, "body", 0.9f);
//		spellchecker.indexDictionary(dictionary,config,true);
		// To index a file containing words:
		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/en.txt")),config,true);
//		spellchecker.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/it.txt")),config,true);
		spellchecker.close();
	}
	
	public String[] getBasicSuggestions(String query, int numSug, float similarity) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(query);
		int tokenizerSize = tokenizer.countTokens();
		String[] result = new String[(int) Math.pow(numSug+1, tokenizerSize)];
		result[0] = "";
		Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
		IndexReader reader = DirectoryReader.open(index);
		boolean firstTime = true;
		spellchecker.setStringDistance(new NGramDistance());
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
			if (spellchecker.exist(currentToken)) {
				for (int i=0; i<result.length;i++) {
					if (firstTime) {
						result[i] = result[i]+currentToken;
						firstTime = false;
					} else
						result[i] = result[i]+" "+currentToken;
				}
			} else {
//				String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
//				suggestions= spellchecker.suggestSimilar(currentToken, numSug, reader, "body", SuggestMode.SUGGEST_MORE_POPULAR, similarity);
				if (suggestions!=null && suggestions.length>0) {
					result = createSuggestions(suggestions, result);
				} else {
					for (int i=0; i<result.length;i++) {
						if (firstTime) {
							result[i] = result[i]+currentToken;
							firstTime = false;
						} else
							result[i] = result[i]+" "+currentToken;
					}
				}
			}
		}
		return result;
	}
	
	public List<String> getBasicSuggestions1(String query, int numSug, float similarity) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(query);
		List<String> result = new ArrayList<String>();
		result.add("");
		int resultSize;
		spellchecker.setStringDistance(new LuceneLevenshteinDistance());
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
			resultSize = result.size();
			for (int i=0; i<resultSize;i++) {
				String tmp = result.get(0);
				for (String s : suggestions) {
					result.add(tmp.concat(s+" "));
					System.out.println(tmp.concat(s+" "));
				}
				result.add(tmp.concat(currentToken+" "));
				System.out.println(tmp.concat(currentToken+" "));
				result.remove(0);
			}
			System.out.println("");
			System.out.println(result.size());
			System.out.println("");
		}
		return result;
	}
	
//	public Map<String,List<String>> getBasicSuggestions(String query, int numSug, float similarity) throws IOException {
//		StringTokenizer tokenizer = new StringTokenizer(query);
//		Map<String,List<String>> results = new HashMap<String,List<String>>();
//		while (tokenizer.hasMoreTokens()) {
//			String currentToken = tokenizer.nextToken();
//			if (spellchecker.exist(currentToken)) {
//				List<String> corrections = new ArrayList<String>();
//				corrections.add(currentToken);
//				results.put(currentToken, corrections);
//			} else {
//				String[] suggestions = spellchecker.suggestSimilar(currentToken, numSug, similarity);
//				List<String> corrections = new ArrayList<String>();
//				if (suggestions!=null) {
//					for (String s : spellchecker.suggestSimilar(currentToken, numSug, similarity))
//						corrections.add(s);
//				} else {
//					corrections.add(currentToken);
//				}
//				results.put(currentToken, corrections);
//			}
//		}
//		return results;
//	}
	
	private String[] createSuggestions(String[]suggestions, String[] currentSuggestions) {
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
