package it.uniroma3.searchweb.engine.searcher;

public class SpeedTest {
	public static void main(String[] args) {
		String query = "test";
		String[] fields = new String[3];
		fields[0] = "url";
		fields[1] = "title";
		fields[2] = "body";
		DebuggerSearchEngine dse = new StupidSearchEngine();
		String contentType = "html";
		dse.getResults(query, fields, contentType, false, "en");
		double start = System.currentTimeMillis();
		for (int i=0; i<77;i++)
			dse.getResults(query, fields, contentType, false, "en");
		double stop = System.currentTimeMillis();
		double time = stop-start;
		System.out.println(time);
	}
}
