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

public class ServerNetworkManager implements IModule{
	private ServerSocket bandwidthSocket, latencySocket;
	private LinkedTransferQueue<Object> bandwidthSendQueue, bandwidthRecieveQueue, latencySendQueue, latencyRecieveQueue;
	
	
	
	public void acceptClient() {
		try {
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), 0, bandwidthRecieveQueue))).start();
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), 1, bandwidthSendQueue))).start();

			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), 0, latencyRecieveQueue))).start();
			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), 1, latencySendQueue))).start();
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
			
			System.out.println("Creating queues");
			bandwidthSendQueue = new LinkedTransferQueue<Object>();
			bandwidthRecieveQueue = new LinkedTransferQueue<Object>();
			latencySendQueue = new LinkedTransferQueue<Object>();
			latencyRecieveQueue = new LinkedTransferQueue<Object>();
			
			System.out.println("Linking queues to messaging system");
			Messaging.addMessageQueue(Tag.NetworkBandwidthSend, bandwidthSendQueue);
			Messaging.addMessageQueue(Tag.NetworkBandwidthRecieve, bandwidthRecieveQueue);
			Messaging.addMessageQueue(Tag.NetworkLatencySend, latencySendQueue);
			Messaging.addMessageQueue(Tag.NetworkLatencyRecieve, latencyRecieveQueue);
		} catch (IOException e) {
			System.err.println("Could not listen on ports: " + Globals.getBandwidthPort() + ", " + Globals.getLatencyPort());
			System.exit(-1);
		}
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUp() {
		startServer();
		while(true){
			acceptClient();
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
		
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
