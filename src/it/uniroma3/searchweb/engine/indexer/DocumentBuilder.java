package it.uniroma3.searchweb.engine.indexer;

import org.apache.lucene.document.Document;

public interface DocumentBuilder {
	
	public Document build(byte[] header, byte[] content);
	
}
