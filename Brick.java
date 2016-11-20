import java.awt.Rectangle;

public class Brick extends Rectangle {
	public int lives;
	public Brick(int x, int y, int width, int height, int l) {
		super(x, y, height, width);
		lives = l;
	}
}
