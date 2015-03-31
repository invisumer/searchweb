package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.SpellCheckerMapper;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.HighFrequencyDictionary;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.SuggestMode;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.search.spell.SuggestWordFrequencyComparator;
import org.apache.lucene.search.spell.SuggestWordScoreComparator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class NaiveSpellCheckers implements SpellCheckers{
	SpellCheckerMapper mapper;
	
	public NaiveSpellCheckers(SpellCheckerMapper mapper) throws IOException {
		this.mapper = mapper;
	}
	
	public void initialize(String path) throws CorruptIndexException, IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        File dir = new File(engineConfig.getIndexPath()+"/"+path);
		if (!dir.exists())
			dir.mkdir();
		Directory index = FSDirectory.open(dir);
		IndexReader reader = DirectoryReader.open(index);
		SpellChecker spellchecker = this.mapper.getSpellChecker();
        spellchecker.indexDictionary(new HighFrequencyDictionary(reader, "body",0.003f), config, true);
        spellchecker.close();
	}
	
//	public List<String> getBasicSuggestions(String query, String lang) throws IOException {
//		EngineConfig engineConfig = EngineConfig.getInstance();
//		SpellChecker spellchecker = this.mapper.getSpellChecker();
//		spellchecker.setComparator(new SuggestWordScoreComparator());
//		StringTokenizer tokenizer = new StringTokenizer(query);
//		List<String> result = new ArrayList<String>();
//		result.add("");
//		int resultSize;
//		LuceneLevenshteinDistance ldistance = new LuceneLevenshteinDistance();
//		spellchecker.setStringDistance(ldistance);
//		int numSug = (int) ((engineConfig.getMaxCorrection()/Math.sqrt(engineConfig.getMaxCorrection()))/tokenizer.countTokens());
//		System.out.println("NaiveSpellChecker- Numero di suggerimenti per parola : "+(numSug+1));
//		int tokens = tokenizer.countTokens();
//		while (tokenizer.hasMoreTokens()) {
//			String currentToken = tokenizer.nextToken();
//			if (tokens<4)
//				result = generateSuggestions(result, currentToken, spellchecker, numSug);
//			else {
//				if (spellchecker.exist(currentToken)) {
//					resultSize = result.size();
//					for (int i=0; i<resultSize;i++) {
//						String tmp = result.get(0);
//						result.add(tmp.concat(currentToken+" "));
//						result.remove(0);
//					}
//				} else {
//					result = generateSuggestions(result, currentToken, spellchecker, numSug);
//				}
//			}
//		}
//		return result;
//	}
//	
//	private List<String> generateSuggestions(List<String> result, String currentToken, SpellChecker spellchecker, int numSug) throws IOException {
//		float similarity = 1.0f;
//		int resultSize;
//		if (currentToken.length()>1) {
//			similarity = (float) (1-(2.0/currentToken.length()));
//			System.out.println("similarity "+similarity);
//			if (similarity>0.85)
//				similarity = 0.7f;
//		}
//		System.out.println("similarity "+similarity);
//		String[] suggestionsIndex = spellchecker.suggestSimilar(currentToken, numSug, similarity);
//		resultSize = result.size();
//		for (int i=0; i<resultSize;i++) {
//			String tmp = result.get(0);
//			for (String s : suggestionsIndex) {
//				result.add(tmp.concat(s+" "));
//			}
//			if (spellchecker.exist(currentToken))
//				result.add(tmp.concat(currentToken+" "));
//			result.remove(0);
//		}
//		return result;
//	}
	
	public List<String> getBasicSuggestions(String query, String lang) throws IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		File dir = new File(engineConfig.getIndexPath()+"/"+"html");
		if (!dir.exists())
			dir.mkdir();
		Directory index = FSDirectory.open(dir);
		IndexReader reader = DirectoryReader.open(index);
		DirectSpellChecker spellchecker = new DirectSpellChecker();
		spellchecker.setComparator(new SuggestWordScoreComparator());
		StringTokenizer tokenizer = new StringTokenizer(query);
		List<String> result = new ArrayList<String>();
		result.add("");
		LuceneLevenshteinDistance ldistance = new LuceneLevenshteinDistance();
		spellchecker.setDistance(ldistance);
		spellchecker.setMaxEdits(2);
		System.out.println(spellchecker.getThresholdFrequency());
		spellchecker.setLowerCaseTerms(true);
//		int numSug = (int) ((engineConfig.getMaxCorrection()/Math.sqrt(engineConfig.getMaxCorrection()))/tokenizer.countTokens());
		int numSug = 10;
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			result = generateSuggestions(result, currentToken, spellchecker, numSug,reader);
		}
		return result;
	}
	
	private List<String> generateSuggestions(List<String> result, String currentToken, DirectSpellChecker spellchecker, int numSug, IndexReader reader) throws IOException {
		float similarity = 1.0f;
		int resultSize;
		if (currentToken.length()>1) {
			similarity = (float) (1-(2.0/currentToken.length()));
			if (similarity>0.85)
				similarity = 0.7f;
		}
		Term t = new Term("body",currentToken);
		SuggestWord[] suggestionsIndex = spellchecker.suggestSimilar(t, numSug, reader, SuggestMode.SUGGEST_MORE_POPULAR);
		resultSize = result.size();
		for (int i=0; i<resultSize;i++) {
			String tmp = result.get(0);
			int j = 0;
			for (SuggestWord s : suggestionsIndex) {
				if (j<2)
					result.add(tmp.concat(s.string+" "));
				else
					break;
				j++;
			}
			result.remove(0);
		}
		return result;
	}
}
