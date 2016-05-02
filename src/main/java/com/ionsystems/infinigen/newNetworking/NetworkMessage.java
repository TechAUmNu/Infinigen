package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.Serializable;
import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.messages.Tag;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.networking.PhysicsNetworkBody;
import main.java.com.ionsystems.infinigen.world.ChunkID;
import main.java.com.ionsystems.infinigen.world.NetworkChunkData;

public class NetworkMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5409394775872811653L;
	public boolean disconnect = false;
	public Client client;
	public ArrayList<NetworkChunkData> ncd;
	public Tag tag;
	public ArrayList<ChunkID> chunkRequest;
}
