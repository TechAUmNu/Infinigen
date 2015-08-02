package Testing;

public class Testing {

	public Testing() {
		spiral(5,5, 0, 1);
	}
	
	public void spiral(int X, int Y, int xStart, int yStart) {

		int x, y, dx, dy;
		x = 0;
		y = 0;
		dx = 0;
	
		
				
		dy = -1;
		
		
		
		dx += xStart;
		x += xStart;
		y += yStart;
		dy += yStart;
		
		int t = Math.max(X, Y);

		int maxI = t * t;
		for (int i = 0; i < maxI; i++) {
			if ((-X / 2 <= x) && (x <= X / 2) && (-Y / 2 <= y) && (y <= Y / 2)) {
				System.out.println("" + x + " " + y);
			}
			if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
				t = dx;
				dx = -dy;
				dy = t;
			}
			x += dx;
			y += dy;
		}
	}
	
	
	public static void main(String[] args){
		new Testing();
	}

}
