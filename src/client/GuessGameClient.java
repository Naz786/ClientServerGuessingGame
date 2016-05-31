package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

public class GuessGameClient {

	public static void main(String[] args) {
		// 2 command line arguments
		String servername;
		int port;

		if (args.length <= 0) {
			System.err.println("Usage: DataReaderDemo <file>");
			System.exit(1);
		}

		servername = args[0];
		port = Integer.parseInt(args[1]);

		// Client makes a Socket connection to the server application.
		// Client knows the IP address and port number.

		try (Socket server = new Socket(servername, port)) {
			System.out.println("Connected to " + server.getInetAddress());

			// From user/client
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					System.in));
			//From Server
			BufferedReader in = new BufferedReader(new InputStreamReader(
					server.getInputStream(), "UTF-8"));
			Writer out = new OutputStreamWriter(server.getOutputStream());

			String result;
			Boolean notFinished = true;
			String number;

			String startMessage = in.readLine();
			System.out.println(startMessage);

			do {
				System.out.print("Enter your guess: ");
				number = stdin.readLine();

				out.write(String.format("%s%n", number));
				out.flush();

				result = in.readLine();
				System.out.println(result);

//				if (result.contains("WIN") || result.contains("LOSE")
//						|| result.contains("Over")) {
//					notFinished = false;
//				}

			} while (notFinished);

//			 System.out.println("Client shutdown");
			server.close();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + servername);
			System.err.println(e);
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
}
//