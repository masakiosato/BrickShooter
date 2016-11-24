import java.awt.Color;

public class Colortheme {
	Color background, grid, gun, bullet, brick1, brick2, brick3, text, pause, title;
	
	public Colortheme(Color ba, Color gr, Color br, Color t, Color p) {
		background = ba;
		grid = gr;
		gun = br;
		bullet = br;
		brick1 = br;
		brick2 = brick1.darker();
		brick3 = brick2.darker();
		text = t;
		pause = p;
		title = br;
	}
}
