package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class RawModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8449727902564196263L;
	private int vaoID;
	private ArrayList<Integer> vboIDs;
	private int vertexCount;

	public RawModel(int vaoID, ArrayList<Integer> vboIDs, int vertexCount) {
		this.vaoID = vaoID;
		this.vboIDs = vboIDs;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void cleanUp() {
		GL30.glDeleteVertexArrays(vaoID);
		for (int id : vboIDs) {
			GL15.glDeleteBuffers(id);
		}

	}

}
