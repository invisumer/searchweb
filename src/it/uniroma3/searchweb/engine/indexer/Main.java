package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Main {

	public static void main(String[] args) {

		try {
			/* Get Engine configurations */
			EngineConfig engineConfig = EngineConfig.getInstance();
			
			/* create a standard analyzer */
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
			/* create the index in the pathToFolder or in RAM (choose one) */
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath()));
			/* set an index congif */
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			config.setOpenMode(OpenMode.CREATE);
			/* create the writer */
			IndexWriter writer = new IndexWriter(index, config);
			/* pick a document */
			String title1 = "Football: News, opinion, previews, results & live scores - Mirror Online";
			String url1 = "http://www.mirror.co.uk/sport/football/transfer-news/";
			String textdoc1 = "The captain of Liverpool football club Steven " + "Gerrard announce he will leave the club";
			String title2 = "Liverpool - Wikipedia";
			String url2 = "https://it.wikipedia.org/wiki/Liverpool";
			String textdoc2 = "Liverpool e' una città di 466 415 abitanti (censimento 2012) del Regno Unito, " + 
			                  "capoluogo dell'omonimo distretto metropolitano e della contea metropolitana inglese " + 
					          "del Merseyside. Sorge lungo l'estuario della Mersey e affaccia sul Mare d'Irlanda, non" + 
			                  " lontano dal confine con il Galles." +
			                  "La nascita di Liverpool viene fatta solitamente risalire all'agosto del 1207, quando re " + 
			                  "Giovanni Senzaterra fece emanare una propria lettera con cui concedeva il privilegio di " + 
			                  "libera citta' all'allora villaggio di Liverpool e invitava inoltre i coloni delle zone " + 
			                  "circostanti a trasferirvisi per trovarvi dimora.";
			/* create the document adding the fields */
			Document doc1 = new Document();
			doc1.add(new TextField("title", title1, Store.YES));
			doc1.add(new StringField("url", url1, Store.YES));
			doc1.add(new TextField("body", textdoc1, Store.YES));
			Document doc2 = new Document();
			doc2.add(new TextField("title", title2, Store.YES));
			doc2.add(new StringField("url", url2, Store.YES));
			doc2.add(new TextField("body", textdoc2, Store.YES));
			doc2.add(new LongField("date", new Date().getTime(), Store.YES));
			/* write the document */
			writer.addDocument(doc1);
			writer.addDocument(doc2);
			/* close the writer */
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
