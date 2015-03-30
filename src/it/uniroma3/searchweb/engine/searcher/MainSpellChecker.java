package it.uniroma3.searchweb.engine.searcher;

import it.uniroma3.searchweb.engine.mapper.SpellCheckerMapper;

import java.io.IOException;
import java.util.List;

public class MainSpellChecker {
	public MainSpellChecker(){}
	public static void main(String[] args) throws IOException {
		SpellCheckerMapper mapper = new SpellCheckerMapper();
		NaiveSpellCheckers sp = new NaiveSpellCheckers(mapper);
//		sp.initialize("html");
		String query = "kristin";
		List<String> result = sp.getBasicSuggestions(query, "en");
		for (String s : result) {
			System.out.println("Did you mean : "+s);
			
		}
	}
}
