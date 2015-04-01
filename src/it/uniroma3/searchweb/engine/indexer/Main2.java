package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.AnalyzerMapper;
import it.uniroma3.searchweb.engine.mapper.IndexerMapper;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

public class Main2 {

	public static void main(String[] args) {

		try {
			EngineConfig engineConfig = EngineConfig.getInstance();
			IndexerMapper mapper = new IndexerMapper();
			IndexWriter writer = null;
			AnalyzerMapper analyzers = new AnalyzerMapper();

			WarcParser parser = new WarcParser();
			String[] files = engineConfig.getWarcFiles();
			parser.open(files[0]);

			int counter = 1;
			int maxDoc = 100000;
			int batch = maxDoc / 4;
			long start = System.currentTimeMillis();
			while (counter < maxDoc) {
				Document doc = parser.next();

				if (doc == null)
					break;

				String context = doc.get("context");
				String type = doc.get("type");
				String writerType = null;
				if (context.equals("text") && type.equals("html"))
					writerType = "html";
				if (context.equals("audio"))
					writerType = "audio";
				if (context.equals("image"))
					writerType = "image";
				if (context.equals("video"))
					writerType = "video";
				writer = mapper.pickWriter(writerType);
				if (writer == null)
					continue;
				if (type.equals("html")) {
				writer.addDocument(doc, analyzers.pickAnalyzer(doc.getField("lang").stringValue())); // TODO analyzer depending on language
				}
				System.out.println(counter);
				counter++;

				if (counter % batch == 0)
					writer.commit();
			}

			long stop = System.currentTimeMillis();
			double time = (stop - start) / 1000.0;
			System.out.println(counter + " document(s) added in " + time
					+ " sec");

			parser.close();
			writer.close();

			stop = System.currentTimeMillis();
			time = (stop - start) / 1000.0;
			System.out.println("Closing in " + time + " sec");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
