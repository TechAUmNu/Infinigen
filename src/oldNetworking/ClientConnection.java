package oldNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Timer;

import sprites.UnitSprite;
import dataStuctures.Client;
import dataStuctures.ClientCommand;
import dataStuctures.InterthreadHolder;
import dataStuctures.Message;

//A connection from the server to a client
public class ClientConnection implements Runnable, ActionListener {

	Socket connection;
	ObjectOutputStream out;
	ObjectInputStream in;
	Message message;
	Client client;
	ArrayList<Message> sentMessages;

	ClientConnection(Socket socket) {
		connection = socket;
		sentMessages = new ArrayList<Message>();

	}

	public void run() {
		// try {

		System.out.println("Connection received from " + connection.getInetAddress().getHostName());
		// 3. get Input and Output streams
		try {
			connection.setPerformancePreferences(2, 2, 1);
			connection.setTcpNoDelay(true);
			out = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));
			out.flush();

			in = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));

			System.out.println("Sending First world instance and pathability map");
			sendMessage(new Message(null, false, null, false, null, false, "Downloading Map"));
			sendMessage(new Message(InterthreadHolder.getInstance().getNetworkWorld(), false, null, false, null, false, null));
			sendMessage(new Message(null, false, null, false, null, false, "Downloading Pathfinding Map"));
			sendMessage(new Message(null, false, null, false, InterthreadHolder.getInstance().getWalkability(), true, "done"));
			// Timer which fires the send message event, this should be on a
			// fast time since we want to send the message as soon as possible.
			Timer timer = new Timer(50, this); // With 50ms it will send the
												// message less than 50ms after
												// it was created. Checking for
												// messages 200 times a second.
			timer.start();

			// 4. The two parts communicate via the input and output streams
			do {

				try {

					message = (Message) in.readObject();
					if (message.disconnect) {
						System.out.println(client.username + " disconnected");
					} else {

						// Do something based on client input presumably a
						// selection or move.

						// start waiting for the input to be processed
						InterthreadHolder.getInstance().setWaiting(true);
						// if there is a command then wait for it to be
						// processed
						if (message.command != null) {
							processClientInput(message);
							while (InterthreadHolder.getInstance().getWaiting()) {
								try {
									Thread.sleep(10);
									// wait on the command to be processed
									System.out.println("Waiting");
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
							}
						}
						if (message.sendClient) {
							// The client replied to our client info request.
							// So we need to set the client here with the ip
							// address.
							client = message.client;
							client.ip = connection.getInetAddress();

						}
						// Send the current world to the client. This will only
						// occur if a client is
						// desynced and is to stop the game from just ending or
						// becoming unplayable.
						// TODO: use this... by adding desync checks
						if (message.requestUpdate) {
							System.out.println("Sending updated world to client: " + connection.getInetAddress().getHostName());
							sendMessage(new Message(InterthreadHolder.getInstance().getNetworkWorld(), false, null, false, null, false, null));
						}
					}

				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} while (!message.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				connection.close();

			} catch (Exception e) {
				// Don't really care the connection is already dead anyway
			}

		}
	}

	// Process the client command that was received
	private void processClientInput(Message message) {
		ClientCommand cmd = message.command;

		switch (cmd.commandType) {
		case "Select":
			System.out.println("Client sent Select order");
			for (UnitSprite s : InterthreadHolder.getInstance().getWorld().getSprites()) {
				for (UnitSprite sp : cmd.selectedUnits) {
					if (s.spriteID == sp.spriteID && s.getTeamID() == client.teamID) { // Check
																						// that
																						// the
																						// client
																						// can
																						// actually
																						// select
																						// the
																						// item
						s.setSelected(true, client.teamID);
						// System.out.println("Selecting unit:" + s.spriteID);
					}
				}

			}
			break;
		case "deSelectAll":
			System.out.println("Client sent deSelectAll order");
			for (UnitSprite s : InterthreadHolder.getInstance().getWorld().getSprites()) {
				if (s.getTeamID() == client.teamID) {
					s.setSelected(false, client.teamID);
				}

				// System.out.println("Deselecting unit:" + s.spriteID);
			}
			break;
		case "Move":
			System.out.println("Client send move order");
			for (UnitSprite s : InterthreadHolder.getInstance().getWorld().getSprites()) {
				if (s.isSelected()) {
					s.moveTo(cmd.x + cmd.camX, cmd.y + cmd.camY);
					// System.out.println("Moving unit:" + s.spriteID);
				}
			}
			break;
		}
		// Add the message to the send list so that it is sent to all clients.
		InterthreadHolder.getInstance().addMessage(message);
		InterthreadHolder.getInstance().setWaiting(false);

	}

	// Send a message to the client probably an update with the new world
	// or a message saying someone has left the game
	void sendMessage(Message msg) {
		try {
			out.writeObject(msg);
			out.flush();
			out.reset();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// sends the waiting messages to the client
	void sendMessages() {
		// System.out.println("Starting sending messages to client");

		// Loop through the list of messages
		for (Message m : InterthreadHolder.getInstance().getMessages()) {
			// If the message is not from this client then
			if (!m.client.username.equals(client.username)) {
				// if the message has not already been sent to this client.
				if (!sentMessages.contains(m)) {
					// Send the message
					sendMessage(m);
					// Add it to the list of sent messages.
					sentMessages.add(m);
				}
			}

			// System.out.println("Sending message to client: " +
			// m.command.commandType);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		sendMessages();

	}

}
