package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

class GuessGameServerHandler implements Runnable {

	Socket client;
	Writer out;
	BufferedReader in;
	static GameState game;
	String guessTemp;
	int guess;
	int maxValue;
	long timeLimit;

	boolean notInRange = false;
	boolean inputError = false;
	boolean notInRangeFlag = false;
	boolean inputErrorFlag = false;

	char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z' };

	char charID;
	static int count = 0;

	public GuessGameServerHandler(int maximumValue, long timeLimit, Socket cl) {
		client = cl;
		this.maxValue = maximumValue;
		this.timeLimit = timeLimit;
		charID = letters[count];
	}

	public void run() {
		try {
			while (true) {
				count++;
				// get and display client's IP address
				InetAddress clientAddress = client.getInetAddress();

				System.out.printf("%s Client from %s connected%n", charID,
						clientAddress);
				System.out.printf("%s start watching%n", charID);

				out = new OutputStreamWriter(client.getOutputStream());
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream(), "UTF-8"));

				// new instance of the game class created by server
				game = new GameState(maxValue, timeLimit, out,
						new GuessGameServerHandler(maxValue, timeLimit, client));

				// this will start a new thread for each game to monitor the
				// time taken so far, and to finish the game when it has gone on
				// too long
				new Thread(game).start();

				System.out.printf("%s target is %d%n", charID, game.targetNum);

				out.write(String.format(
						"START: range is 1..%d, time allowed is %d%n",
						maxValue, timeLimit));
				out.flush();

				do {
					guessTemp = in.readLine();
					/*
					 * This try-catch block will find out if the guess is a
					 * number or not. If it is not a number, we will not be able
					 * to parse it and we will land in the catch block where we
					 * set the inputError flag to true depending on being within
					 * the time remaining.
					 */
					try {
						guess = Integer.parseInt(guessTemp);

						// This will check if the integer input is within range.
						// If it is, it will execute the else block and make the
						// guess. Otherwise, the notinrange boolean will set
						// true.
						if (guess < game.MINVAL || guess > game.maxVal) {
							if (!game.isFinished()) {
								notInRange = true;
							}
						} else {
							game.guess(guess);
						}

					} catch (NumberFormatException e) {
						// is a letter
						if (!game.isFinished()) {
							inputError = true;
						}
					}

					if (notInRange) {
						/*
						 * If the guess is not in the range, the System.out will
						 * print the former to the server.
						 */
						System.out.print(String.format(
								"%s %d (ERR out of range)-%d/%d%n", charID,
								guess, game.getTimeRemaining(),
								game.getGuesses()));

						/*
						 * Similarly, this message which uses the out.write will
						 * print the message to the client if the guess is not
						 * in range.
						 */
						out.write(String.format("Turn %d: ERR%n",
								game.getGuesses()));
						out.flush();

						notInRange = false;
						notInRangeFlag = true;

					} else if (inputError) {
						/*
						 * If the input from the guess cannot be parsed into an
						 * integer, then it is not a valid input and this will
						 * be printed using the system.out to the server
						 */
						System.out.print(String.format(
								"%s ** (ERR non-integer)-%d/%d%n", charID,
								game.getTimeRemaining(), game.getGuesses()));

						/*
						 * Similarly, if the input is of any other type than an
						 * integer, the following message will be output to the
						 * client, using the out.write
						 */

						out.write(String.format("Turn %d: ERR%n",
								game.getGuesses()));
						out.flush();

						inputError = false;
						inputErrorFlag = true;

					} else {
						System.out.print(String.format("%s %s", charID,
								game.toString()));
					}

					// Beginning of the following if statement includes the
					// protocol for handling flags that handle incorrect input.
					// The rest will sent to the client using the out.write()
					// method if the input is high, low or a winning guess.
					if (notInRangeFlag) {
						notInRangeFlag = false;

					} else if (inputErrorFlag) {
						inputErrorFlag = false;

					} else if (game.getTimeRemaining() > 0) {

						if (guess > game.targetNum) {
							out.write(String.format(
									"Turn %d: %d was HIGH, %ds remaining%n",
									game.getGuesses(), guess,
									game.getTimeRemaining()));
							out.flush();
						}

						else if (guess < game.targetNum) {
							out.write(String.format(
									"Turn %d: %d was LOW, %ds remaining%n",
									game.getGuesses(), guess,
									game.getTimeRemaining()));
							out.flush();
						}

						else if (guess == game.targetNum) {
							out.write(String.format("Turn %d: WIN%n ",
									game.getGuesses()));
							out.flush();
						}

					} else {
						out.write(String.format("Turn %d: LOSE%n",
								game.getGuesses()));
						out.flush();
					}

				} while (!game.isFinished());

				client.close();
				break;
			} // end of while

		} catch (IOException e) {
			System.out.println("Client game terminated.");
		} // end of try catch
	} // end of run
}
