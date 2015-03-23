package it.uniroma3.searchweb.engine.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import it.uniroma3.searchweb.config.EngineConfig;

public class WarcParser {
	
	public WarcParser(){}
	
	static EngineConfig engineConfig = EngineConfig.getInstance();
	static String warcFile = engineConfig.getDatasetPath()+"/example.warc";
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Document html = Jsoup.parse(new File(warcFile), "UTF-8");
		System.out.println(html.getElementsByTag("html"));
	}
}
