package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

public class MainSpellChecker {
	public MainSpellChecker(){}
	public static void main(String[] args) throws IOException {
		SpellCheckers sp = new SpellCheckers();
		sp.populateLuceneDictionary();
//		int numSug = 5;
//		String query = "famoos enterprener";
//		float similarity = 0.8f;
//		String[] result = sp.getBasicSuggestions(query, numSug,similarity);
//		for (String s : result) {
//			System.out.println("Did you mean : "+s);
//		}
	}
}
