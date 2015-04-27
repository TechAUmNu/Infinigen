package hud;

public class HUDManager {
	//List of hud elements
	
	HUDWindow mywindow;
	
	public HUDManager(){
		mywindow = new HUDWindow(1, 1, 5, 5){
			public void action() {
				System.out.println("It works!");
			}
		};
		
		mywindow.action();
	}
	
	
	
	public static void main(String[] args){
		new HUDManager();
	}

	
}
