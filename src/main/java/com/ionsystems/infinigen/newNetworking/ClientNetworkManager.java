package main.java.com.ionsystems.infinigen.newNetworking;

public class ClientNetworkManager {
	/*
	 * The new networking system is responsible for a few things.
	 * 
	 * The main job is the stream visible chunk data to the client. This is done
	 * by only sending the visible chunk data to reduce bandwidth. This is then
	 * compressed and sent to the client. The server will only return to the
	 * client visible chunk data that is actually visible to the client.
	 * 
	 * Additionally the networking system on the client side needs to constantly
	 * tell the server where the camera is so the server can send the right
	 * data.
	 * 
	 * Since this is primarily an RTS engine the client is mostly dumb and
	 * doesn't get a say on what is happening, to make cheating and hacking as
	 * hard as possible the server is authoritative on all actions but receives
	 * input from the clients of what they want to move and where. If the server
	 * deems a clients actions to be possible then it will carry out the action
	 * from the client.
	 * 
	 * Finally the networking system has to update the position of all entities
	 * within a certain range. This is done by sending an individual id for each
	 * new entity and the loading name of the entity to a client when it is
	 * created. Then on each network update the server sends the positions of
	 * all the entities around the client and the client updates their
	 * positions.
	 * 
	 * 
	 * 
	 * The networking subsystem has 4 threads associated with it. 2 for High
	 * Bandwidth and 2 for Low Latency connections. By only allowing
	 * communication in 1 direction on each of the four threads it reduces the
	 * affect of latency on the available bandwidth of the socket. The high
	 * bandwidth sockets are used for chunk updates and unit positions and the
	 * low latency connections are used for client input and current position as
	 * well as other information eg gui data for a team, etc
	 */

	/*
	 * This particular class has to setup the connection to the server on all
	 * four networking threads and attempt to reconnect if they drop connection.
	 */
}
