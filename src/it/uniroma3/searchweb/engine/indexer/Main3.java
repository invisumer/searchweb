package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;
import it.uniroma3.searchweb.engine.mapper.AnalyzerMapper;
import it.uniroma3.searchweb.engine.mapper.IndexerMapper;

import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main3 {

	public static void main(String[] args) {
		int poolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = null;
		CompletionService<Integer> executor = null;
		
		try {
			EngineConfig engineConfig = EngineConfig.getInstance();

			IndexerMapper indexers = new IndexerMapper();
			AnalyzerMapper analyzers = new AnalyzerMapper();

			int nfiles = engineConfig.getWarcFiles().length;
			int filePerThread = (int) Math.ceil(nfiles / (poolSize + 0.0));
			
			pool = Executors.newFixedThreadPool(poolSize);
			executor = new ExecutorCompletionService<Integer>(pool);
			long startTime = System.currentTimeMillis();
			
			for (int i=0; i<nfiles; i+=(filePerThread)) {
				int start = i;
				int stop = (i+filePerThread) > nfiles ? nfiles : i+filePerThread;
				
				WarcParserTask task = new WarcParserTask(indexers, analyzers, start, stop);
				executor.submit(task);
			}
			
			int counter = 0;
			for (int i=0; i<nfiles; i+=(filePerThread))
				counter += executor.take().get();
			
			indexers.close();
			
			long stopTime = System.currentTimeMillis();
			double time = (stopTime-startTime)/1000.0;
			System.out.println(counter + "doc(s) added in: " + time + " secs");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if (pool != null)
				pool.shutdown();
		}
	}

}
