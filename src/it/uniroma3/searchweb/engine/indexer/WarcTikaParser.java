package it.uniroma3.searchweb.engine.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import it.uniroma3.searchweb.config.EngineConfig;

public class WarcTikaParser {
	public String datasetPath;
	
	public WarcTikaParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.datasetPath = config.getDatasetPath();
	}
	
	public String getDatasetPath() {
		return datasetPath;
	}
	
	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}
	
	public static void main(String[] args) throws IOException, SAXException, TikaException {
		//WarcTikaParser parser = new WarcTikaParser();

	      Scanner scanner = new Scanner(System.in);
	      System.out.println("enter path of your file");
	      
	      String filepath = scanner.nextLine();
	      File file = new File(filepath);
	      Parser parser = new AutoDetectParser();
	      BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      InputStream inputstream = new FileInputStream(file);
	      ParseContext context = new ParseContext();
	      
	      parser.parse(inputstream, handler, metadata, context);
	   
	      //printing all the meta data elements after adding new elements
	      System.out.println("Here is the list of all the metadata elements  after adding new elements ");
	      System.out.println( Arrays.toString(metadata.names()));
	}
}
