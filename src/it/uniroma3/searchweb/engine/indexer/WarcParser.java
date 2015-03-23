package it.uniroma3.searchweb.engine.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private String datasetPath;
	
	public WarcParser(){
		EngineConfig config = EngineConfig.getInstance();
		this.datasetPath = config.getDatasetPath();	
	}
	
	public String getDatasetPath() {
		return datasetPath;
	}
	
	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}
	
	public static void main(String[] args) {
		WarcParser parser = new WarcParser();
        File file = new File( parser.getDatasetPath() + "/example.warc" );
        
        try {
            InputStream in = new FileInputStream( file );
 
            int records = 0;
            int errors = 0;
 
            WarcReader reader = WarcReaderFactory.getReader( in );
            WarcRecord record;
 
            while ( (record = reader.getNextRecord()) != null ) {
                printRecord(record);
//                printRecordErrors(record);
 
                ++records;
 
//                if (record.hasErrors()) {
//                    errors += record.getValidationErrors().size();
//                }
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
//        System.out.println("       Version: " + record.bMagicIdentified + " " + record.bVersionParsed + " " + record.major + "." + record.minor);
//        System.out.println("       TypeIdx: " + record.warcTypeIdx);
//        System.out.println("          Type: " + record.warcTypeStr);
//        System.out.println("      Filename: " + record.warcFilename);
//        System.out.println("     Record-ID: " + record.warcRecordIdUri);
//        System.out.println("          Date: " + record.warcDate);
        System.out.println("Content-Length: " + record.getHeader("Content-Length").value);
        System.out.println("  Content-Type: " + record.getHeader("Content-Type").value);
//        System.out.println("     Truncated: " + record.warcTruncatedStr);
//        System.out.println("   InetAddress: " + record.warcInetAddress);
//        System.out.println("  ConcurrentTo: " + record.warcConcurrentToUriList);
//        System.out.println("      RefersTo: " + record.warcRefersToUri);
//        System.out.println("     TargetUri: " + record.warcTargetUriUri);
//        System.out.println("   WarcInfo-Id: " + record.warcWarcInfoIdUri);
//        System.out.println("   BlockDigest: " + record.warcBlockDigest);
//        System.out.println(" PayloadDigest: " + record.warcPayloadDigest);
//        System.out.println("IdentPloadType: " + record.warcIdentifiedPayloadType);
//        System.out.println("       Profile: " + record.warcProfileStr);   

			System.out.println("      Segment#: " + record.getHeader("Payload"));
		
//        System.out.println(" SegmentOrg-Id: " + record.warcSegmentOriginIdUrl);
//        System.out.println("SegmentTLength: " + record.warcSegmentTotalLength);
    }
 
//    public static void printRecordErrors(WarcRecord record) {
//        if (record.hasErrors()) {
//            Collection<WarcValidationError> errorCol = record.getValidationErrors();
//            if (errorCol != null && errorCol.size() > 0) {
//                Iterator<WarcValidationError> iter = errorCol.iterator();
//                while (iter.hasNext()) {
//                    WarcValidationError error = iter.next();
//                    System.out.println( error.error );
//                    System.out.println( error.field );
//                    System.out.println( error.value );
//                }
//            }
//        }
//    }
}
