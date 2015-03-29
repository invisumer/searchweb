package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

public class MainSpellChecker {
	public MainSpellChecker(){}
	public static void main(String[] args) throws IOException {
		NaiveSpellCheckers sp = new NaiveSpellCheckers();
		sp.initialize("en");
//		String query = "enterpreneus";
//		List<String> result = sp.getBasicSuggestions(query, "en");
//		for (String s : result) {
//			System.out.println("Did you mean : "+s);
//			
//		}
	}
}
