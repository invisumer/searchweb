package it.uniroma3.searchweb.engine.indexer;

import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	private String datasetPath;
	
	public WarcParser() {
		EngineConfig config = EngineConfig.getInstance();
		this.datasetPath = config.getDatasetPath();
	}
	
	public String getDatasetPath() {
		return datasetPath;
	}
	
	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}
}
