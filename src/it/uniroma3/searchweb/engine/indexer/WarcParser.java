package it.uniroma3.searchweb.engine.indexer;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.GZIPInputStream;

import org.apache.lucene.document.Document;

import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private static final Logger logger = Logger.getLogger(WarcParser.class.getName()); 
	private String dataset;
	private DataInputStream stream;
	private WarcRecord cur;
	private WarcConverter converter;

	public WarcParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.dataset = config.getDatasetPath();
		this.converter = new WarcConverter(config.isCleanHtml());
	}

	public WarcParser(String datasetPath) {
		EngineConfig config = EngineConfig.getInstance();
		this.dataset = datasetPath;
		this.converter = new WarcConverter(config.isCleanHtml());
	}

	public String getDatasetPath() {
		return dataset;
	}

	public void setDatasetPath(String datasetPath) {
		this.dataset = datasetPath;
	}

	public void open(String file) throws FileNotFoundException, IOException {
		String filepath = this.getDatasetPath() + "/" + file;
		GZIPInputStream gzInputStream = new GZIPInputStream(
				new FileInputStream(filepath));
		this.stream = new DataInputStream(gzInputStream);
	}

	public void close() throws IOException {
		this.stream.close();
	}

	public Document next() throws IOException {
		this.cur = WarcRecord.readNextWarcRecord(this.stream);

		while (this.cur != null) {
			Document doc = null;
			if (this.cur.getHeaderRecordType().equals("response"))
				doc = this.createDocument();
			if (doc != null)
				return doc;
			this.cur = WarcRecord.readNextWarcRecord(this.stream);
		}

		return null;
	}

	private Document createDocument() {
		Document doc = this.converter.parseDocument(this.cur);
		return doc;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		// Log in file
		EngineConfig config = EngineConfig.getInstance();
        FileHandler fh = new FileHandler(config.getLogPath());
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

		WarcParser parser = new WarcParser();
		String[] files = config.getWarcFiles();

		System.out.println("Parsing: " + parser.getDatasetPath() + "/html" 
				+ files[0] + "\n"); // TODO change directory
		parser.open(files[0]);

		int i = 0;
		int notEncoded = 0;
		long limit = 50000;
		
		Document doc = null;
		while ((doc = parser.next())!= null && i<limit) {
			if (doc != null) {
				if (!doc.get("lang").equals("en"))
					System.out.println("[" + (i+1) + ", " + doc.get("lang") + "] " + doc.get("dec") + ", " + doc.get("enc") + ", " + 
								   doc.get("context") + ", " + doc.get("type") + ", " +
								   "[ " + doc.get("title") + " ] " + doc.get("url"));
				
				if (doc.get("dec").equals("default"))
					notEncoded++;
			}
			i++;
		}
		System.out.println("Total file with no certain encoding: " + notEncoded);
		System.out.println("Total file extracted: " + i);

		parser.close();
	}

}
