package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearcherMapper {
	private static final Logger logger = Logger.getLogger(SearcherMapper.class.getName());
	private Map<String, IndexSearcher> mapper;
	EngineConfig engineConfig = EngineConfig.getInstance();
	
	public SearcherMapper() {
		this.open();
	}
	
	private void open() {
		this.mapper = new HashMap<String, IndexSearcher>();
		this.mapper.put("text-en", this.buildIndexSearcher("text-en"));
//		this.mapper.put("text-it", this.buildIndexSearcher("text-it"));
//		this.mapper.put("text-fr", this.buildIndexSearcher("text-fr"));
//		this.mapper.put("text-th", this.buildIndexSearcher("text-th"));
		// TODO other formats
	}
	
	public IndexSearcher pickSearcher(String lang) {
		return this.mapper.get(lang);
	}
	
	public IndexSearcher buildIndexSearcher(String path) {
		// TODO Use searcher manager
		IndexSearcher searcher = null;
		
		try {
			File dir = new File(engineConfig.getIndexPath() + "/" + path);
			if (!dir.exists())
				dir.mkdir();
			Directory index = FSDirectory.open(dir);
			IndexReader reader = DirectoryReader.open(index);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return searcher;
	}
	
	public void refresh() {
		// TODO Use searcher manager
	}
}
