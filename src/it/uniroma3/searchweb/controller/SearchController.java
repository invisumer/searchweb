package it.uniroma3.searchweb.controller;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import it.uniroma3.searchweb.engine.searcher.SearchEngine;
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
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class SearchController {
	private static final String INVALID_QUERY = "The query is not valid.";
	private static final String INVALID_PAGE = "The page is not valid.";
	
	@Resource(name="searcher")
	private SearchEngine engine;
	private DecimalFormat df = new DecimalFormat("0.000"); 
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String submitRequest(@Valid @ModelAttribute QueryForm query, BindingResult result, 
			ModelMap model, HttpSession session, HttpServletRequest request) {
		
		if (result.hasErrors()) {
			model.addAttribute("error", INVALID_QUERY);
			model.addAttribute("queryForm", query);
			return "search";
		}
		
		// some logic
		if (!query.getQuery().isEmpty()) {
			Locale locale = RequestContextUtils.getLocale(request);
			System.out.println(locale.getCountry());    // TODO che ci faccio?
			System.out.println(locale.getLanguage());
			session.setAttribute("queryForm", query);
			
			long start = System.currentTimeMillis();
			ResultsPager pager = this.getPager(query.getQuery());
			session.setAttribute("pager", pager);
			long stop = System.currentTimeMillis();
			
			String time = df.format((stop-start+0.0)/1000);
			int nResults = pager.getDocs().length;
			session.setAttribute("statistics", nResults + " result(s) in " + time + " sec");
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
//			session.removeAttribute("queryForm");
//			session.removeAttribute("pager");
//			session.removeAttribute("statistics");
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
		String lang = "en";  // TODO prendere da spring la location
		return this.engine.getResults(query, fields, lang);
	}

}
