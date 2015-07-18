package main.java.com.ionsystems.infinigen.newWorld;

public enum Face {
	
	

	Front(0), Back(1), Top(2), Bottom(3), Left(4), Right(5);
	
	private int face;
	
	Face(int i){
		face = i;
	}
	
	public int getFace(){
		return face;
	}
	
}
