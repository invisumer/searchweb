package it.uniroma3.searchweb.model;

import javax.validation.constraints.NotNull;

public class QueryForm {
	@NotNull
	private String query;
	private boolean spellCheckerActive = true;
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public boolean isSpellCheckerActive() {
		return spellCheckerActive;
	}
	
	public void setSpellCheckerActive(boolean spellCheckerActive) {
		this.spellCheckerActive = spellCheckerActive;
	}
}
