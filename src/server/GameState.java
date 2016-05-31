package server;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import TimerTesting.Timing;

public class GameState implements Runnable {

	Writer out;
	static Random RANDGEN = new Random();
	int targetNum;
	int playerGuess;
	int maxVal;
	int numOfGuessesSoFar;
	long timeLeft;
	long timeLimit;
	public static final int MINVAL = 1;
	public GuessGameServerHandler handler;
	Timing timing;

	long startTime;
	long currentTime;

	long timeRunning;

	long time;

	public GameState(int maxValue, long timeLimit, Writer o,
			GuessGameServerHandler ggsh) {
		maxVal = maxValue;
		this.timeLimit = timeLimit;
		out = o;
		timeLeft = timeLimit;
		targetNum = RANDGEN.nextInt(maxVal) + 1;
		handler = ggsh;
	}

	// this is true if guess is correct - false otherwise
	public boolean isFinished() {
		if (playerGuess == targetNum || getTimeRemaining() <= 0) {
			return true;

		} else {
			return false;
		}
	}

	public int getGuesses() {
		return numOfGuessesSoFar;
	}

	public int getTarget() {
		return targetNum;
	}

	public long getTimeRemaining() {
		timeLeft = timeLimit - timeRunning;

		return timeLeft;
	}

	public void guess(int g) {
		numOfGuessesSoFar++;
		playerGuess = g;
		isFinished();
	}

	public void run() {

		startTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(),
				TimeUnit.NANOSECONDS);

		do {

			currentTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(),
					TimeUnit.NANOSECONDS);

			timeRunning = TimeUnit.MILLISECONDS
					.toSeconds((currentTime - startTime));

		} while (getTimeRemaining() > 0);

		try {
			out.write(String.format("Turn %d: LOSE %n", getGuesses()));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String toString() {

		String gameState = "Game Not Started.\n";

		if (getTimeRemaining() > 0) {

			if (playerGuess > targetNum) {
				gameState = String.format("%d (HIGH) - %d/%d%n", playerGuess,
						getTimeRemaining(), getGuesses());
			}

			if (playerGuess < targetNum) {
				gameState = String.format("%d (LOW) - %d/%d%n", playerGuess,
						getTimeRemaining(), getGuesses());
			}

			if (playerGuess == targetNum) {
				gameState = String.format("%d (WIN) - %d/%d%nGame Over%n",
						playerGuess, getTimeRemaining(), getGuesses());
			}

		} else {
			gameState = String.format(" - (LOSE) -- %d/%d%nGame Over%n",
					getTimeRemaining(), getGuesses());
		}
		return gameState;
	}

}
//