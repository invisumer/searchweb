package it.uniroma3.searchweb.engine.indexer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.tidy.Tidy;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;

public class WarcAdapter {
	private static final Logger logger = Logger.getLogger(WarcAdapter.class.getName()); 
	private CharsetDetector detector;
	
	public WarcAdapter(CharsetDetector detector) {
		this.detector = detector;
	}
	
	public Document parseDocument(WarcRecord warc) {
		byte[] contentStream = warc.getContent();
		
		// get url target
		String url = this.getUrlTarget(warc);
		
		// get http response
		int httpLen = this.getHttpResponseLength(contentStream);
		String httpResponse = this.getHttpResponse(contentStream, httpLen);
		byte[] htmlStream = this.getBodyStream(contentStream, httpLen+3);
//		System.out.println(httpResponse);
		
		/* Now, try to extract html with the right enconding */
		
		String html = null;
		String enc = null;
		String decoder = null;
		
		try {
			enc = this.getCharsetFromHttp(httpResponse);
			
			if (enc != null) {
//				html = new String(htmlStream, enc);
				decoder = "http"; 
			}
			
			// find encoding from html meta if necessary
			if (enc == null) {
				String htmlMeta = new String(htmlStream, "UTF-8");
				enc = this.getCharsetFromMeta(htmlMeta);
				if (enc != null) {
//					html = new String(htmlStream, enc);
					decoder = "meta";
				}
			}
			
			// try to guess encoding if necessary
			if (enc == null) {
				enc = this.guessEncoding(htmlStream);
				if (enc != null) {
//					html = new String(htmlStream, enc);
					decoder = "tika";
				}
			}
			
			// default encoding
			if (enc == null) {
				enc = "UTF-8";
//				html = new String(htmlStream, enc);
				decoder = "default";
			}
			
			// Malformed html corrector
			html = new String(htmlStream, enc);
//			html = this.fixMalformedHtml(htmlStream, enc);
//			System.out.println(html);
		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
//			e.printStackTrace();
			return null;
		}
			
		/* Extract all the informations */
		
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
		
		String title = htmlDoc.title();
		if (htmlDoc.title() == null || htmlDoc.title().isEmpty())
			return null;
		
		Element bodyEl = htmlDoc.body();
		if (bodyEl == null)
			return null;
		String body = bodyEl.text();

		Document record = new Document();
		record.add(new StringField("enc", enc, Store.YES));     // TODO and language?
		record.add(new StringField("dec", decoder, Store.YES));
		record.add(new StringField("url", url, Store.YES));
		
		TextField titleField = new TextField("title", title, Store.YES);
		titleField.setBoost(2f);
		record.add(titleField);
		
		TextField bodyField = new TextField("body", body, Store.YES);
		bodyField.setBoost(1f);
		record.add(bodyField);

		return record;
	}
	
	private String getUrlTarget(WarcRecord warc) {
		WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(warc);
		String url = htmlRecord.getTargetURI();
		return url;
	}
	
	private int getHttpResponseLength(byte[] contentStream) {
		int i=1;
		while(!((contentStream[i] == '\r') && (contentStream[i-1] == '\n'))) {
			i++;
		}
		return i-1;
	}
	
	private String getHttpResponse(byte[] contentStream, int length) {
		byte[] httpResponseStream = new byte[length];
		
		for (int j=0; j<length; j++)
			httpResponseStream[j] = contentStream[j];
		
		String httpResponse = null;
		
		try {
			httpResponse = new String(httpResponseStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return httpResponse;
	}
	
	private String getCharsetFromHttp(String response) {
		Pattern pattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
		Matcher matcher = pattern.matcher(response);
		String enc = null;
		
		if (matcher.find()) {
			String group = matcher.group();
			enc = group.replaceAll("(?i)\\bcharset=\\s*\"?", "");
			if (enc.isEmpty())
				enc = null;
		}
		
		return enc;
	}
	
	private byte[] getBodyStream(byte[] contentStream, int start) {
		byte[] htmlStream = new byte[contentStream.length-start];
		for (int k=0; k<htmlStream.length; k++)
			htmlStream[k] = contentStream[k+start];
		return htmlStream;
	}
	
	private String getCharsetFromMeta(String html) {
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
		Elements metaEls = htmlDoc.head().select("meta[charset],meta[http-equiv=Content-Type]");
		String enc = null;
		
		String metaEnc = metaEls.attr("charset");
		if (metaEnc != null && !metaEnc.isEmpty())
			enc = metaEnc.replaceAll("[\"]*", "");
		if (enc!=null && enc.isEmpty())
			enc = null;
		
		if (enc == null) {
			metaEnc = metaEls.attr("content");
			if (metaEnc != null & !metaEnc.isEmpty())
				enc = this.getCharsetFromHttp(metaEnc);
		}
		
		return enc;
	}
	
	private String guessEncoding(byte[] htmlStream) {
		String enc = null;
		
		detector.setText(htmlStream);
		CharsetMatch match = detector.detect();
		if (match != null)
			enc = match.getName();
		
		return enc;
	}
	
	private String fixMalformedHtml(byte[] inStream, String enc) throws UnsupportedEncodingException {
		Tidy tidy = new Tidy();
		
	    tidy.setInputEncoding(enc);
	    tidy.setOutputEncoding(enc);
	    tidy.setPrintBodyOnly(false);
	    tidy.setQuiet(true);
	    tidy.setShowErrors(0);
//	    tidy.setErrout(null);
	    tidy.setShowWarnings(false);
//	    tidy.setWraplen(Integer.MAX_VALUE);
	    tidy.setForceOutput(true);
	    tidy.setMakeClean(true);
	    
	    ByteArrayInputStream inputStream = new ByteArrayInputStream(inStream);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    tidy.parse(inputStream, outputStream);
	    
	    String html = new String(outputStream.toByteArray(), enc);
	    return html;
	}

}
