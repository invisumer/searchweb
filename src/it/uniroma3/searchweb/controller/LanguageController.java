package it.uniroma3.searchweb.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import it.uniroma3.searchweb.model.LanguagesForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LanguageController {
	private Map<String, String> langOptions;
	
	@RequestMapping(value = "/select/language", method=RequestMethod.GET)
	public String getLanguageForm(@ModelAttribute LanguagesForm form, Model model, HttpSession session) {
		@SuppressWarnings("unchecked")
		Set<String> langs = (Set<String>) session.getAttribute("langs");
		if (langs != null && !langs.isEmpty())
			form.setLanguages(langs);
		
		model.addAttribute("languageForm", form);
		model.addAttribute("langOptions", this.getLanguages());
		return "language";
	}
	
	@RequestMapping(value = "/select/language", method=RequestMethod.POST)
	public String setLanguage(@ModelAttribute LanguagesForm form, HttpSession session) {
		session.setAttribute("langs", form.getLanguages());
		
		return "redirect:/";
	}
	
	public Map<String, String> getLanguages() {
		if (this.langOptions == null) {
			this.langOptions = new HashMap<String, String>();
			this.langOptions.put("th", "ไทย&");
			this.langOptions.put("it", "Italiano");
			this.langOptions.put("fr", "Français");
			this.langOptions.put("en", "English");
			
			// TODO en it es fr jp de ko
		}
		
		return this.langOptions;
	}
	
}
