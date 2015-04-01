package it.uniroma3.searchweb.engine.indexer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.cmu.lemurproject.WarcRecord;

public class Main4 {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String filepath = "/home/redox/dataset/CommonCrawler/CC-MAIN-20140820021320-00003-ip-10-180-136-8.ec2.internal.warc.gz";
		GZIPInputStream gzInputStream = new GZIPInputStream(
				new FileInputStream(filepath));
		DataInputStream stream = new DataInputStream(gzInputStream);
		PrintWriter writer = new PrintWriter("/home/redox/types4", "UTF-8");
		WarcRecord cur = WarcRecord.readNextWarcRecord(stream);
		
		int z = 0;
		while (cur != null && z<Integer.MAX_VALUE) {
			if (cur.getHeaderRecordType().equals("response")) {
				byte[] contentStream = cur.getContent();
				
				int i = 1;
				while (!((contentStream[i] == '\r') && (contentStream[i - 1] == '\n'))) {
					i++;
				}
				i = i - 1;
				
				byte[] httpResponseStream = new byte[i];

				for (int j = 0; j < i; j++)
					httpResponseStream[j] = contentStream[j];

				String httpResponse = null;

				httpResponse = new String(httpResponseStream, "UTF-8");
				
				byte[] htmlStream = new byte[contentStream.length - (i + 3)];
				for (int k = 0; k < htmlStream.length; k++)
					htmlStream[k] = contentStream[k + (i + 3)];
				
				Pattern pattern = Pattern
						.compile("Content-Type:\\s*\"?([^\\s;,\"]*)");
				Matcher matcher = pattern.matcher(httpResponse);
				
				String type = "default";
				
				if (matcher.find()) {
					type = matcher.group();
					type = type.replaceAll("Content-Type:\\s*\"?", "");
					if (type.isEmpty())
						type = "unknown";
				}
				
				type = type.toLowerCase();
				String[] meta = type.split("/");
				
				if (meta.length == 2) {
					writer.println(meta[0] + " " + meta[1]);
		
					
					if (meta[0].equals("audio")) {
						System.out.println(meta[0] + " " + meta[1]);
						InputStream input = new ByteArrayInputStream(htmlStream);
						ContentHandler handler = new DefaultHandler();
						Metadata metadata = new Metadata();
						Parser parser = new Mp3Parser();
						ParseContext parseCtx = new ParseContext();
						try {
							parser.parse(input, handler, metadata, parseCtx);
						} catch (SAXException | TikaException e) {
							e.printStackTrace();
						}
						input.close();
						 
						// List all metadata
						String[] metadataNames = metadata.names();
						
						for(String name : metadataNames){
							System.out.print(name + ", ");
						}
						System.out.println();
						 
						// Retrieve the necessary info from metadata
						// Names - title, xmpDM:artist etc. - mentioned below may differ based
						// on the standard used for processing and storing standardized and/or
						// proprietary information relating to the contents of a file.
						 
						System.out.println("Title: " + metadata.get("title"));
						System.out.println("Artists: " + metadata.get("xmpDM:artist"));
						System.out.println("Genre: " + metadata.get("xmpDM:genre"));
					}
				}
			}
			cur = WarcRecord.readNextWarcRecord(stream);
			z++;
		}
		
		
		stream.close();
		writer.close();
	}

}
