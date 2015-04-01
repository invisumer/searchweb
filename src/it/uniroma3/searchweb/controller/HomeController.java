package it.uniroma3.searchweb.controller;

import javax.servlet.http.HttpSession;

import it.uniroma3.searchweb.model.QueryForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/", method=RequestMethod.GET)
	public String search(@ModelAttribute QueryForm query, Model model, HttpSession session) {		
		model.addAttribute("queryForm", query);
		
		return "indexWeb";
	}

}
