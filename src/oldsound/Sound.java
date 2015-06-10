package oldsound;

public class Sound {
	private String file;
	private boolean loop;

	public Sound(String file, boolean loop) {
		this.setFile(file);
		this.setLoop(loop);
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}
}
