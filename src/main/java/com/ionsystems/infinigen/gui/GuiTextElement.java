package main.java.com.ionsystems.infinigen.gui;

import main.java.com.ionsystems.infinigen.rendering.Loader;

import org.lwjgl.util.vector.Vector2f;

public class GuiTextElement extends GuiElement {

	public GuiTextElement(Vector2f position, String texture, Loader loader) {
		super(position, texture, loader);
		// TODO Auto-generated constructor stub
	}

	public GuiTextElement(Vector2f position, Vector2f detectionSize, String texture, Loader loader) {
		super(position, detectionSize, texture, loader);
		// TODO Auto-generated constructor stub
	}

	public GuiTextElement(Vector2f position, Vector2f size, Vector2f detectionSize, String texture, Loader loader) {
		super(position, size, detectionSize, texture, loader);
		// TODO Auto-generated constructor stub
	}
}
