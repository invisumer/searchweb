package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearcherMapper {
	private static final Logger logger = Logger.getLogger(SearcherMapper.class.getName());
	private Map<String, SearcherManager> mapper;
	EngineConfig engineConfig = EngineConfig.getInstance();
	
	public SearcherMapper() {
		this.open();
	}
	
	private void open() {
		this.mapper = new HashMap<String, SearcherManager>();
		this.mapper.put("html", this.buildIndexSearcher("html"));
		// TODO other formats
	}
	
	public SearcherManager pickSearcher(String contentType) {
		return this.mapper.get(contentType);
	}
	
	public SearcherManager buildIndexSearcher(String path) {
		SearcherManager manager = null;
		try {
			File dir = new File(engineConfig.getIndexPath() + "/" + path);
			if (!dir.exists())
				return null;
			Directory index = FSDirectory.open(dir);
			manager = new SearcherManager(index, new SearcherFactory());
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} 
		
		return manager;
	}
	
	public void refresh() {
		// TODO Use searcher manager
	}
}
