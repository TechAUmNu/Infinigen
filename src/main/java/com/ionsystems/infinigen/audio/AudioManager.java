package main.java.com.ionsystems.infinigen.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class AudioManager{
	
	private static HashMap<String,Audio> audioFiles = new HashMap<String,Audio>();
	private static ArrayList<Audio> loadedAudio = new ArrayList<Audio>();
	private static String audioLocation = "res/sound/";
	
	public static void loadWAVAudioFile(String audioFile){
		try {
			Audio a = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(audioLocation + audioFile));
			audioFiles.put(audioFile, a);
			loadedAudio.add(a);
		} catch (IOException e) {
			System.err.println("Failed to load WAV file: " + audioFile);
			e.printStackTrace();
		}
	}
	
	public static void loadOGGAudioFile(String audioFile){
		try {
			Audio a = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(audioLocation +audioFile));
			audioFiles.put(audioFile, a );
			loadedAudio.add(a);
		} catch (IOException e) {
			System.err.println("Failed to load OGG file: " + audioFile);
			e.printStackTrace();
		}
	}
	
	public static void loadAIFAudioFile(String audioFile){
		try {
			Audio a = AudioLoader.getAudio("AIF", ResourceLoader.getResourceAsStream(audioLocation +audioFile));
			audioFiles.put(audioFile,a  );
			loadedAudio.add(a);
		} catch (IOException e) {
			System.err.println("Failed to load aif file: " + audioFile);
			e.printStackTrace();
		}
	}
	
	public static void loadMODXMAudioFile(String audioFile){
		try {
			Audio a = AudioLoader.getAudio("MOD", ResourceLoader.getResourceAsStream(audioLocation +audioFile));
			audioFiles.put(audioFile, a );
			loadedAudio.add(a);
		} catch (IOException e) {
			System.err.println("Failed to load MOD file: " + audioFile);
			e.printStackTrace();
		}
	}
	
	
	public static void playMusic(String audioFile, float pitch, float gain, boolean loop){
		audioFiles.get(audioFile).playAsMusic(pitch, gain, loop);
	}
	
	public static void playSoundEffect(String audioFile, float pitch, float gain, boolean loop){
		audioFiles.get(audioFile).playAsSoundEffect(pitch, gain, loop);
	}
	
	public static void playSoundEffect(String audioFile, float pitch, float gain, boolean loop, float x, float y, float z){
		audioFiles.get(audioFile).playAsSoundEffect(pitch, gain, loop, x, y, z);
	}

	public static void cleanup() {
		for(Audio a : loadedAudio){
			a.getBufferID()
		}
		loadedAudio
		AudioLoader.
		
	}
	
	

}
