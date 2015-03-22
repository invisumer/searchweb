package it.uniroma3.searchweb.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class SearchController {
	
	@RequestMapping(value = "/", produces=MediaType.TEXT_HTML_VALUE)
	public String search() {
		return "search";
	}

}
