package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;

public class MainSpellChecker {
	public MainSpellChecker(){}
	public static void main(String[] args) throws IOException {
		NaiveSpellCheckers sp = new NaiveSpellCheckers();
		sp.initialize();
//		int numSug = 1;
//		String query = "applw";
//		float similarity = 0.8f;
//		List<String> result = sp.getBasicSuggestions(query, numSug,similarity);
//		NGramDistance d = new NGramDistance();
//		LuceneLevenshteinDistance lld = new LuceneLevenshteinDistance();
//		for (String s : result) {
//			System.out.println("Did you mean : "+s);
//			
//		}
	}
}
