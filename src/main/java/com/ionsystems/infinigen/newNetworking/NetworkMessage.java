package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.Serializable;
import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.messages.Tag;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.networking.PhysicsNetworkBody;

public class NetworkMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5409394775872811653L;
	public boolean disconnect;
	public Client client;
	public boolean chunkUpdate;
	public int chunkCount;
	public ChunkData chunkData;
	public boolean chunkUpdateComplete;
	public boolean physicsUpdate;
	public ArrayList<PhysicsNetworkBody> physicsData;
	public boolean newEntity;
	public ArrayList<PhysicsEntity> entityData;
	public int clientID;
	public boolean clientIDChange;
	public Tag tag;
}
