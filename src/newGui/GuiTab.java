package newGui;

import java.util.ArrayList;
import java.util.List;

public class GuiTab {
	
	private List<GuiButton> buttons;
	private List<GuiElement> elements;
	private List<GuiTextElement> textElements;
	private GuiButton menuButton;
	
	
	public GuiTab(){
		buttons = new ArrayList<GuiButton>();
		elements = new ArrayList<GuiElement>();
		textElements = new ArrayList<GuiTextElement>();
	}
	
	public void addButton(GuiButton button){
		buttons.add(button);
	}
	
	public void addElement(GuiElement element){
		elements.add(element);
	}
	
	public void addTextElement(GuiTextElement textElement){
		textElements.add(textElement);
	}
	
	public List<GuiButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<GuiButton> buttons) {
		this.buttons = buttons;
	}

	public List<GuiElement> getElements() {
		return elements;
	}

	public void setElements(List<GuiElement> elements) {
		this.elements = elements;
	}

	public List<GuiTextElement> getTextElements() {
		return textElements;
	}

	public void setTextElements(List<GuiTextElement> textElements) {
		this.textElements = textElements;
	}

	public void draw(GuiShader shader){
		for(GuiButton b : buttons){
			b.draw(shader);
		}
		for(GuiElement e : elements){
			e.draw(shader);
		}
		for(GuiTextElement te : textElements){
			te.draw(shader);
		}
	}

	public GuiButton getMenuButton() {
		return menuButton;
	}

	public void setMenuButton(GuiButton menuButton) {
		this.menuButton = menuButton;
	}
	
	
}
