package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.SpellCheckerMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.SuggestMode;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.search.spell.SuggestWordFrequencyComparator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class NaiveSpellChecker implements SpellCheckerEngine{
	SpellCheckerMapper mapper;
	
	public NaiveSpellChecker(SpellCheckerMapper mapper) throws IOException {
		this.mapper = mapper;
	}
	
	public List<String> getSuggestions(String query) throws IOException {
		EngineConfig engineConfig = EngineConfig.getInstance();
		File dir = new File(engineConfig.getIndexPath()+"/"+"html");
		if (!dir.exists())
			dir.mkdir();
		Directory index = FSDirectory.open(dir);
		IndexReader reader = DirectoryReader.open(index);
		DirectSpellChecker spellchecker = new DirectSpellChecker();
		spellchecker.setComparator(new SuggestWordFrequencyComparator());
		StringTokenizer tokenizer = new StringTokenizer(query);
		List<String> result = new ArrayList<String>();
		result.add("");
		LuceneLevenshteinDistance ldistance = new LuceneLevenshteinDistance();
		spellchecker.setDistance(ldistance);
		spellchecker.setLowerCaseTerms(true);
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
			similarity = (float) (1-(1.5/currentToken.length()));
			if (similarity>0.85)
				similarity = 0.7f;
		}
		Term t = new Term("body",currentToken);
		SuggestWord[] suggestionsIndex = spellchecker.suggestSimilar(t, numSug, reader, SuggestMode.SUGGEST_MORE_POPULAR,similarity);
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
			if (suggestionsIndex.length == 0)
				result.add(tmp.concat(currentToken+" "));
			result.remove(0);
		}
		return result;
	}
}
