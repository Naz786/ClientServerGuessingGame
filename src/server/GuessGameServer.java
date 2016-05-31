package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuessGameServer {

	public static void main(String[] args) {

		// 3 command line arguments
		int port;
		int maxValue;
		long timeLimit;
		GuessGameServerHandler handler;

		if (args.length <= 0) {
			System.err.println("Usage: DataReaderDemo <file>");
			System.exit(1);
		}

		port = Integer.parseInt(args[0]);
		maxValue = Integer.parseInt(args[1]);
		timeLimit = Long.parseLong(args[2]);

		/*
		 * Server makes ServerSocket on specific port which starts the server
		 * application listening for client requests coming in for port 8080.
		 */

		try (ServerSocket server = new ServerSocket(port)) {
			System.out.println("Server waiting for client...");
			while (true) {
				/*
				 * Server creates a new Socket to communicate with the client.
				 * Accept() method blocks the while waiting for the client
				 * Socket connection.
				 */
				Socket client = server.accept();
				ExecutorService executor = Executors.newCachedThreadPool();
				handler = new GuessGameServerHandler(maxValue, timeLimit,
						client);
				executor.execute(new Thread(handler));

			}
		} catch (IOException e) {
			System.out.println("Error occurred. ");
		}

	}
}
//
