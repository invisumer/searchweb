package it.uniroma3.searchweb.engine.indexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.parser.html.HtmlEncodingDetector;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlBuilder implements DocumentBuilder {
	private static final Logger logger = Logger.getLogger(HtmlBuilder.class.getName());
	private CharsetDetector detector;
	private HtmlEncodingDetector htmlDetector;
	private boolean cleanHtml;
	
	public HtmlBuilder(boolean clean) {
		this.detector = new CharsetDetector();
		this.htmlDetector = new HtmlEncodingDetector();
		this.cleanHtml = clean;
	}

	@Override
	public Document build(String url, String httpResponse, byte[] htmlStream) {
		/* Now, try to extract html with the right enconding */

		String html = null;
		String enc = null;
		String decoder = null;
		String lang = null;

		try {
			enc = this.getCharsetFromHttp(httpResponse);

			if (enc != null) {
				decoder = "http";
			}

			// find encoding from html meta if necessary
			if (enc == null) {
				enc = this.getCharsetFromMeta(htmlStream);
				if (enc != null) {
					decoder = "meta";
				}
			}

			// try to guess encoding if necessary
			if (enc == null) {
				enc = this.guessEncoding(htmlStream);
				if (enc != null) {
					decoder = "tika";
				}
			}

			if (enc != null && enc.endsWith("_ltr")) // TODO what about
														// IBM*_ltr?
				enc = enc.replace("_ltr", "");

			if (enc != null && enc.endsWith("_rtl")) // TODO what about
														// IBM*_rtl?
				enc = enc.replace("_rtl", "");

			// default encoding
			if (enc == null) {
				enc = "UTF-8";
				decoder = "default";
			}

			// Malformed html corrector
			if (cleanHtml)
				html = this.fixMalformedHtml(htmlStream, enc);
			else
				html = new String(htmlStream, enc);
		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			return null;
		}
		
		/* Extract all the informations */

		if (html == null)
			return null;

		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);

		String title = htmlDoc.title();
		if (htmlDoc.title() == null || htmlDoc.title().isEmpty())
			return null;

		Element bodyEl = htmlDoc.body();
		if (bodyEl == null) // TODO is bodyEl is empty?
			return null;
		String body = bodyEl.text();

		Document record = new Document();
		record.add(new StringField("enc", enc, Store.YES)); // TODO and
															// language?
		record.add(new StringField("dec", decoder, Store.YES));
		record.add(new StringField("url", url, Store.YES));

		 String domain = "";
		 Pattern pattern =
		 Pattern.compile("(https?|ftp|gopher|telnet|file|ldap)://(www[0-9]?(\\.)?)?([a-zA-Z0-9]{3,})\\.(.*)$");
		 Matcher matcher = pattern.matcher(url);
		 while (matcher.find()) {
		 domain = matcher.group(4);
//		 System.out.println("URL: " + matcher.group());
//		 System.out.println("dominio: " + matcher.group(4));
		 }

		TextField domainField = new TextField("domain", domain, Store.YES);
		record.add(domainField);
		
		String domain2 = "";
		Pattern pattern2 = Pattern.compile("(https?|ftp|gopher|telnet|file|ldap)://(www[0-9]?(\\.)?)?([a-zA-Z0-9]{3,})\\.(([a-zA-Z0-9]{3,})\\.)?(.*)$");
		Matcher matcher2 = pattern2.matcher(url);
		while (matcher2.find()) {
			String string = matcher2.group(5);
			if (string != null) {
				domain2 = string.substring(0, string.length() - 1);
			}
//			System.out.println("dominio: " + domain2);
		}
		
//		System.out.println("body: " + htmlDoc.body().text());
//		System.out.println("language: " + this.guessLanguage(htmlStream));
		
		TextField domainField2 = new TextField("domain2", domain2, Store.YES);
		record.add(domainField2);

		TextField titleField = new TextField("title", title, Store.YES);
		// titleField.setBoost(config.getTitleBoost());
		record.add(titleField);

		TextField bodyField = new TextField("body", body, Store.YES);
		// bodyField.setBoost(config.getBodyBoost());
		record.add(bodyField);
		
/* Language */
		
		Element taglang = htmlDoc.select("html").first();
		
		Map<String, String> codlang = new HashMap<String, String>();
		codlang.put("Shift_JIS", "Japanese");
		codlang.put("ISO-2022-JP", "Japanese");
		codlang.put("ISO-2022-CN", "Chinese");
		codlang.put("ISO-2022-KR", "Korean");
		codlang.put("GB18030", "Chinese");
		codlang.put("Big5", "Chinese");
		codlang.put("EUC-JP", "Japanese");
		codlang.put("EUC-KR", "Korean");
		codlang.put("ISO-8859-5", "Russian");
		codlang.put("ISO-8859-6", "Arabic");
		codlang.put("ISO-8859-7", "Greek");
		codlang.put("ISO-8859-8", "Hebrew");
		codlang.put("ISO-8859-9", "Turkish");
		codlang.put("windows-1251", "Russian");
		codlang.put("windows-1253", "Greek");
		codlang.put("windows-1254", "Turkish");
		codlang.put("windows-1255", "Hebrew");
		codlang.put("windows-1256", "Arabic");
		codlang.put("KOI8-R", "Russian");
		codlang.put("IBM420", "Arabic");
		codlang.put("IBM424", "Hebrew");
		if (codlang.get(enc) != null) {
			lang = codlang.get(enc);
		}
		if (lang == null) {
			taglang.attr("lang");
		}
		if (lang == null) {
			lang = this.guessLanguage(htmlStream);
		}
//		if (lang == null) {
//			lang = this.guessLanguage2(htmlDoc.body().text());
//		}
		if (lang == null) {
			lang = "en";
		}
		
//		System.out.println(lang);

		return record;
	}
	
	private String getCharsetFromHttp(String response) {
		Pattern pattern = Pattern
				.compile("(?i)\\bcharset=\\s*\"?([^\\s;,\"]*)");
		Matcher matcher = pattern.matcher(response);
		String enc = null;

		try {
			if (matcher.find()) {
				String group = matcher.group();
				enc = group.replaceAll("(?i)\\bcharset=\\s*\"?", "");
				if (enc.isEmpty() || !Charset.isSupported(enc))
					enc = null;
			}
		} catch (IllegalCharsetNameException e) {
			logger.severe(e.getMessage());
			return null;
		}

		return enc;
	}
	
	private String getCharsetFromMeta(byte[] htmlStream) {
		ByteArrayInputStream stream = new ByteArrayInputStream(htmlStream);
		Charset charset = null;
		String enc = null;

		try {
			charset = this.htmlDetector.detect(stream, null);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			return null;
		}

		if (charset != null)
			enc = charset.name();

		return enc;
	}

	private String guessEncoding(byte[] htmlStream) {
		String enc = null;

		detector.setText(htmlStream);
		CharsetMatch match = detector.detect();
		
		if (match != null && (match.getConfidence() > 33)) // TODO Confidence?
			enc = match.getName();

		return enc;
	}
	
	private String guessLanguage(byte[] htmlStream) {
		String lang = null;

		detector.setText(htmlStream);
		CharsetMatch match = detector.detect();
//		if (match != null && (match.getConfidence() > 10)) // TODO Confidence?
			lang = match.getLanguage();

		return lang;
	}
	
	private String guessLanguage2(String text) {
		String language = null;
		
		LanguageIdentifier identifier = new LanguageIdentifier(text);
//		if (identifier.isReasonablyCertain()) 
			language = identifier.getLanguage();

		return language;
	}

	private String fixMalformedHtml(byte[] inStream, String enc)
			throws UnsupportedEncodingException {
		ByteArrayInputStream in = new ByteArrayInputStream(inStream);
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = null;
		String html = null;

		try {
			node = cleaner.clean(in);
		} catch (Exception e) {
			return null;
		}

		if (node != null)
			html = cleaner.getInnerHtml(node);

		return html;
	}

}
