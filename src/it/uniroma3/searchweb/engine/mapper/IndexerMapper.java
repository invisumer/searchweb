package it.uniroma3.searchweb.engine.mapper;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexerMapper {
	private static final Logger logger = Logger.getLogger(IndexerMapper.class.getName());
	private Map<String, IndexWriter> mapper;
	private AnalyzerMapper analyzerMapper = new AnalyzerMapper();
	EngineConfig engineConfig = EngineConfig.getInstance();
	
	public IndexerMapper() {
		this.open();
	}
	
	private void open() {
		this.mapper = new HashMap<String, IndexWriter>();
		
		this.mapper.put("html", this.buildWriter("html"));
		this.mapper.put("audio", this.buildWriter("audio"));
		this.mapper.put("image", this.buildWriter("image"));
		this.mapper.put("video", this.buildWriter("video"));
		
		// TODO other formats
	}
	
	public IndexWriter pickWriter(String context, String type) {
		String writerType = "";
		
		if (context.equals("text") && type.equals("html"))
			writerType = "html";
		if (context.equals("audio"))
			writerType = "audio";
		if (context.equals("image"))
			writerType = "image";
		if (context.equals("video"))
			writerType = "video";
		
		IndexWriter writer = this.mapper.get(writerType);
		
		return writer;
	}
	
	private IndexWriter buildWriter(String context) {
		Analyzer a = this.analyzerMapper.pickAnalyzer("default");
		
		File dir = new File(engineConfig.getIndexPath() + "/" + context);
		if (!dir.exists())
			dir.mkdir();
		
		IndexWriter writer = null;
		
		try {
			Directory index = FSDirectory.open(dir);
			Version version = EngineConfig.getVersion();
			IndexWriterConfig config = new IndexWriterConfig(version, a);
			config.setRAMBufferSizeMB(engineConfig.getRAMBufferSize());
			config.setOpenMode(engineConfig.getIndexOpenMode());
			writer = new IndexWriter(index, config);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return writer;
	}
	
	public void close() throws IOException {
		for (IndexWriter writer : this.mapper.values())
			writer.close();
	}

}
