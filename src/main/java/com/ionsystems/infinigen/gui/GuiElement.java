package main.java.com.ionsystems.infinigen.gui;

import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.rendering.DisplayManager;
import main.java.com.ionsystems.infinigen.rendering.Loader;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

public class GuiElement {

	private RawModel box;
	private Vector3f texture;
	private Vector2f position;
	private Vector2f detectionSize;
	private float guiRatioX, guiRatioY;

	public GuiElement(Vector2f position, String texture, Loader loader) {
		super();

		loadTexture(texture, loader);
		scale();
		generateModel(position, loader);
		this.position = position;
	}

	public GuiElement(Vector2f position, Vector2f detectionSize, String texture, Loader loader) {
		super();
		this.detectionSize = detectionSize;
		loadTexture(texture, loader);
		scale();
		generateModel(position, loader);
		this.position = position;
	}

	public GuiElement(Vector2f position, Vector2f size, Vector2f detectionSize, String texture, Loader loader) {
		super();
		this.detectionSize = detectionSize;
		this.position = position;
		loadTexture(texture, loader, size);
		scale();
		generateModel(position, loader);

	}

	private void loadTexture(String textureName, Loader loader) {
		texture = loader.loadTexture(textureName);
	}

	private void loadTexture(String textureName, Loader loader, Vector2f size) {
		texture = loader.loadTexture(textureName);
		texture.x = size.x;
		texture.y = size.y;
	}

	public void scale() {

		texture.y = texture.y * DisplayManager.getGUIScale();
		texture.x = texture.x * DisplayManager.getGUIScale();

		if (detectionSize == null) {
			detectionSize = new Vector2f(texture.x, texture.y);
		} else {
			detectionSize.x = detectionSize.x * guiRatioX;
			detectionSize.y = detectionSize.y * guiRatioY;
		}

	}

	private void generateModel(Vector2f position, Loader loader) {
		float[] positions = { convertCoord(position.x, Display.getWidth()), convertCoord(position.y + texture.y, Display.getHeight()),
				convertCoord(position.x + texture.x, Display.getWidth()), convertCoord(position.y + texture.y, Display.getHeight()),
				convertCoord(position.x, Display.getWidth()), convertCoord(position.y, Display.getHeight()),
				convertCoord(position.x + texture.x, Display.getWidth()), convertCoord(position.y, Display.getHeight()),

		};

		float[] textureCoords = { 0, 0, 1, 0, 0, 1, 1, 1 };

		box = loader.loadToVAO(positions, textureCoords, 2);

	}

	public int getTexture() {
		return (int) texture.z;
	}

	public RawModel getBox() {
		return box;
	}

	public boolean CheckHover() {	
		if (Mouse.getX() < position.x + detectionSize.x && Mouse.getX() > position.x && !Mouse.isGrabbed()) {
			if (Mouse.getY() < position.y + detectionSize.y && Mouse.getY() > position.y) {
				return true;
			}
		}
		return false;
	}

	private float convertCoord(float coord, float maxSize) {

		return (((coord * 2) / maxSize) - 1);
	}

	public void draw(GuiShader shader) {
		GL30.glBindVertexArray(getBox().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture());
		shader.loadMouseOver(CheckHover());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, getBox().getVertexCount());
	}

}
