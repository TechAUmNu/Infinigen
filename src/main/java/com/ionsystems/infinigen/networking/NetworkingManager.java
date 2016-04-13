package main.java.com.ionsystems.infinigen.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.newNetworking.Client;

//The basic idea for networking is that the game will be launched with a
// command line argument by the lobby which will tell it what server to
// connect to. The lobby system will have already sent the information for
// the match to the server so it knows which team each player is in.

//So in this class we need to determine if we are the client or the server in order to know what to do.
//We shall use the globals class to find out.

public class NetworkingManager implements IModule {

	private ServerSocket serverSocket; // The socket to listen for clients on.
	private int noPlayers = 1;
	private int connectedClients = 0;
	private Client client;

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {

		// In the setup we will need to connect to the server if we are a client
		// and start the connection listener if we are the server.
		if (Globals.isServer()) {
			startListenServer();
			waitForClients();
		} else {
			client = new Client();
			client.username = "Euan";
			connectToServer();
		}

	}

	private void connectToServer() {
		(new Thread(new ConnectionToServer(client))).start();

	}

	private void waitForClients() {
		while (connectedClients < noPlayers)
			try {
				System.out.println("Waiting on client connection");
				System.out.println("Server listening on: " + serverSocket.getInetAddress());
				(new Thread(new ConnectionFromClient(serverSocket.accept()))).start();
				connectedClients++;
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

	private void startListenServer() {
		try {
			serverSocket = new ServerSocket(Globals.getLatencyPort());
			serverSocket.setPerformancePreferences(0, 1, 2);
			System.out.println("Successfully bound server to port " + Globals.getLatencyPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + +Globals.getLatencyPort());
			System.exit(-1);
		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

}
