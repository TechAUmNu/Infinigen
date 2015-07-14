package newNetworking;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkMessage implements Serializable{
	
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
}
