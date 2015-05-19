package sound;

import java.util.ArrayList;



public class SoundManager implements Runnable {

	ArrayList<Sound> tracks;
	boolean playing;
	SoundPlayer player;

	public SoundManager() {
		tracks = new ArrayList<Sound>();
		player = null;

		addToQueue(new Sound("Res/Sound/StickEngine.wav", true));
		

	}

	public void addToQueue(Sound s) {
		tracks.add(s);
	}

	public void removeFromQueue(Sound s) {
		if (tracks.contains(s)) {
			tracks.remove(s);
		}
	}

	public void playQueue() {
		if (player != null) {
			if (tracks.size() != 0) {
				if (player.finished) {
					player = null;

					playNextTrack();

				}
			}

		} else if (tracks.size() != 0) {
			playNextTrack();
		}

	}

	private void playNextTrack() {
		Sound nextTrack = tracks.get(tracks.size() - 1);
		System.out.println("Now playing " + nextTrack.getFile());
		SoundPlayer sp;
		(new Thread(sp = new SoundPlayer(nextTrack.getFile()))).start();
		player = sp;

		if (!nextTrack.isLoop()) {
			tracks.remove(nextTrack);
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
				playQueue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
