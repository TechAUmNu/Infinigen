package main.java.com.ionsystems.infinigen.newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.Timer;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.xxhash.XXHashFactory;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.messages.Messaging;
import main.java.com.ionsystems.infinigen.messages.Tag;

public class ServerNetworkAdapter implements Runnable {

	private Socket socket;

	private DataOutputStream out;
	private DataInputStream in;
	final LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
	private NetworkMessage inMessage;
	Ack ack;
	private Client client;
	private int clientID;
	private int direction;
	String tag;
	String networkType;
	private Checksum checksum = new CRC32();
	final LZ4FastDecompressor decompressor = LZ4Factory.nativeInstance().fastDecompressor();
	
	// Send
	public ServerNetworkAdapter(Socket socket, int clientID, String networkType) {
		this.socket = socket;
		this.direction = 1;		
		this.clientID = clientID;
		this.networkType = networkType;
	}

	
	// Receive
	public ServerNetworkAdapter(Socket socket, int clientID) {
		this.socket = socket;
		try {
			this.socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.direction = 0;		
		this.clientID = clientID;		
	}

	@Override
	public void run() {
		if (direction == 0) { // Receiving
			recieve();
		} else {
			send(); // Sending
		}

	}

	/**
	 * This will only deal with the actual transfer of data between the client and server. No processing should occur in this thread. Receive is only receiving
	 * data from a client and sending an acknowledge back.
	 */
	public void recieve() {
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());

		try {
			System.out.println("Creating output stream");

			
			//out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out = new DataOutputStream(new BufferedOutputStream(new LZ4BlockOutputStream( socket.getOutputStream(), 64 * 1024, compressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum(), true )));
			out.flush();

			//in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			in = new DataInputStream(new BufferedInputStream(new LZ4BlockInputStream(socket.getInputStream(), decompressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum())));


			/**
			 * This do loop runs until a client disconnects
			 */
			do {
				try {
					
					inMessage = (NetworkMessage) readObjectFromStream(in);
					
					if(inMessage != null){
						if (inMessage.disconnect) {
							System.out.println(client.username + " diconnected");
						} else {
							processMessage(inMessage); // add the message from the
														// client to the incoming
														// queue
							sendAck(false); // send an acknowledgement to the client
											// saying there was no error
						}
					}else{
						inMessage = new NetworkMessage();
					}
					
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
					sendAck(true); // send an acknowledgement to the client
									// saying there was an error
				} catch (SocketException connectionReset) {
					System.err.println("Client closed the connection");
					break;
				}
			} while (!inMessage.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println(client.username + " disconnected");

			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}

	public void send() {
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());

		try {
			System.out.println("Creating output stream");

			//out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out = new DataOutputStream(new BufferedOutputStream(new LZ4BlockOutputStream( socket.getOutputStream(), 64 * 1024, compressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum(), true )));
			out.flush();

			//in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			in = new DataInputStream(new BufferedInputStream(new LZ4BlockInputStream(socket.getInputStream(), decompressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum())));


			
			// wait for client to send username
			while(Globals.getClientMapping(clientID) == null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			/**
			 * This do loop runs until a client disconnects
			 */
			do {
				try {
					
					sendMessage(); // Send a message to the client 
					// blocks until a message is sent

					// Wait for an acknowledgement
					ack = (Ack) readObjectFromStream(in);
					System.out.println("ack recieved from client");
					if (!ack.ack) {
						System.out.println("Problem with previously sent message");
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");					
				} catch (SocketException connectionReset) {
					System.err.println("Client closed the connection");
					break;
				}
			} while (!ack.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println(client.username + " disconnected");

			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}

	private void sendAck(boolean error) {
		Ack ack = new Ack();
		ack.ack = !error;
		sendNetworkMessage(ack);
	}

	private void processMessage(NetworkMessage msg) {
		if (msg.client != null){
			client = msg.client;
			Globals.mapClient(clientID, client);
		}
		System.out.println(networkType != null ? networkType : "Recieve" + ": Message recieved from client: " + msg.tag.toString());
		
		Messaging.addMessage(msg.tag, msg);
	}
	
	private void sendMessage(){
		
		if(tag == null && Globals.getClientMapping(clientID) != null){
			tag = Globals.getClientMapping(clientID).username + networkType;
		}
		NetworkMessage msg = null;
		if(tag != null){
			msg = (NetworkMessage) Messaging.takeLatestMessage(tag);
		}
		if(msg != null)
			System.out.println(networkType + ": Sending message to client: " + msg.tag.toString());
			sendNetworkMessage(msg);
	}
	
	private void sendNetworkMessage(Object o){
		try {			
			writeObjectToStream(out,o);
			out.flush();			
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.exit(0);
		}
	}
	
static boolean UseStdStreams = false;
    
    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    protected static Object readObjectFromStream(DataInputStream inputStream) throws IOException, ClassNotFoundException {
        if ( UseStdStreams ) {
            ObjectInputStream in = new ObjectInputStream(inputStream);
            return in.readObject();
        } else {
            int len = inputStream.readInt();
            byte buffer[] = new byte[len]; // this could be reused !
            while (len > 0)
                len -= inputStream.read(buffer, buffer.length - len, len);
            return ServerNetworkAdapter.conf.getObjectInput(buffer).readObject();
        }
    }

    protected static void writeObjectToStream(DataOutputStream outputStream, Object toWrite) throws IOException {
        if ( UseStdStreams ) {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(toWrite);
            out.flush();
        } else {
            // write object 
            FSTObjectOutput objectOutput = conf.getObjectOutput(); // could also do new with minor perf impact
            // write object to internal buffer
            objectOutput.writeObject(toWrite);
            // write length
            outputStream.writeInt(objectOutput.getWritten());
            // write bytes
            outputStream.write(objectOutput.getBuffer(), 0, objectOutput.getWritten());

            objectOutput.flush(); // return for reuse to conf
        }
    }

}
