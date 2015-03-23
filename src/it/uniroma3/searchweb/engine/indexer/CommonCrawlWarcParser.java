package it.uniroma3.searchweb.engine.indexer;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import edu.cmu.lemurproject.WarcHTMLResponseRecord;
import edu.cmu.lemurproject.WarcRecord;
import it.uniroma3.searchweb.config.EngineConfig;

public class CommonCrawlWarcParser {
	public String datasetPath;
	
	public CommonCrawlWarcParser() {
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
		// use a callback class for handling WARC record data:
		String inputWarcFile="/home/redox/Scaricati/CC-MAIN-20140820021320-00000-ip-10-180-136-8.ec2.internal.warc.gz";
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));
		DataInputStream inStream=new DataInputStream(gzInputStream);
		WarcRecord thisWarcRecord;
		int i = 0;
		while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null && i<1000) {
		if (thisWarcRecord.getHeaderRecordType().equals("response")) {
		WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
		String thisTargetURI=htmlRecord.getTargetURI();
		String thisContentUtf8 = htmlRecord.getRawRecord().getContentUTF8();
		// handle WARC record content:
		System.out.println(thisTargetURI);
//		System.out.println(thisContentUtf8);
		System.out.println("------------------------------------>");
		}
		i++;
		}
		inStream.close();
		// done processing all WARC records:
	}

}
