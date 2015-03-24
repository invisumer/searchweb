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
	private String indexPath = "index";
	private String datasetPath = "dataset";
	private String dictionaryPath = "dictionary";
	private String plainPath = "plain";
	private OpenMode indexOpenMode = OpenMode.APPEND;
	private double RAMBufferSize = 16;
	private boolean debugMode = false;
	private int numTopScoreExplanation = 0;
	
	private EngineConfig() {
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyPath));
			
			// mandatory properties
			this.indexPath = prop.getProperty("index.path");
			this.datasetPath = prop.getProperty("dataset.path");
			this.dictionaryPath = prop.getProperty("lucene.dictionary.path");
			this.plainPath = prop.getProperty("lucene.plain.path");
			
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
			String size = prop.getProperty("index.rambuffersize");
			if (size != null)
				this.RAMBufferSize = Double.parseDouble(size);
			
			// Debug mode
			String debug = prop.getProperty("searcher.debugmode");
			if (debug != null)
				this.debugMode = Boolean.parseBoolean(debug);
			
			// Number of top score explanation
			String num = prop.getProperty("debug.numexplanations");
			if (this.debugMode)
				this.numTopScoreExplanation = 10;
			if (this.debugMode && num != null)
				this.numTopScoreExplanation = Integer.parseInt(num);
			
			logger.info("Index path: " + this.indexPath);
			logger.info("Dataset path: " + this.datasetPath);
			logger.info("Dictionary path: " + this.dictionaryPath);
			logger.info("Index open mode: " + this.indexOpenMode);
			logger.info("RAM buffer size: " + this.RAMBufferSize);
			logger.info("Query explanation enabled: " + this.debugMode);
			logger.info("Number of score explanation: " + this.numTopScoreExplanation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.severe("Property file could not be found: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Properties file could not be loaded: " + e.getMessage());
		}
	}
	

	public void setDictionaryPath(String dictionaryPath) {
		this.dictionaryPath = dictionaryPath;
	}

	public String getPlainPath() {
		return plainPath;
	}

	public void setPlainPath(String plainPath) {
		this.plainPath = plainPath;
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
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	public int getNumTopScoreExplantion() {
		return numTopScoreExplanation;
	}
	
	public void setNumTopScoreExplantion(int numTopScoreExplantion) {
		this.numTopScoreExplanation = numTopScoreExplantion;
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

	public String getDictionaryPath() {
		return this.dictionaryPath;
	}
	
}
