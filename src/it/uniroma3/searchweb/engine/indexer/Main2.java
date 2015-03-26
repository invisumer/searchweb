package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Main2 {

	public static void main(String[] args) {

		try {
			EngineConfig engineConfig = EngineConfig.getInstance();

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			config.setRAMBufferSizeMB(engineConfig.getRAMBufferSize());
			config.setOpenMode(engineConfig.getIndexOpenMode());
			IndexWriter writer = new IndexWriter(index, config);
			
			WarcParser parser = new WarcParser();
			String[] files = engineConfig.getWarcFiles();
			parser.open(files[0]);
			
			int counter = 1;
			int maxDoc = 4000;
			long start = System.currentTimeMillis();
			while (counter < maxDoc) {                            // TODO cambiare
				Document doc = parser.next();
				
				if (doc == null)
					break;
					
				writer.addDocument(doc);
				counter++;
			}
			
			long stop = System.currentTimeMillis();
			// TODO logger for index time
			double time = (stop-start)/1000.0;
			System.out.println(counter + " document(s) added in " + time + " sec");
			
			parser.close();
			writer.close();
			
			stop = System.currentTimeMillis();
			time = (stop-start)/1000.0;
			System.out.println("Closing in " + time + " sec");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
