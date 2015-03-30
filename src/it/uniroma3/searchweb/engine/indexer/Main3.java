package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Main3 {

	public static void main(String[] args) {
		int poolSize = Runtime.getRuntime().availableProcessors();
//		int poolSize = 2;
		ExecutorService pool = null;
		CompletionService<Integer> executor = null;
		
		try {
			EngineConfig engineConfig = EngineConfig.getInstance();
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
			Directory index = FSDirectory.open(new File(engineConfig.getIndexPath() + "/html")); // TODO temporaneamente
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			config.setRAMBufferSizeMB(engineConfig.getRAMBufferSize());
			config.setOpenMode(engineConfig.getIndexOpenMode());
			IndexWriter writer = new IndexWriter(index, config);
			int nfiles = engineConfig.getWarcFiles().length;
//			int nfiles = 2;
			int filePerThread = (int) Math.ceil(nfiles / (poolSize + 0.0));
			
			pool = Executors.newFixedThreadPool(poolSize);
			executor = new ExecutorCompletionService<Integer>(pool);
			long startTime = System.currentTimeMillis();
			
			for (int i=0; i<nfiles; i+=(filePerThread)) {
				int start = i;
				int stop = (i+filePerThread) > nfiles ? nfiles : i+filePerThread;
				
				WarcParserTask task = new WarcParserTask(writer, start, stop);
				executor.submit(task);
			}
			
			int counter = 0;
			for (int i=0; i<nfiles; i+=(filePerThread))
				counter += executor.take().get();
			
			writer.close();
			
			long stopTime = System.currentTimeMillis();
			double time = (stopTime-startTime)/1000.0;
			System.out.println(counter + "doc(s) added in: " + time + " secs");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pool != null)
				pool.shutdown();
		}
	}

}
