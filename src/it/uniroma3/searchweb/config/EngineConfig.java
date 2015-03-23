package it.uniroma3.searchweb.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class EngineConfig {

	private static final Logger logger = Logger.getLogger(EngineConfig.class.getName()); 
	private static EngineConfig instance;
	private static String propertyPath = "META-INF/config.properties";
	private String indexPath;
	private String datasetPath;
	
	private EngineConfig() {
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyPath));

			this.indexPath = prop.getProperty("index.path");
			this.datasetPath = prop.getProperty("dataset.path");
		} catch (FileNotFoundException e) {
			this.indexPath = "index";
			this.datasetPath = "dataset";
			e.printStackTrace();
			logger.severe("Property file could not be found: " + e.getMessage());
		} catch (IOException e) {
			this.indexPath = "index";
			this.datasetPath = "dataset";
			e.printStackTrace();
			logger.severe("Properties file could not be loaded: " + e.getMessage());
		}
	}
	
	public static String getPropertyPath() {
		return propertyPath;
	}
	
	public static void setPropertyPath(String path) {
		propertyPath = path;
	}
	
	public String getIndexPath() {
		return indexPath;
	}
	
	public void setIndexPath(String path) {
		this.indexPath = path;
	}
	
	public String getDatasetPath() {
		return datasetPath;
	}
	
	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}

	/** Return the singleton object of this Factory.
	 * 
	 * @return Singleton
	 */
	public static synchronized EngineConfig getInstance() {
		if (instance==null) 
			instance = new EngineConfig();
		return instance;
	}
	
}
