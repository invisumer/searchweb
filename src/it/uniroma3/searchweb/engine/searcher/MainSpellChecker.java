package it.uniroma3.searchweb.engine.searcher;

import java.io.IOException;
import java.util.List;

public class MainSpellChecker {
	public MainSpellChecker(){}
	public static void main(String[] args) throws IOException {
		NaiveSpellCheckers sp = new NaiveSpellCheckers();
//		sp.initialize("it");
		int numSug = 1;
		String query = "il mare è bellussimo";
		List<String> result = sp.getBasicSuggestions(query, numSug,"it");
		for (String s : result) {
			System.out.println("Did you mean : "+s);
			
		}
	}
}
