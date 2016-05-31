package TimerTesting;

public class TestTime {

	public static void main(String[] args) {
		
		Timing timing = new Timing(10);
		
		new Thread(timing).start();
	}
}
