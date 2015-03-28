package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.util.Version;

public class AnalyzerMapper {
	private Map<String, Analyzer> mapper;
	
	public AnalyzerMapper() {
		Version version = EngineConfig.getVersion();
		this.mapper = new HashMap<String, Analyzer>();
		
		this.mapper.put("text-en", new StandardAnalyzer(version));
//		this.mapper.put("text-it", new ItalianAnalyzer(version));
//		this.mapper.put("text-fr", new FrenchAnalyzer(version));
//		this.mapper.put("text-th", new ThaiAnalyzer(version));
		
		// TODO other formats
	}
	
	public Analyzer pickAnalyzer(String code) {
		if (code == null)
			return null;
		
		Analyzer analyzer = null;
		analyzer = this.mapper.get(code);
		
		if (analyzer==null && code.startsWith("text-"))
			analyzer = this.mapper.get("text-en");
		
		// TODO defaults analyzer for other formats
		
		return analyzer;
	}
}
