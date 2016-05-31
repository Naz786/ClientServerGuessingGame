package TimerTesting;
import java.util.concurrent.TimeUnit;

public class Timing implements Runnable {

	long startTime;
	long currentTime;

	long timeLimit;
	long timeLimitMilli;
	
	long timeRunning;

	boolean withinTimeLimit;
	
	long time;

	public Timing(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void run() {

		startTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(),
				TimeUnit.NANOSECONDS);

		timeLimitMilli = toMilliSeconds(timeLimit);

		while (true) {

			currentTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(),
					TimeUnit.NANOSECONDS);

			timeRunning = currentTime - startTime;
			
//			System.out.println(toSeconds(timeRunning));
			time = toSeconds(timeRunning);
			
			if (withinTime()) {
				break;
			}

		}
	}

	public boolean withinTime() {
		if (timeLimitMilli <= timeRunning) {
//			System.out.println(timeRunning);
			return true;
		} else {
			return false;
		}
	}
	
	

	public long toSeconds(long millisecond) {
		long timeRemaining = millisecond / 1000;
		return timeRemaining;
	}

	public long toMilliSeconds(long second) {
		long timeLimit = second * 1000;
		return timeLimit;
	}

}
