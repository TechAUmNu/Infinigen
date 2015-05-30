package newGui;

import java.util.ArrayList;
import java.util.List;

public class GuiTabMenu {

	
	//A tab menu is a collection of tabs it will display the menuButton from each tab and call the draw method on the selected tab.
	
	private List<GuiTab> tabs;
	private GuiTab selectedTab;

	
	public GuiTabMenu(){
		tabs = new ArrayList<GuiTab>();		
	}
	
	public void draw(GuiShader shader){
		for(GuiTab gt : tabs){
			gt.getMenuButton().draw(shader);
		}
		selectedTab.draw(shader);
	}

	public List<GuiTab> getTabs() {
		return tabs;
	}

	public void setTabs(List<GuiTab> tabs) {
		this.tabs = tabs;
	}

	public GuiTab getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(GuiTab selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	
}
