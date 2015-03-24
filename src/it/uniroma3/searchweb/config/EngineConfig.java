package it.uniroma3.searchweb.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;

public class EngineConfig {

	private static final Logger logger = Logger.getLogger(EngineConfig.class.getName()); 
	private static EngineConfig instance;
	private static String propertyPath = "META-INF/config.properties";
	private String indexPath;
	private String datasetPath;
	private OpenMode indexOpenMode;
	private double RAMBufferSize;
	
	private EngineConfig() {
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyPath));
			
			// mandatory properties
			this.indexPath = prop.getProperty("index.path");
			this.datasetPath = prop.getProperty("dataset.path");
			
			// open mode
			String mode = prop.getProperty("index.openmode");
			if (mode == null)
				this.indexOpenMode = OpenMode.APPEND; // default
			else if (mode.equals("create"))
				this.indexOpenMode = OpenMode.CREATE;
			else if (mode.equals("append"))
				this.indexOpenMode = OpenMode.APPEND;
			else if (mode.equals("createOrAppend"))
				this.indexOpenMode = OpenMode.CREATE_OR_APPEND;
			else
				this.indexOpenMode = OpenMode.APPEND; // default
			
			// RAM buffer size
			this.RAMBufferSize = 16;
			String size = prop.getProperty("index.rambuffersize");
			if (size != null)
				this.RAMBufferSize = Double.parseDouble(size);
			
			logger.info("Index path: " + this.indexPath);
			logger.info("Dataset path: " + this.datasetPath);
			logger.info("Index open mode: " + this.indexOpenMode);
			logger.info("RAM buffer size: " + this.RAMBufferSize);
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
	
	public OpenMode getIndexOpenMode() {
		return indexOpenMode;
	}
	
	public void setIndexOpenMode(OpenMode indexOpenMode) {
		this.indexOpenMode = indexOpenMode;
	}
	
	public double getRAMBufferSize() {
		return RAMBufferSize;
	}
	
	public void setRAMBufferSize(double rAMBufferSize) {
		RAMBufferSize = rAMBufferSize;
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
