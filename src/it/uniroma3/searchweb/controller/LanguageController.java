package it.uniroma3.searchweb.controller;

import java.util.Map;
import java.util.TreeMap;

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
			this.langOptions = new TreeMap<String, String>();
			this.langOptions.put("th", "ไทย");
			this.langOptions.put("it", "Italiano");
			this.langOptions.put("fr", "Française");
			this.langOptions.put("en", "English");
			this.langOptions.put("es", "Español");
			this.langOptions.put("jp", "日本語");
			this.langOptions.put("de", "Deutsch");
			this.langOptions.put("ko", "한국어, 조선어");
		}
		
		return this.langOptions;
	}
	
}
