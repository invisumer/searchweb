package it.uniroma3.searchweb.engine.searcher;

public class SpeedTest extends Thread {
	private static final int attempt = 57;
	
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
		int numCpu = Runtime.getRuntime().availableProcessors();
		SpeedTest[] tests = new SpeedTest[numCpu];
		for (int i=0; i<tests.length;i++) {
			tests[i] = new SpeedTest();
		}
		double start = System.currentTimeMillis();
		for (int i=0; i<tests.length;i++) {
			tests[i].start();
		}
		for (int i=0; i<tests.length;i++) {
			tests[i].join();
		}
		
		double stop = System.currentTimeMillis();
		double time = stop-start;
		System.out.println(attempt*numCpu+" query executed in : "+time);
	}
}
