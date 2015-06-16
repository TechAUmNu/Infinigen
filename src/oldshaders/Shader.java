package oldshaders;

public enum Shader {
	Chunk(0);

	private int shaderID;

	Shader(int i) {
		shaderID = i;
	}

	public int GetID() {
		return shaderID;
	}

}
