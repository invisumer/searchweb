package it.uniroma3.searchweb.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.util.Version;

public class EngineConfig {

	private static final Logger logger = Logger.getLogger(EngineConfig.class.getName());
	private static final Version version = Version.LUCENE_46;
	private static EngineConfig instance;
	private static String propertyPath = "META-INF/config.properties";
	private String indexPath = "index";
	private String datasetPath = "dataset";
	private String dictionaryPath = "dictionary";
	private String plainDictionaryPath = "plain";
	private OpenMode indexOpenMode = OpenMode.APPEND;
	private double RAMBufferSize = 16;
	private boolean debugMode = false;
	private int numTopScoreExplanation = 0;
	private int maxHits = 50;
	private double scoreThreshold = maxHits*0.314;
	private int correctionPerWord = 5;
	private float similarityThreshold = 0.75f;
	private String logPath = "logger";
	private float titleBoost = 2f;
	private float bodyBoost = 40f;
	private boolean cleanHtml = false;

	private EngineConfig() {
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyPath));
			
			// mandatory properties
			this.indexPath = prop.getProperty("index.path");
			this.datasetPath = prop.getProperty("dataset.path");
			this.dictionaryPath = prop.getProperty("dictionary.path");
			this.plainDictionaryPath = prop.getProperty("dictionary.plain.path");
			
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
			
			// Max number of results
			String max = prop.getProperty("searcher.maxhits");
			if (max != null)
				this.maxHits = Integer.parseInt(max);
			
			// TODO get property configuration for spell corrector
			
			// logger file
			String log = prop.getProperty("log.path");
			if (log != null)
				this.logPath = log;
			
			// Clean html
			String clean = prop.getProperty("html.clean");
			if (clean != null)
				this.cleanHtml = Boolean.parseBoolean(clean);
			
			//score threshold
			String scorethreshold = prop.getProperty("scorethreshold");
			if (scorethreshold!=null)
				this.scoreThreshold = maxHits*Double.parseDouble(scorethreshold);
			
			//correction per word
			String correctionperword = prop.getProperty("correctionperword");
			if (correctionperword!=null)
				this.correctionPerWord = Integer.parseInt(correctionperword);
			
			//similarity threshold
			String similaritythreshold = prop.getProperty("similaritythreshold");
			if (similaritythreshold!=null)
				this.similarityThreshold = Float.parseFloat(similaritythreshold);
			
			logger.info("Index path: " + this.indexPath);
			logger.info("Dataset path: " + this.datasetPath);
			logger.info("Dictionary path: " + this.dictionaryPath);
			logger.info("Index open mode: " + this.indexOpenMode);
			logger.info("RAM buffer size: " + this.RAMBufferSize);
			logger.info("Query explanation enabled: " + this.debugMode);
			logger.info("Number of score explanation: " + this.numTopScoreExplanation);
			logger.info("Max number of hits: " + this.maxHits);
			logger.info("Min number of hits for query suggestions: " + this.scoreThreshold);
			logger.info("Max number of query suggestions: " + this.correctionPerWord);
			logger.info("Similarity threshold for query suggestion: " + this.similarityThreshold);
			logger.info("Log path: " + this.logPath);
			logger.info("Clean html pages: " + this.cleanHtml);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.severe("Property file could not be found: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Properties file could not be loaded: " + e.getMessage());
		}
	}
	
	public int getMaxCorrection() {
		return correctionPerWord;
	}

	public void setMaxCorrection(int maxCorrection) {
		this.correctionPerWord = maxCorrection;
	}
	
	public String getDictionaryPath() {
		return this.dictionaryPath;
	}

	public void setDictionaryPath(String dictionaryPath) {
		this.dictionaryPath = dictionaryPath;
	}

	public String getPlainPath() {
		return plainDictionaryPath;
	}

	public void setPlainPath(String plainPath) {
		this.plainDictionaryPath = plainPath;
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
	
	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}
	
	public double getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}
	
	public float getSimilarity() {
		return similarityThreshold;
	}

	public void setSimilarity(float similarity) {
		this.similarityThreshold = similarity;
	}
	
	public String getLogPath() {
		return logPath;
	}
	
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
	public boolean isCleanHtml() {
		return cleanHtml;
	}
	
	public void setCleanHtml(boolean cleanHtml) {
		this.cleanHtml = cleanHtml;
	}

	public String[] getWarcFiles() {
		File datasetFolder = new File(this.getDatasetPath());
		final Pattern p = Pattern.compile(".*\\.(warc\\.gz)$");
		File[] warcFiles = datasetFolder.listFiles(new FileFilter() {
	        @Override
	        public boolean accept(File file) {
	            return p.matcher(file.getName()).matches();
	        }
	    });
		String[] filenames = new String[warcFiles.length];

		for (int i = 0; i < warcFiles.length; i++) {
			filenames[i] = warcFiles[i].getName();
		}

		Arrays.sort(filenames);

		return filenames;
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

	public float getTitleBoost() {
		return titleBoost;
	}

	public void setTitleBoost(float titleBoost) {
		this.titleBoost = titleBoost;
	}

	public float getBodyBoost() {
		return bodyBoost;
	}

	public void setBodyBoost(float bodyBoost) {
		this.bodyBoost = bodyBoost;
	}
	
	public static Version getVersion() {
		return version;
	}
	
}
