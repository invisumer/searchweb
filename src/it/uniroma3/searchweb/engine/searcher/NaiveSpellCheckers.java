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
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class NaiveSpellCheckers implements SpellCheckers{
	SpellCheckerMapper mapper;
	
	public NaiveSpellCheckers(SpellCheckerMapper mapper) throws IOException {
		this.mapper = mapper;
	}
	
	public void initialize(String lang,String path) throws CorruptIndexException, IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		SpellChecker spellcheckerDictionary = this.mapper.getSpellChecker(lang);
		SpellChecker spellcheckerIndex = this.mapper.getSpellChecker("index");
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        File dir = new File(engineConfig.getIndexPath());
		if (!dir.exists())
			dir.mkdir();
		Directory index = FSDirectory.open(dir);
		IndexReader reader = DirectoryReader.open(index);
        spellcheckerIndex.indexDictionary(new LuceneDictionary(reader, "body"), config, true);
		spellcheckerDictionary.indexDictionary(new PlainTextDictionary(new File(engineConfig.getPlainPath()+"/"+lang+".txt")),config,true);
		spellcheckerDictionary.close();
		spellcheckerIndex.close();
	}
	
	public List<String> getBasicSuggestions(String query, String lang, String path) throws IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		SpellChecker spellcheckerDictionary = this.mapper.getSpellChecker(lang);
		SpellChecker spellcheckerIndex = this.mapper.getSpellChecker("index");
		StringTokenizer tokenizer = new StringTokenizer(query);
		List<String> result = new ArrayList<String>();
		result.add("");
		int resultSize;
		LuceneLevenshteinDistance ldistance = new LuceneLevenshteinDistance();
		spellcheckerDictionary.setStringDistance(ldistance);
		spellcheckerIndex.setStringDistance(ldistance);
		float similarity = 1.0f;
		int numSug = (engineConfig.getMaxCorrection()/10)/tokenizer.countTokens();
		System.out.println(numSug);
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			if (currentToken.length()>1) {
				similarity = (float) (1-1.0/currentToken.length());
				if (similarity>0.85)
					similarity = 0.7f;
			}
			String[] suggestionsDictionary = spellcheckerDictionary.suggestSimilar(currentToken, numSug, similarity);
//			String[] suggestionsIndex = spellcheckerIndex.suggestSimilar(arg0, arg1, arg2, arg3, arg4, arg5)
			resultSize = result.size();
			for (int i=0; i<resultSize;i++) {
				String tmp = result.get(0);
				for (String s : suggestionsDictionary) {
					result.add(tmp.concat(s+" "));
				}
				result.remove(0);
			}
		}
		spellcheckerDictionary.close();
		return result;
	}
	
}
