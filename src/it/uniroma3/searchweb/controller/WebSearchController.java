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
public class WebSearchController {
	private static final String INVALID_QUERY = "The query is not valid.";
	private static final String INVALID_PAGE = "The page is not valid.";
	
	@Resource(name="searcher")
	private SearchEngine engine;
	private DecimalFormat df = new DecimalFormat("0.000"); 
	
	@RequestMapping(value="/search/web", method=RequestMethod.GET)
	public String submitRequest(@Valid @ModelAttribute QueryForm query, BindingResult result, 
			ModelMap model, HttpSession session, HttpServletRequest request) {
		
		if (result.hasErrors()) {
			model.addAttribute("error", INVALID_QUERY);
			model.addAttribute("queryForm", query);
			return "searchWeb";
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
			
			return "redirect:/search/web/page/1";
		}
		
	    return "indexWeb";
	}
	
	@RequestMapping(value="/search/web/page/{n}", method=RequestMethod.GET)
	public String getPage(@PathVariable int n, HttpSession session, ModelMap model) {
		ResultsPager pager = (ResultsPager) session.getAttribute("pager");
		QueryForm form = (QueryForm) session.getAttribute("queryForm");
		
		if (pager == null || form == null) {
			model.addAttribute("error", INVALID_QUERY);
			model.addAttribute("queryForm", new QueryForm());
			return "searchWeb";
		}
		
		model.addAttribute("queryForm", form);
		List<Result> results = pager.getPage(n);
		
		if (results == null) {
//			session.removeAttribute("queryForm");
//			session.removeAttribute("pager");
//			session.removeAttribute("statistics");
			model.addAttribute("error", INVALID_PAGE);
			return "searchWeb";
		}
		
		model.addAttribute("results", pager.getPage(n));
		model.addAttribute("pages", pager.getPages());
		model.addAttribute("currentPage", n);
			
		return "searchWeb";
	}
	
	private ResultsPager getPager(String query) {
		String[] fields = new String[4];
		fields[0] = "title";
		fields[1] = "body";
		fields[2] = "domain";
		fields[3] = "domain2";
		String contentType = "html";
		String lang = "en";  // TODO prendere da spring la location
		return this.engine.getResults(query, fields, contentType, lang);
	}

}
