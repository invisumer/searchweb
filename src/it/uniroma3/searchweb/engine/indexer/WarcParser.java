package it.uniroma3.searchweb.engine.indexer;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.lucene.document.Document;
import org.apache.tika.parser.txt.CharsetDetector;

import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private static final Logger logger = Logger.getLogger(WarcParser.class.getName()); 
	private String dataset;
	private DataInputStream stream;
	private WarcRecord cur;
	private DocumentBuilder builder;

	public WarcParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.dataset = config.getDatasetPath();
		CharsetDetector detector = new CharsetDetector();
		this.builder = new DocumentBuilder(detector);
	}

	public WarcParser(String datasetPath) {
		this.dataset = datasetPath;
		CharsetDetector detector = new CharsetDetector();
		this.builder = new DocumentBuilder(detector);
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
		Document doc = null;
		
		try {
			doc = this.builder.create(this.cur);
		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return doc;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		// Log in file
//        FileHandler fh = new FileHandler("/home/redox/java.log");
//        logger.addHandler(fh);
//        SimpleFormatter formatter = new SimpleFormatter();
//        fh.setFormatter(formatter);

		WarcParser parser = new WarcParser();
		EngineConfig config = EngineConfig.getInstance();
		String[] files = config.getWarcFiles();

		System.out.println("Parsing: " + parser.getDatasetPath() + "/"
				+ files[0] + "\n");
		parser.open(files[0]);

		int i = 0;
		int notEncoded = 0;
		long limit = 10;
		
		Document doc = null;
		while ((doc = parser.next())!= null && i<limit) {
			if (doc != null) {
				System.out.println("---------- Response " + (i+1) + " ----------");
				System.out.println(doc.get("encPrec") + " -> " + doc.get("enc"));
				System.out.println();
				System.out.println(doc.get("url"));
				System.out.println();
				System.out.println(doc.get("title"));
				System.out.println();
				System.out.println(doc.get("body"));
				System.out.println();
				
				if (doc.get("encPrec").equals("default"))
					notEncoded++;
			}
			i++;
		}
		System.out.println("Total file with no certain encoding: " + notEncoded);
		System.out.println("Total file extracted: " + i);

		parser.close();
	}

}
