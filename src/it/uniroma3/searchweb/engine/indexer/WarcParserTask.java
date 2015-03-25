package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

public class WarcParserTask implements Callable<Integer> {
	private static final Logger logger = Logger.getLogger(WarcParserTask.class.getName());
	private long id;
	private IndexWriter writer;
	private WarcParser parser;
	private String[] files;
	private int start;
	private int stop;
	
	public WarcParserTask(IndexWriter writer, int start, int stop) {
		this.writer = writer;
		this.parser = new WarcParser();
		EngineConfig config = EngineConfig.getInstance();
		this.files = config.getWarcFiles();
		this.start = start;
		this.stop = stop;
	}
	
	public WarcParserTask(IndexWriter writer, int start, int stop, String[] files) {
		this.writer = writer;
		this.parser = new WarcParser();
		this.files = files;
		this.start = start;
		this.stop = stop;
	}
	
	@Override
	public Integer call() throws Exception {
		this.id = Thread.currentThread().getId();
		return parseAll();
	}
	
	private int parseAll() {
		int i = -this.start;
		int counter = 0;
		
		try {
			for (i=this.start; i<this.stop; i++) {
				counter += this.parse(this.files[i]);
			}
		} catch (FileNotFoundException e) {
			logger.severe("[" + id + "] " + e.getMessage());
			e.printStackTrace();
			return -i;
		} catch (IOException e) {
			logger.severe("[" + id + "] " + e.getMessage());
			return -i;
		}
		
		return counter;
	}
	
	private int parse(String file) throws FileNotFoundException, IOException {
		Document doc = null;
		int counter = 1;
		
		parser.open(file);
		while ((doc = parser.next()) != null) {
			writer.addDocument(doc);
			counter++;
		}
		parser.close();
		
		logger.info("["+id+"] " + "Parsed '"+file+"' with "+counter+" docs");
		writer.commit();
		
		return counter;
	}

}
