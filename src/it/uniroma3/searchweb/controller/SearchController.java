package it.uniroma3.searchweb.controller;

import java.util.List;

import javax.validation.Valid;

import it.uniroma3.searchweb.engine.StupidSearchEngine;
import it.uniroma3.searchweb.model.QueryForm;
import it.uniroma3.searchweb.model.Result;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SearchController {
	private static final String INVALID_QUERY = "The query is not valid";
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String search(@ModelAttribute QueryForm query, ModelMap model) {
		model.addAttribute("queryForm", query);
		
		if (model.containsAttribute("results"))
			System.out.println("ECHO");
		
	    return "search";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.POST)
	public String submitRequest(@Valid @ModelAttribute QueryForm query, BindingResult result, 
			RedirectAttributes flash) {
		
		if (result.hasErrors()) {
			flash.addFlashAttribute("error", INVALID_QUERY);
			return "redirect:/search";
		}
		
		// some logic
		if (!query.getQuery().isEmpty())
			flash.addFlashAttribute("results", this.search(query.getQuery()));
		
	    return "redirect:/search";
	}
	
	private List<Result> search(String query) {
		StupidSearchEngine engine = new StupidSearchEngine();
		return engine.getResults(query);
	}

}
