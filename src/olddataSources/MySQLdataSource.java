package olddataSources;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oldSecurity.PasswordEncryptionService;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MySQLdataSource {

	private MysqlDataSource source;
	
	public MySQLdataSource(){
		//TODO move this connection info into a config file
		
		source = new MysqlDataSource();
		source.setUser("StickEngineUser");
		source.setPassword("wFGjGuQUGwSeCp2n");
		source.setServerName("play2.ghsgaming.com");
		source.setDatabaseName("stickengine");
		System.out.println("Connected to mysql");
		
	}
	
	
	
	public boolean register(String username, byte[] hash, byte[] salt) {
		try {
			//Get a new connection to the database
			Connection c = source.getConnection();
			
			//Create a prepared statement using the given user info
			PreparedStatement ps = c.prepareStatement("INSERT INTO `users`(`username`, `hash`, `salt`) VALUES (?,?,?)");
			ps.setString(1, username);
			ps.setBytes(2, hash);
			ps.setBytes(3, salt);
			
			//Execute the statement
			boolean ok = ps.execute();
			
			//Close the connection			
			ps.close();
			c.close();
			if(ok) return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean login(String username, String attemptedPassword) {
		try {
			//Get an instance of the password encryption service
			PasswordEncryptionService pes = new PasswordEncryptionService();
			
			
			//Get a new connection to the database
			Connection c = source.getConnection();
			
			//Create a prepared statement using the given user info
			PreparedStatement ps = c.prepareStatement("SELECT `hash`, `salt` FROM `users` WHERE `username` = ?");
			ps.setString(1, username);
						
			//Execute the statement
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				byte[] hash = rs.getBytes(1);
				byte[] salt = rs.getBytes(2);
				if(pes.authenticate(attemptedPassword, hash, salt)){
					return true;
				}else{
					return false;
				}
			}
			//Close the connection			
			ps.close();
			c.close();
		} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
}
