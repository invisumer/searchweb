package it.uniroma3.searchweb.engine.indexer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private String dataset;
	private DataInputStream stream;
	private WarcRecord cur;

	public WarcParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.dataset = config.getDatasetPath();
	}

	public WarcParser(String datasetPath) {
		this.dataset = datasetPath;
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

	public String[] getWarcFiles() {
		File datasetFolder = new File(this.getDatasetPath());
		File[] warcFiles = datasetFolder.listFiles();
		String[] filenames = new String[warcFiles.length];

		for (int i = 0; i < warcFiles.length; i++) {
			filenames[i] = warcFiles[i].getName();
		}

		Arrays.sort(filenames);

		return filenames;
	}

	private Document createDocument() throws IOException {
		WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(this.cur);
		String url = htmlRecord.getTargetURI();
		
		// extract body and title
		String html = htmlRecord.getRawRecord().getContentUTF8();
		html = html.substring(html.indexOf("\n\r\n")+3, html.length());
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
		
		String title = htmlDoc.title();
		if (htmlDoc.title() == null || htmlDoc.title().isEmpty())
			return null;
		
		Element bodyEl = htmlDoc.body();
		if (bodyEl == null)
			return null;
		String body = bodyEl.text();

		Document record = new Document();
		record.add(new StringField("title", title, Store.YES));
		record.add(new StringField("url", url, Store.YES));
		record.add(new TextField("body", body, Store.YES));

		return record;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		WarcParser parser = new WarcParser();
		String[] files = parser.getWarcFiles();

		System.out.println("Parsing: " + parser.getDatasetPath() + "/"
				+ files[0] + "\n");
		parser.open(files[0]);

		int i = 0;
		int ndocs = 10;

		while (i < ndocs) {
			Document doc = parser.next();
			if (doc != null) {
				System.out.println("---------- Response " + (i+1) + " ----------");
				System.out.println(doc.get("url"));
				System.out.println();
				System.out.println(doc.get("title"));
				System.out.println();
				System.out.println(doc.get("body"));
				System.out.println();
			}
			i++;
		}

		parser.close();
	}

}
