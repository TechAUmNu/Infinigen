package oldutility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A Utility class for dealing with files.
 * 
 * @author Sri Harsha Chilakapati
 */
public class FileUtil {
	/**
	 * @return The entire source of a file as a single string
	 */
	public static String readFromFile(String name) {
		StringBuilder source = new StringBuilder();
		try {

			InputStreamReader isr = new InputStreamReader(ShaderProgram.class.getClassLoader().getResourceAsStream(name));
			BufferedReader reader = new BufferedReader(isr);

			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}

			reader.close();
		} catch (Exception e) {
			System.err.println("Error loading source code: " + name);
			e.printStackTrace();
		}

		return source.toString();
	}

	/**
	 * @return An array of all the lines of a file
	 */
	public static String[] readAllLines(String name) {
		return readFromFile(name).split("\n");
	}
}
