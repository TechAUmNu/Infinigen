package sound;

import javax.sound.sampled.*;
import java.io.*;

public class SoundPlayer implements Runnable {
	boolean finished;
	String filename;

	SoundPlayer(String filename) {
		this.filename = filename;

	}

	@Override
	public void run() {
		int total, totalToRead, numBytesRead, numBytesToRead;
		byte[] buffer;
		boolean stopped;
		AudioFormat wav;

		SourceDataLine lineIn;
		DataLine.Info info;

		FileInputStream fis;

		// AudioFormat(float sampleRate, int sampleSizeInBits,
		// int channels, boolean signed, boolean bigEndian)
		wav = new AudioFormat(48000, 16, 2, true, false);
		info = new DataLine.Info(SourceDataLine.class, wav);

		buffer = new byte[1024 * 333];
		numBytesToRead = 1024 * 333;
		numBytesRead = numBytesToRead;
		total = 0;
		stopped = false;
		finished = false;

		if (!AudioSystem.isLineSupported(info)) {
			System.out.print("no support for " + wav.toString());
		}
		try {
			// Obtain and open the line.
			lineIn = (SourceDataLine) AudioSystem.getLine(info);
			lineIn.open(wav);
			lineIn.start();
			fis = new FileInputStream(new File(filename));
			totalToRead = fis.available();
			System.out.println(totalToRead);
			System.out.println(filename);

			while (total < totalToRead && !stopped) {
				numBytesRead = fis.read(buffer, 0, numBytesToRead);
				if (numBytesRead == -1)
					break;
				total += numBytesRead;
				lineIn.write(buffer, 0, numBytesToRead);
			}
			finished = true;

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException nofile) {
			nofile.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}

	}

}