package newNetworking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;

//The basic idea for networking is that the game will be launched with a
// command line argument by the lobby which will tell it what server to
// connect to. The lobby system will have already sent the information for
// the match to the server so it knows which team each player is in.

//So in this class we need to determine if we are the client or the server in order to know what to do.
//We shall use the globals class to find out.

public class NetworkingManager implements IModule {

	private ServerSocket serverSocket; //The socket to listen for clients on.
	private int noPlayers = 2;
	private int connectedClients = 0;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		
		

	}

	@Override
	public void setUp() {
		//In the setup we will need to connect to the server if we are a client and start the connection listener if we are the server.
		if(Globals.isServer()){			
			startListenServer();
			waitForClients();		
		}
	
	}

	private void waitForClients() {
		while (connectedClients < noPlayers )
			try {
				System.out.println("Waiting on client connection");
				(new Thread(new ConnectionFromClient(serverSocket.accept()))).start();
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
			serverSocket = new ServerSocket(19987);
			serverSocket.setPerformancePreferences(0, 1, 2);			
			System.out.println("Successfully bound server to port 19987");
		} catch (IOException e) {
			System.err.println("Could not listen on port: 19987.");
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
