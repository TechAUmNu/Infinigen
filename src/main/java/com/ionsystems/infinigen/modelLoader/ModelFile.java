package main.java.com.ionsystems.infinigen.modelLoader;

public class ModelFile {
	private String filePath;
	private String folder;
	private String spawnName;
	
	public String getFilePath() {		
		return filePath;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getSpawnName() {
		return spawnName;
	}

	public void setSpawnName(String spawnName) {
		this.spawnName = spawnName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public ModelFile(String filePath, String folder, String spawnName) {
		super();
		this.filePath = filePath;
		this.folder = folder;
		this.spawnName = spawnName;
	}
}
