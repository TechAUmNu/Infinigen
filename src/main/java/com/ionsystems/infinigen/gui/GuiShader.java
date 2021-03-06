package main.java.com.ionsystems.infinigen.gui;

import main.java.com.ionsystems.infinigen.shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/guiFragmentShader.txt";

	private int location_mouseOver;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getAllUniformLoactions() {
		location_mouseOver = super.getUniformLocation("mouseOver");
	}

	public void loadMouseOver(boolean mouseOver) {
		super.loadBoolean(location_mouseOver, mouseOver);
	}

}
