package it.uniroma3.searchweb.engine.indexer;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;

public class WarcConverter {
	private static final Logger logger = Logger.getLogger(WarcConverter.class.getName());
	private StringField contextField;
	private StringField typeField;
	private HtmlBuilder htmlBuilder;
	private ImgBuilder imgBuilder;
	private Mp3Builder mp3Builder;

	public WarcConverter(boolean clean) {
		this.contextField = new StringField("context", "", Store.YES);
		this.typeField = new StringField("type", "", Store.YES);
		this.htmlBuilder = new HtmlBuilder(clean);
		this.imgBuilder = new ImgBuilder();
		this.mp3Builder = new Mp3Builder();
	}

	public Document parseDocument(WarcRecord warc) {
		byte[] contentStream = warc.getContent();

		// get url target
		String url = this.getUrlTarget(warc);

		// get http response
		int httpLen = this.getHttpResponseLength(contentStream);
		String httpResponse = this.getHttpResponse(contentStream, httpLen);
		byte[] htmlStream = this.getBodyStream(contentStream, httpLen + 3);
		
		// get content type
		String[] contentType = this.getContentType(httpResponse);
		
		if (contentType == null)
			return null;
		
		System.out.println(contentType[0] + " " + contentType[1]); // TODO Rimuovere
		
		Document doc = null;
		if (contentType[0].equals("text") && contentType[1].equals("html"))
			doc = this.htmlBuilder.build(url, httpResponse, htmlStream);
		if (contentType[0].equals("image"))
			doc = this.imgBuilder.build(url, httpResponse, htmlStream);
		if (contentType[0].equals("audio"))
			doc = this.mp3Builder.build(url, httpResponse, htmlStream);
		
		if (doc != null) {
			this.contextField.setStringValue(contentType[0]);
			this.typeField.setStringValue(contentType[1]);
			doc.add(this.contextField);
			doc.add(this.typeField);
		}
		
		return doc;
	}

	private String getUrlTarget(WarcRecord warc) {
		WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(warc);
		String url = htmlRecord.getTargetURI();
		return url;
	}

	private int getHttpResponseLength(byte[] contentStream) {
		int i = 1;
		while (!((contentStream[i] == '\r') && (contentStream[i - 1] == '\n'))) {
			i++;
		}
		return i - 1;
	}

	private String getHttpResponse(byte[] contentStream, int length) {
		byte[] httpResponseStream = new byte[length];

		for (int j = 0; j < length; j++)
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

	private byte[] getBodyStream(byte[] contentStream, int start) {
		byte[] htmlStream = new byte[contentStream.length - start];
		for (int k = 0; k < htmlStream.length; k++)
			htmlStream[k] = contentStream[k + start];
		return htmlStream;
	}

	private String[] getContentType(String httpResponse) {
		Pattern pattern = Pattern.compile("Content-Type:\\s*\"?([^\\s;,\"]*)");
		Matcher matcher = pattern.matcher(httpResponse);
		
		String type = "unknown";
		
		if (matcher.find()) {
			type = matcher.group();
			type = type.replaceAll("Content-Type:\\s*\"?", "");
			if (type.isEmpty())
				type = "unknown";
		}
		
		type = type.toLowerCase();
		String[] meta = type.split("/");
		
		if (meta.length != 2)
			return null;
		
		if (!meta[0].equals("application") && !meta[0].equals("text") && 
			!meta[0].equals("image") && !meta[0].equals("audio") && !meta[0].equals("video") && 
			!meta[0].equals("model") && !meta[0].equals("multipart") && !meta[0].equals("message"))
			return null;
		
		if (meta[1].equals(""))
			return null;
		
		return meta;
	}
}
