package it.uniroma3.searchweb.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import it.uniroma3.searchweb.model.LanguageForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LanguageController {
	private Map<String, String> langOptions;
	
	@RequestMapping(value = "/select/language", method=RequestMethod.GET)
	public String getLanguageForm(@ModelAttribute LanguageForm form, Model model, HttpSession session) {
		String lang = (String) session.getAttribute("lang");
		if (lang != null && !lang.isEmpty())
			form.setLanguage(lang);
		
		model.addAttribute("languageForm", form);
		model.addAttribute("langOptions", this.getLanguages());
		return "language";
	}
	
	@RequestMapping(value = "/select/language", method=RequestMethod.POST)
	public String setLanguage(@ModelAttribute LanguageForm form, HttpSession session) {
		session.setAttribute("lang", form.getLanguage());
		return "redirect:/";
	}
	
	public Map<String, String> getLanguages() {
		if (this.langOptions == null) {
			this.langOptions = new HashMap<String, String>();
			this.langOptions.put("th", "Thai");
			this.langOptions.put("it", "Italian");
			this.langOptions.put("fr", "French");
			this.langOptions.put("en", "English");
			
			// TODO en it es fr jp de ko
		}
		
		return this.langOptions;
	}
	
}
