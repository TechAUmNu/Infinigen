package hud;

public abstract class HUDWindow {
	//A hud window contains the position and action.
	private int x, y, x2, y2;
	
	public HUDWindow(int x, int y, int x2, int y2){
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void checkClicked(int mouseX, int mouseY){
		if(mouseX > x && mouseX < x2){
			action();
		}else if(mouseY > y && mouseY < y2){
			action();
		}
	}
	
	public void draw() {
		
	}

	
	public abstract void action();
	
	
}
