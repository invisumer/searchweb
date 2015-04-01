package it.uniroma3.searchweb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class ErrorHandler {
	List<String> invalidAllChars;
	List<String> endInvalidChars;
	
	public ErrorHandler() {
		invalidAllChars = initializeAll();
		endInvalidChars = initializeEnd();
	}
	
	private List<String> initializeAll() {
		List<String> invalid = new ArrayList<String>();
		invalid.add("{");
		invalid.add("}");
		invalid.add("[");
		invalid.add("]");
		invalid.add(":");
		invalid.add("^");
		invalid.add("!!");
		invalid.add(" ?");
		invalid.add("/");
		invalid.add("\\");
		return invalid;
	}
	
	private List<String> initializeEnd() {
		List<String> invalid = new ArrayList<String>();
		invalid.add(" +");
		invalid.add(" -");
		invalid.add("!");
		invalid.add(" &&");
		invalid.add(" !!");
		return invalid;
	}
	
	public String analyzeQuery(String query) {
		int countQuote = StringUtils.countOccurrencesOf(query, "\"");
		if (countQuote%2==1)
			query = query.replaceFirst("\"", "");
		for (String inv : this.invalidAllChars) {
			if (query.contains(inv)) {
				query = query.replaceAll("\\"+inv, "");
			}
		}
		for (String inv : this.endInvalidChars) {
			if (query.endsWith(inv))
				query = query.substring(0, query.length()-(inv.length()));
		}
		if (query.contains(" *"))
			query = query.replaceAll(" \\*", " ");
		if (query.startsWith("*"))
			query = query.replaceFirst("\\*", "");
			
		return query;
	}

}
