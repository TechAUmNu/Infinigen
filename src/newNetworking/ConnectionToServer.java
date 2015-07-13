package newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import newMain.Globals;

public class ConnectionToServer implements Runnable, ActionListener {

	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	GZIPOutputStream gzipOut;
	Client client;
	NetworkMessage inMessage, outMessage;
	int chunkCount = 0, currentChunk = 0;;
	ArrayList<ChunkData> chunkUpdate;

	ConnectionToServer(Client c) {
		client = c;

	}

	public void run() {
		try {
			// 1. creating a socket to connect to the server

			System.out.println("about to connect to server at: " + Globals.getIp() + ":" + Globals.getPort());
			socket = new Socket(Globals.getIp(), Globals.getPort());
			socket.setPerformancePreferences(0, 1, 2);
			socket.setTcpNoDelay(true);
			System.out.println("Connected to localhost in port " + +Globals.getPort());
			// 2. get Input and Output streams
			System.out.println("Creating output stream");
			gzipOut = new GZIPOutputStream(socket.getOutputStream(), 4096, true);
			out = new ObjectOutputStream(new BufferedOutputStream(gzipOut));
			System.out.println("Flushing output");
			out.flush();

			System.out.println("Creating input stream");
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			// 3: Communicating with the server
			System.out.println("Connected to server, sending client info");
			// Send our client info to the server.
			outMessage = new NetworkMessage();
			outMessage.client = client;
			sendMessage();

			// Now we want to know what the current terrain is, so we get a
			// chunk update which will get all currently loaded chunks on the
			// server (Might be an idea in future to make it only around a
			// specific area?)

			outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true;
			sendMessage();

			do {
				try {
					// System.out.println("Waiting on message from server");
					inMessage = (NetworkMessage) in.readObject();
					// System.out.println("Message received from server");
					if (!inMessage.disconnect) {
						processMessage();
					}

				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				} catch (EOFException e) {
					// Ignore this error since it will always occur when you
					// quit.
				}
			} while (!inMessage.disconnect);
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();

		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void processMessage() {
		if (inMessage.chunkUpdate) {
			chunkUpdate();
		}
	}

	private void chunkUpdate() {

		// We are now updating our chunks

		// First we will receive the number of chunks that are being loaded
		if (inMessage.chunkCount > 0) {
			chunkCount = inMessage.chunkCount;
			chunkUpdate = new ArrayList<ChunkData>();
			currentChunk = 0;
		}

		// Then we will receive the chunks

		if (inMessage.chunkData != null) {
			chunkUpdate.add(inMessage.chunkData);
			System.out.println("Chunk: " + currentChunk + "/" + chunkCount);
			currentChunk++;

		}

		if (inMessage.chunkUpdateComplete) {
			System.out.println("Recieved: " + currentChunk + "/" + chunkCount + " Chunks in chunk update");
			Globals.setChunkUpdate(chunkUpdate);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	void sendMessage() {
		try {
			out.writeObject(outMessage);
			out.flush();
			out.reset();
			outMessage = null; // This makes sure we don't ever send the same
								// message twice.
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

}
