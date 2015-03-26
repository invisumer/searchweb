package it.uniroma3.searchweb.engine.indexer;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class DocumentBuilder {
	private CharsetDetector detector;
	
	public DocumentBuilder(CharsetDetector detector) {
		this.detector = detector;
	}
	
	public Document create(WarcRecord warc) throws UnsupportedEncodingException {
		// get url target
		WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(warc);
		String url = htmlRecord.getTargetURI();
		
		// extract http response
		byte[] contentStream = warc.getContent();
		int i=1;
		while(!((contentStream[i] == '\r') && (contentStream[i-1] == '\n'))) {
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
			// Malformed html corrector
//			Tidy tidy = new Tidy();
//			tidy.setXHTML(true);
//			ByteArrayOutputStream corrected = new ByteArrayOutputStream(htmlStream.length);
//			tidy.parse(new ByteArrayInputStream(htmlStream), corrected);
//			htmlStream = corrected.toByteArray();
			
			html = new String(htmlStream, enc);
		} catch (UnsupportedEncodingException e) {
			encPrec = "default";
			enc = "UTF-8";
			
			// Malformed html corrector
//			Tidy tidy = new Tidy();
//			tidy.setXHTML(true);
//			ByteArrayOutputStream corrected = new ByteArrayOutputStream(htmlStream.length);
//			tidy.parse(new ByteArrayInputStream(htmlStream), corrected);
//			htmlStream = corrected.toByteArray();
			
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
					// Malformed html corrector
//					Tidy tidy = new Tidy();
//					tidy.setXHTML(true);
//					ByteArrayOutputStream corrected = new ByteArrayOutputStream(htmlStream.length);
//					tidy.parse(new ByteArrayInputStream(htmlStream), corrected);
//					htmlStream = corrected.toByteArray();
					
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
					// Malformed html corrector
//					Tidy tidy = new Tidy();
//					tidy.setXHTML(true);
//					ByteArrayOutputStream corrected = new ByteArrayOutputStream(htmlStream.length);
//					tidy.parse(new ByteArrayInputStream(htmlStream), corrected);
//					htmlStream = corrected.toByteArray();
					
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

}
