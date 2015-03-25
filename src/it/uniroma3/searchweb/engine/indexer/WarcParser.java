package it.uniroma3.searchweb.engine.indexer;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private String dataset;
	private DataInputStream stream;
	private WarcRecord cur;
	private CharsetDetector detector;

	public WarcParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.dataset = config.getDatasetPath();
		this.detector = new CharsetDetector();
	}

	public WarcParser(String datasetPath) {
		this.dataset = datasetPath;
		this.detector = new CharsetDetector();
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

	private Document createDocument() throws IOException {
		// get url target
		WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(this.cur);
		String url = htmlRecord.getTargetURI();
		
		// extract http response
		byte[] contentStream = this.cur.getContent();
		int i=0;
		while(!(contentStream[i] == '\r' && (i>0) && contentStream[i-1] == '\n')) {
			i++;
		}
		
		byte[] httpResponseStream = new byte[i-1];
		for (int j=0; j<httpResponseStream.length; j++) {
			httpResponseStream[j] = contentStream[j];
		}
		String httpResponse = new String(httpResponseStream, "UTF-8");
		
		// try extract html
		byte[] htmlStream = new byte[contentStream.length-(i+2)];
		for (int k=i+2; k<contentStream.length; k++) {
			htmlStream[k-i-2] = contentStream[k];
		}
		
		Pattern pattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
		Matcher matcher = pattern.matcher(httpResponse);
		String enc = "UTF-8";
		String encPrec = "default";
		boolean foundCharset = false;
		
		// find encoding in http response
		if (matcher.find()) {
			String group = matcher.group();
			enc = group.replaceAll("(?i)\\bcharset=\\s*\"?", "");
			foundCharset = true;
			if (enc.isEmpty()) {
				enc = "UTF-8";
				foundCharset = false;
			}
			if (foundCharset)
				encPrec = "http";
		}
		
		String html = "";
		
		try {
			html = new String(htmlStream, enc);
		} catch (UnsupportedEncodingException e) {
			encPrec = "default";
			enc = "UTF-8";
			html = new String(htmlStream, enc);
			foundCharset = false;
		}
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
		
		// find encoding in html meta
		if (!foundCharset) {
			Elements metaEls = htmlDoc.select("meta");
			String metaEnc = metaEls.attr("charset");
			
			if (metaEnc != null && !metaEnc.isEmpty()) {
				enc = metaEnc.replaceAll("[\"]*", "");
				foundCharset = true;

				try {
					html = new String(htmlStream, enc);
				} catch (UnsupportedEncodingException e) {
					enc = "UTF-8";
//					html = new String(htmlStream, enc);
					foundCharset = false;
				}
				
				if (foundCharset)
					encPrec = "html";
			}
		}
		
		// TODO short form of metadata
		
		// try to guess encoding
		if (!foundCharset) {
			detector.setText(htmlStream);
			CharsetMatch match = detector.detect();
			
			if (match != null) {
				enc = match.getName();
				foundCharset = true;
				
				try {
					html = new String(htmlStream, enc);
				} catch(UnsupportedEncodingException e) {
					enc = "UTF-8";
//					html = new String(htmlStream, enc);
					foundCharset = false;
				}
				
				if (foundCharset)
					encPrec = "tika";
			}
		}
		
		if (!foundCharset) {
			encPrec = "default";
		} else {
			htmlDoc = Jsoup.parse(html);
		}
		
		// TODO how to solve non correctly indexed documents?
		String title = htmlDoc.title();
		if (htmlDoc.title() == null || htmlDoc.title().isEmpty())
			return null;
		
		Element bodyEl = htmlDoc.body();
		if (bodyEl == null)
			return null;
		String body = bodyEl.text();

		Document record = new Document();
		record.add(new StringField("enc", enc, Store.YES));     // TODO and language?
		record.add(new StringField("encPrec", encPrec, Store.YES));
		record.add(new StringField("title", title, Store.YES));
		record.add(new StringField("url", url, Store.YES));
		record.add(new TextField("body", body, Store.YES));

		return record;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		// Log in file
        FileHandler fh = new FileHandler("/home/redox/java.log");
//        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

		WarcParser parser = new WarcParser();
		EngineConfig config = EngineConfig.getInstance();
		String[] files = config.getWarcFiles();

		System.out.println("Parsing: " + parser.getDatasetPath() + "/"
				+ files[0] + "\n");
		parser.open(files[0]);

		int i = 0;
		int notEncoded = 0;
		long limit = 50;
		
		Document doc = null;
		while ((doc = parser.next())!= null && i<limit) {
			if (doc != null) {
//				System.out.println("---------- Response " + (i+1) + " ----------");
//				System.out.println(doc.get("encPrec") + " -> " + doc.get("enc"));
//				System.out.println();
//				System.out.println(doc.get("url"));
//				System.out.println();
//				System.out.println(doc.get("title"));
//				System.out.println();
//				System.out.println(doc.get("body"));
//				System.out.println();
				
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
