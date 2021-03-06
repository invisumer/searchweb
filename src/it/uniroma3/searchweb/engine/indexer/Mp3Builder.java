package it.uniroma3.searchweb.engine.indexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Mp3Builder implements DocumentBuilder {
	private static final Logger logger = Logger.getLogger(Mp3Builder.class.getName());
	private StringField urlField;
	private StringField decField;
	private StringField encField;
	private TextField titleField;
	private TextField artistField;
	private TextField genreField;
	
	public Mp3Builder() {
		this.urlField = new StringField("url", "", Store.YES);
		this.decField = new StringField("dec", "", Store.YES);
		this.encField = new StringField("enc", "", Store.YES);
		this.titleField = new TextField("title", "", Store.YES);
		this.artistField = new TextField("artist", "", Store.YES);
		this.genreField = new TextField("genre", "", Store.YES);
	}

	@Override
	public Document build(String url, String httpResponse, byte[] htmlStream) {
		Document doc = null;
		
		try {
			InputStream input = new ByteArrayInputStream(htmlStream);
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();
			
			String title = metadata.get("title");
			String artist = metadata.get("xmpDM:artist");
			String genre = metadata.get("xmpDM:genre");
			
			doc = new Document();
			
			this.urlField.setStringValue(url);
			this.decField.setStringValue("streamer");
			this.encField.setStringValue("byte");
			this.titleField.setStringValue(title == null ? "unknown" : title);
			this.artistField.setStringValue(artist == null ? "unknown" : artist);
			this.genreField.setStringValue(genre == null ? "unknown" : genre);
			
			doc.add(this.urlField);
			doc.add(this.decField);
			doc.add(this.encField);
			doc.add(this.titleField);
			doc.add(this.artistField);
			doc.add(this.genreField);
			
		} catch (SAXException | TikaException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return doc;
	}

}
