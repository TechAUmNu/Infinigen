package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;

public class ServerNetworkManager implements IModule{
	private ServerSocket bandwidthSocket;
	private ServerSocket latencySocket;

	
	
	public void acceptClient() {
		try {
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), 0))).start();
			(new Thread(new ServerNetworkAdapter(bandwidthSocket.accept(), 1))).start();

			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), 0))).start();
			(new Thread(new ServerNetworkAdapter(latencySocket.accept(), 1))).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startServer() {
		try {
			bandwidthSocket = new ServerSocket(Globals.getBandwidthPort());
			bandwidthSocket.setPerformancePreferences(0, 0, 1);
			System.out.println("Successfully bound bandwidth server to port " + Globals.getBandwidthPort());
			latencySocket = new ServerSocket(Globals.getLatencyPort());
			latencySocket.setPerformancePreferences(0, 1, 0);
			System.out.println("Successfully bound latency server to port " + Globals.getLatencyPort());
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
		//Here we will add the relivent data to each client adapter
		
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
