package it.uniroma3.searchweb.engine.analyzer;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StripperHTMLAnalyzer extends Analyzer {
	public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET; 
	private Version version;
	
	 public StripperHTMLAnalyzer(Version version) {
		this.version = version;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldname, Reader reader) {
		Tokenizer src = new StandardTokenizer(this.version, reader);
	    TokenFilter tok = new StandardFilter(this.version, src);
	    tok = new LowerCaseFilter(this.version, tok);
	    tok = new StopFilter(this.version, tok, STOP_WORDS_SET);
	    TokenStreamComponents tsc = new TokenStreamComponents(src, tok);
	    return tsc;
	}
	
	@Override
	protected Reader initReader(String fieldName, Reader reader) {
		System.out.println("ciao");
		Set<String> tags = new HashSet<String>();
		tags.add("<h1>");
		tags.add("</h1>");
		return new HTMLStripCharFilter(reader);
	}

}
