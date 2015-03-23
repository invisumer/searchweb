package it.uniroma3.searchweb.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class EngineConfig {

	private static final Logger logger = Logger.getLogger(EngineConfig.class.getName()); 
	private static EngineConfig instance;
	private static String propertyPath = "META-INF/config.properties";
	private String path;
	
	private EngineConfig() {
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyPath));

			this.path = prop.getProperty("index.path");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.severe("Property file could not be found: " + e.getMessage());
		} catch (IOException e) {
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
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
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
