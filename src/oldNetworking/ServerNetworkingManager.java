package oldNetworking;

import java.io.IOException;
import java.net.ServerSocket;

//handles the communication with multiple clients
public class ServerNetworkingManager implements Runnable {

	private ServerSocket serverSocket;
	private boolean listening;

	// The current version of the world being sent to clients.

	public ServerNetworkingManager() {

		listening = true;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(19987);
			serverSocket.setPerformancePreferences(2, 2, 1);

			System.out.println("Successfully bound server to port 19987");
		} catch (IOException e) {
			System.err.println("Could not listen on port: 19987.");
			System.exit(-1);
		}

		while (listening)
			try {
				System.out.println("Waiting on client connection");
				(new Thread(new ClientConnection(serverSocket.accept()))).start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
