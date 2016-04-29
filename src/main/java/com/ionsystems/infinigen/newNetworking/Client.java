package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.Serializable;

public class Client implements Serializable {
	public Client(String string) {
		username = string;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7772468712651432093L;
	public String username;
}
