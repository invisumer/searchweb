package it.uniroma3.searchweb.engine.searcher;

public class SpeedTest extends Thread {
	
	public void run() {
		makeTest();
	}
	
	public static void makeTest() {
		String query = "test";
		String[] fields = new String[3];
		fields[0] = "url";
		fields[1] = "title";
		fields[2] = "body";
		int attempt = 56;
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
		SpeedTest st0 = new SpeedTest();
		SpeedTest st1 = new SpeedTest();
		SpeedTest st2 = new SpeedTest();
		SpeedTest st3 = new SpeedTest();
		double start = System.currentTimeMillis();
		st0.start();
		st1.start();
		st2.start();
		st3.start();
		st0.join();
		st1.join();
		st2.join();
		st3.join();
		
		double stop = System.currentTimeMillis();
		double time = stop-start;
		System.out.println(time);
	}
}
