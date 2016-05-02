package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.LinkedTransferQueue;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.messages.Messaging;
import main.java.com.ionsystems.infinigen.messages.Tag;

public class ServerNetworkManager implements Runnable{
	private ServerSocket bandwidthSocket, latencySocket;

	private int clientID = 0;
	
	public void acceptClient() {
		try {
			clientID++;
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), clientID))).start();
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), clientID, Tag.NetworkBandwidthSend.toString()))).start();

			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), clientID))).start();
			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), clientID, Tag.NetworkLatencySend.toString()))).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void startServer() {
		try {
			bandwidthSocket = new ServerSocket();
			bandwidthSocket.setPerformancePreferences(0, 0, 1);			
			bandwidthSocket.bind(new InetSocketAddress(Globals.getBandwidthPort()));			
			System.out.println("Successfully bound bandwidth server to port " + Globals.getBandwidthPort());
			latencySocket = new ServerSocket();	
			latencySocket.setPerformancePreferences(0, 1, 0);					
			latencySocket.bind(new InetSocketAddress(Globals.getLatencyPort()));
			System.out.println("Successfully bound latency server to port " + Globals.getLatencyPort());
			
			

		} catch (IOException e) {
			System.err.println("Could not listen on ports: " + Globals.getBandwidthPort() + ", " + Globals.getLatencyPort());
			System.exit(-1);
		}
	}

	

	
	public void setUp() {
		startServer();
		while(true){
			acceptClient();
		}
	}

	


	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		setUp();		
	}
	
	
	

}
