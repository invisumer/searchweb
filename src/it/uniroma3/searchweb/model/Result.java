package it.uniroma3.searchweb.model;

import java.util.Date;

public class Result {
	private String title;
	private String url;
	private Date date;
	private String snippet;
	
	public Result(String title, String url, Date date, String snippet) {
		this.title = title;
		this.url = url;
		this.date = date;
		this.snippet = snippet;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

}
