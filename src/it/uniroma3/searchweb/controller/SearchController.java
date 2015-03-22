package it.uniroma3.searchweb.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class SearchController {
	
	@RequestMapping(value = "/", produces=MediaType.TEXT_HTML_VALUE)
	public String search() {
		return "search";
	}

}
