package it.uniroma3.searchweb.engine.indexer;

// http://boston.lti.cs.cmu.edu/clueweb09/wiki/tiki-index.php?page=Working+with+WARC+Files#Linebreaks_within_the_GZipped_Files

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class BetaWarcParser {
	public String datasetPath;
	
	public BetaWarcParser() {
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
		String inputWarcFile="/Users/kristian/git/searchweb/dataset/example.warc";
	    // open our gzip input stream
	    InputStream gzInputStream=new FileInputStream(inputWarcFile);
	    
	    // cast to a data input stream
	    DataInputStream inStream=new DataInputStream(gzInputStream);
	    
	    // iterate through our stream
	    WarcRecord thisWarcRecord;
	    while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
	      // see if it's a response record
	    	System.out.println(thisWarcRecord.getHeaderRecordType());
	      if (thisWarcRecord.getHeaderRecordType().equals("response")) {
	    	  System.out.println("entry");
	        // it is - create a WarcHTML record
	        WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
	        // get our TREC ID and target URI
	        String thisTRECID=htmlRecord.getTargetTrecID();
	        String thisTargetURI=htmlRecord.getTargetURI();
	        String thisContent=htmlRecord.getRawRecord().getContentUTF8().toString();
	        // print our data
	        System.out.println(thisContent);
	      }
	    }
	    
	    inStream.close();
	}
}
