package it.uniroma3.searchweb.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import it.uniroma3.searchweb.engine.searcher.PagingSearchEngine;
import it.uniroma3.searchweb.model.QueryForm;
import it.uniroma3.searchweb.model.Result;
import it.uniroma3.searchweb.model.ResultsPager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchController {
	private static final String INVALID_QUERY = "The query is not valid.";
	private static final String INVALID_PAGE = "The page is not valid.";
	
//	@Resource(name="searcher")
//	private SearchEngine engine;
	PagingSearchEngine searcher = new PagingSearchEngine();
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String submitRequest(@Valid @ModelAttribute QueryForm query, BindingResult result, 
			ModelMap model, HttpSession session) {
		
		if (result.hasErrors()) {
			model.addAttribute("error", INVALID_QUERY);
			model.addAttribute("queryForm", query);
			return "search";
		}
		
		// some logic
		if (!query.getQuery().isEmpty()) {
			session.setAttribute("queryForm", query);
			session.setAttribute("pager", this.getPager(query.getQuery()));
		}
		
	    return "redirect:/search/page/1";
	}
	
	@RequestMapping(value="/search/page/{n}", method=RequestMethod.GET)
	public String getPage(@PathVariable int n, HttpSession session, ModelMap model) {
		ResultsPager pager = (ResultsPager) session.getAttribute("pager");
		QueryForm form = (QueryForm) session.getAttribute("queryForm");
		
		if (pager == null || form == null) {
			model.addAttribute("error", INVALID_QUERY);
			model.addAttribute("queryForm", new QueryForm());
			return "search";
		}
		
		model.addAttribute("queryForm", form);
		List<Result> results = pager.getPage(n);
		
		if (results == null) {
			model.addAttribute("error", INVALID_PAGE);
			return "search";
		}
		
		model.addAttribute("results", pager.getPage(n));
		model.addAttribute("pages", pager.getPages());
		model.addAttribute("currentPage", n);
			
		return "search";
	}
	
	private ResultsPager getPager(String query) {
		String[] fields = new String[2];
		fields[0] = "title";
		fields[1] = "body";
		return this.searcher.getPager(query, fields);
	}
	
//	private List<Result> search(String query) {
//		String[] fields = new String[2];
//		fields[0] = "title";
//		fields[1] = "body";
//		return this.engine.getResults(query,fields);
//	}

}
