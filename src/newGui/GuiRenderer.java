package newGui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import newModels.RawModel;
import newRendering.Loader;
import newUtility.Maths;

public class GuiRenderer {

	private GuiShader shader;

	private List<GuiButton> buttons;
	private List<GuiElement> elements;
	private List<GuiTextElement> textElements;
	private List<GuiTabMenu> tabMenus;
	private List<GuiMenu> menus;

	public GuiRenderer() {
		shader = new GuiShader();
		buttons = new ArrayList<GuiButton>();
		elements = new ArrayList<GuiElement>();
		textElements = new ArrayList<GuiTextElement>();
		tabMenus = new ArrayList<GuiTabMenu>();
		menus = new ArrayList<GuiMenu>();
	}

	public void render() {
		shader.start();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

		// Replace with rendering each element
		for (GuiElement e : elements) {
			e.draw(shader);
		}
		for (GuiTextElement te : textElements) {
			te.draw(shader);
		}
		for (GuiButton gb : buttons) {
			gb.draw(shader);
		}
		for (GuiTabMenu gtm : tabMenus) {
			gtm.draw(shader);
		}
		for (GuiMenu m : menus) {
			m.draw(shader);
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	public void addElement(GuiElement guiElement) {
		elements.add(guiElement);
	}

	public void addTextElement(GuiTextElement gte) {
		textElements.add(gte);
	}

	public void addButton(GuiButton gb) {
		buttons.add(gb);
	}

	public void addTabMenu(GuiTabMenu gtm) {
		tabMenus.add(gtm);
	}

	public void addMenu(GuiMenu gm) {
		menus.add(gm);
	}
}
