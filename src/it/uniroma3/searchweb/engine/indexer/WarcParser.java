package it.uniroma3.searchweb.engine.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	
	public WarcParser(){}
	public static void main(String[] args) {
		EngineConfig engineConfig = EngineConfig.getInstance();
		String warcFile = engineConfig.getDatasetPath()+"/example.warc";
		File file = new File( warcFile );
	    try {
	        InputStream in = new FileInputStream( file );
	
	        int records = 0;
	        int errors = 0;
	
	        WarcReader reader = WarcReaderFactory.getReader( in );
	        WarcRecord record;
	
	        while ( (record = reader.getNextRecord()) != null ) {
	            printRecord(record);
	
	            ++records;
	
	           
	        }
	
	        System.out.println("--------------");
	        System.out.println("       Records: " + records);
	        System.out.println("        Errors: " + errors);
	        reader.close();
	        in.close();
	    }
	    catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}

	public static void printRecord(WarcRecord record) {
	    System.out.println("--------------");
//	    System.out.println("       Version: " + record.bMagicIdentified + " " + record.bVersionParsed + " " + record.major + "." + record.minor);
//	    System.out.println("       TypeIdx: " + record.warcTypeIdx);
//	    System.out.println("          Type: " + record.warcTypeStr);
//	    System.out.println("      Filename: " + record.warcFilename);
//	    System.out.println("     Record-ID: " + record.warcRecordIdUri);
//	    System.out.println("          Date: " + record.warcDate);
//	    System.out.println("Content-Length: " + record.contentLength);
//	    System.out.println("  Content-Type: " + record.contentType);
//	    System.out.println("     Truncated: " + record.warcTruncatedStr);
//	    System.out.println("   InetAddress: " + record.warcInetAddress);
//	    System.out.println("  ConcurrentTo: " + record.warcConcurrentToUriList);
//	    System.out.println("      RefersTo: " + record.warcRefersToUri);
//	    System.out.println("     TargetUri: " + record.warcTargetUriUri);
//	    System.out.println("   WarcInfo-Id: " + record.warcWarcInfoIdUri);
//	    System.out.println("   BlockDigest: " + record.warcBlockDigest);
//	    System.out.println(" PayloadDigest: " + record.warcPayloadDigest);
//	    System.out.println("IdentPloadType: " + record.warcIdentifiedPayloadType);
//	    System.out.println("       Profile: " + record.warcProfileStr);
//	    System.out.println("      Segment#: " + record.warcSegmentNumber);
//	    System.out.println(" SegmentOrg-Id: " + record.warcSegmentOriginIdUrl);
//	    System.out.println("SegmentTLength: " + record.warcSegmentTotalLength);
//	    System.out.
	}
}
