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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
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
			config.setOpenMode(OpenMode.APPEND);
			IndexWriter writer = new IndexWriter(index, config);
			
			int batch = 10000;
			boolean docFinished = false;
			WarcParser parser = new WarcParser();
			String[] files = parser.getWarcFiles();
			parser.open(files[1]);
			
			while (!docFinished) {
				for (int i=0; i<batch; i++) {
					Document doc = parser.next();
					
					if (doc == null) {
						docFinished = true;
						break;
					}
						
					writer.addDocument(doc);
				}
				System.out.println("Batch committed");
				writer.commit();
			}
			
			parser.close();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
