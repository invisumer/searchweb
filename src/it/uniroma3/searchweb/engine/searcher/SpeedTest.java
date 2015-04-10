package it.uniroma3.searchweb.engine.searcher;

public class SpeedTest extends Thread {
	private static final int attempt = 65;
	
	public void run() {
		makeTest();
	}
	
	public static void makeTest() {
		String query = "test";
		String[] fields = new String[3];
		fields[0] = "url";
		fields[1] = "title";
		fields[2] = "body";
		DebuggerSearchEngine dse = new StupidSearchEngine();
		String contentType = "html";
		for (int i=0; i<attempt;i++)
			dse.getResults(query, fields, contentType, false, "en");
	}
	
	public static void main(String[] args) throws InterruptedException {
		String query = "test";
		String[] fields = new String[3];
		fields[0] = "url";
		fields[1] = "title";
		fields[2] = "body";
		DebuggerSearchEngine dse = new StupidSearchEngine();
		String contentType = "html";
		dse.getResults(query, fields, contentType, false, "en");
		
		SpeedTest[] tests = new SpeedTest[Runtime.getRuntime().availableProcessors()];
		for (int i=0; i<tests.length;i++) {
			tests[i] = new SpeedTest();
		}
		double start = System.currentTimeMillis();
		for (int i=0; i<tests.length;i++) {
			tests[i].start();
		}
		for (int i=0; i<tests.length;i++) {
			tests[i].join();;
		}
		
		double stop = System.currentTimeMillis();
		double time = stop-start;
		System.out.println(attempt*4+" query executed in : "+time);
	}
}
