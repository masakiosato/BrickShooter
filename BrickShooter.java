import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BrickShooter extends JPanel implements KeyListener, Runnable {
	static int screenWidth = 1100, screenHeight = screenWidth * 2 / 3;
	//ints
	int gunWidth = screenWidth / 10, 
			gunHeight = gunWidth / 5, 
			gunX = (screenWidth / 2) - (gunWidth / 2), 
			gunY = screenHeight - gunHeight, 
			brickWidth = screenWidth / 15, 
			brickHeight = brickWidth * 2 / 3,
			brickX = 0, 
			brickY = 0,
			bulletSize = screenWidth / 100,
			shotCount = 0,
			brickCount = 0,
			dropCount = 0,
			level = 0;
	//booleans
	boolean right = false, 
			left = false, 
			win = false, 
			loss = false, 
			pause = false, 
			poweredUp = false, 
			gameStarted = false, 
			controls = false, 
			rankings = false,
			settings = false,
			waiting = false,
			nameInputting = false;
	//fonts
	Font title = new Font("Code Bold", Font.PLAIN, screenWidth / 10),
			subTitle = new Font("Code Bold", Font.PLAIN, screenWidth / 15),
			bodyText = new Font("Helvetica", Font.PLAIN, screenWidth / 30);
	Image lossImage;
	String nameInput = "";
	Rectangle Gun = new Rectangle(gunX, gunY, gunWidth, gunHeight);
	Brick[] Bricks = new Brick[4];
	Rectangle[] Bullets = new Rectangle[1];
	Rectangle[] Menu = { 
			new Rectangle(0, 0, screenWidth, screenHeight / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 2 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 3 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 4 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight),
			new Rectangle(0, 0, screenWidth, screenHeight * 6 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 7 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 8 / 5),
			new Rectangle(0, 0, screenWidth, screenHeight * 9 / 5),
	};
	Score[] scores = new Score[5];
	Colortheme[] themes = {
			new Colortheme(Color.WHITE, Color.LIGHT_GRAY, new Color(121, 173, 220), Color.BLACK, new Color(255, 255, 255, 175)),
			new Colortheme(Color.DARK_GRAY, Color.LIGHT_GRAY, new Color(133,163,143), Color.WHITE, new Color(0, 0, 0, 175)),
			new Colortheme(Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK, new Color(255, 255, 255, 175)),
			new Colortheme(Color.DARK_GRAY, Color.LIGHT_GRAY, Color.WHITE, Color.WHITE, new Color(0, 0, 0, 175)),
			new Colortheme(Color.ORANGE, Color.ORANGE.brighter(), Color.WHITE, Color.BLACK, new Color(255, 255, 255, 175))
	};
	int currentTheme = themes.length * 100;
	
	public static void main(String[] args) {
		BrickShooter game = new BrickShooter();
		game.getImage();
		game.addKeyListener(game);
		game.setFocusable(true);
		
		
		JFrame frame = new JFrame();
		frame.setSize(screenWidth,screenHeight+22);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		
		Thread t = new Thread(game);
		t.start();
	}
	public void initializeVariables() {
		Gun.x = (screenWidth / 2) - (gunWidth / 2);
		brickWidth = screenWidth / 15;
		brickHeight = brickWidth * 2 / 3;
		brickX = 0;
		brickY = 0;
		win = false;
		loss = false;
		poweredUp = false;
		controls = false;
		rankings = false;
		settings = false;
		pause = true;
		nameInputting = false;
		if (gameStarted) {
			Bricks = new Brick[45];
			Bullets = new Rectangle[25];
		}
		else {
			Bricks = new Brick[4];
			Bullets = new Rectangle[1];
		}
		nameInput = "";
		
	}
	public void getImage() {
		try {
			lossImage = ImageIO.read(new File("/Users/masakiosato/Desktop/pepe.jpg"));
		} catch (IOException e) {
			System.out.println("Couldn't get backround image");
		}
	}
	public void run() {
		createBricks();
		while (true) { //this loops still happens when the game is pause
			while (!pause) { //in game
				//bricks falling
				dropCount += level * screenHeight / 4;
				if (dropCount >= 1200) {
					dropCount = 0;
					for (int i = 0; i < Bricks.length; i++) {
						if (Bricks[i] != null) {
							Bricks[i].y++;
						}
					}
				}
				
				// bullet movement
				for(int i = 0; i < Bullets.length; i++) {//check each bullet
					if (Bullets[i] != null) { // if bullet exists
						if (poweredUp) Bullets[i].y -= screenHeight / 100; // ball goes 2 times faster
						else Bullets[i].y -= screenHeight/200;
						if (Bullets[i].y <= 0) Bullets[i] = null;
					}
				}
				
				//right&left gun movement
				if (left == true) {   
					Gun.x -= screenWidth / 300;
					right = false;
				} else if (right == true) {
					Gun.x += screenWidth / 300;
					left = false;
				}
				
				//left&right boundaries for gun
				if (Gun.x <= 0) Gun.x = 0;
				if (Gun.x >= screenWidth - gunWidth) Gun.x = screenWidth - gunWidth;
				
				//win or lose
				for (int i = 0; i < Bricks.length; i++) {//check each brick
					for(int j = 0; j < Bullets.length; j++) {//check each bullet
						if (Bullets[j] != null && Bricks[i] != null) { // if bullet & brick exists
							if (Bricks[i].intersects(Bullets[j])) {
								if (!gameStarted) {
									Bullets[j] = null;
									if (i == 0) this.nextLevel();
									else if (i == 1) {
										controls = true;
										pause = true;
										break;
									}
									else if (i == 2) {
										rankings = true;
										pause = true;
										break;
									}
									else if (i == 3) {
										settings = true;
										pause = true;
										break;
									}	
								} else {
									Bricks[i].lives--;
									Bullets[j] = null;	
									if (Bricks[i].lives == 0) {
										Bricks[i] = null;
										brickCount++;
									}
								}
							}
						}						
					}
					if (Bricks[i] != null) {
						if (Bricks[i].y + Bricks[i].height >= screenHeight) { // loss
							loss = true;
							repaint();
						}
					}
				}
				
				// win
				if (brickCount > 0 && brickCount % Bricks.length == 0 && brickCount / Bricks.length == level) {
					win = true;
					repaint();
				}
				if (win || loss) pause = true;
				
				//final repaint
				repaint();
				
				//try and catch is what accepts the KeyEvents.
				try {
					Thread.sleep(10);
				} catch (Exception ex) {}
			}
			//this repaint will allow us to have an interactive pause screen which i haven't done yet
			repaint();
			//try and catch is what accepts the KeyEvents.
			//This one is specifically for when the game is paused.
			try {
				Thread.sleep(10);
			} catch (Exception ex) {}
		}
	}
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    int x = (rect.width - metrics.stringWidth(text)) / 2;
	    int y = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	public void createBricks() {
		if (Bricks.length < 15) {
			brickWidth *= 3;
			brickHeight *= 3;
			brickX = (screenWidth - Bricks.length * brickWidth) / (Bricks.length + 1);
			for (int i = 0; i < Bricks.length; i++) {
				Bricks[i] = new Brick(brickX + i * (brickWidth + brickX), brickHeight, brickHeight, brickWidth, 1);
			}
		} else {
			brickWidth = screenWidth / 15;
			brickHeight = brickWidth * 2 / 3;
			for (int i = 0; i < Bricks.length; i++) {
				Bricks[i] = new Brick(i % 15 * brickWidth , i / 15  * brickHeight, brickHeight, brickWidth, 3 - (i / 15));
				if (Bricks[i].lives < 1) Bricks[i].lives = 1;
			}
		}
	}
	public void nextLevel() {
		requestFocus(true);
		level++;
		gameStarted = true;
		waiting = true;
		initializeVariables();
		createBricks();
		repaint();
	}
	public void restart() {
		requestFocus(true);
		Score k = new Score(brickCount, nameInput);
		for(int i = 0; i < scores.length; i++) {
			if (k != null) {
				if (scores[i] == null) {
					scores[i] = k;
					k = null;
				} else {
					if (scores[i].score < brickCount) {
						Score temp = k;
						k = scores[i];
						scores[i] = temp;
					}
				}
			}	
		}
		level = 0;
		brickCount = 0;
		gameStarted = false;
		initializeVariables();
		pause = false;
		createBricks();
		repaint();
	}
	public void paint(Graphics g) {
		//background
		g.setColor(themes[currentTheme % themes.length].background);
		g.fillRect(0, 0, screenWidth, screenHeight);
 
		//grid drawing
	    g.setColor(themes[currentTheme % themes.length].grid);
	    for (int i = 50; i < screenWidth; i += 50) {
	    	g.drawLine(i, 0, i, screenHeight); 
	        g.drawLine(0, i, screenWidth, i);
	    }
	    
	    //gun
		g.setColor(themes[currentTheme % themes.length].gun);
		g.fillRect(Gun.x, Gun.y, Gun.width, Gun.height);
		
		//draw if the brick exists
		for (int i = 0; i < Bricks.length; i++) {
			if (Bricks[i] != null) {
				if (Bricks[i].lives == 3) g.setColor(themes[currentTheme % themes.length].brick3);
				else if (Bricks[i].lives == 2) g.setColor(themes[currentTheme % themes.length].brick2);
				else g.setColor(themes[currentTheme % themes.length].brick1);
				g.fillRect(Bricks[i].x + 1, Bricks[i].y + 1, Bricks[i].width - 1, Bricks[i].height - 1);
			}
			if (!gameStarted) {
				g.setColor(themes[currentTheme % themes.length].background);
				g.setFont(bodyText);
				if (i == 0) g.drawString("START", Bricks[i].x, Bricks[i].y + brickHeight);
				if (i == 1) g.drawString("CONTROLS", Bricks[i].x, Bricks[i].y + brickHeight);
				if (i == 2) g.drawString("RANKINGS", Bricks[i].x, Bricks[i].y + brickHeight);
				if (i == 3) g.drawString("SETTINGS", Bricks[i].x, Bricks[i].y + brickHeight);
			}
		}
		
		//title before the game starts
		if (!gameStarted) {
			g.setColor(themes[currentTheme % themes.length].title);
			drawCenteredString(g, "BRICK SHOOTER", Menu[0], title);
		}
		
		//bullets
		g.setColor(themes[currentTheme % themes.length].bullet);
		for (int i = 0; i < Bullets.length; i++) {
			if (Bullets[i] != null) g.fillOval(Bullets[i].x, Bullets[i].y, Bullets[i].width, Bullets[i].height);			
		}
		
		g.setColor(themes[currentTheme % themes.length].text);
		g.setFont(bodyText);
		g.drawString("Score: " + brickCount, 10,  screenHeight - 10);
		
		if (pause) {
			g.setColor(themes[currentTheme % themes.length].pause);
			g.fillRect(0, 0, screenWidth, screenHeight);
			g.setColor(themes[currentTheme % themes.length].text);
			if (win) {
				drawCenteredString(g, "LEVEL COMPLETE", Menu[0], subTitle);
				drawCenteredString(g, "Press shift for the next level", Menu[1], bodyText);
				drawCenteredString(g, "Press delete to quit", Menu[2], bodyText);
			} else if (loss) {
				g.drawImage(lossImage, 0, 0, screenWidth, screenHeight, this);
				drawCenteredString(g, "GAME OVER", Menu[0], subTitle);
				drawCenteredString(g, "Press delete to quit", Menu[1], bodyText);
			} else if (controls) {
				drawCenteredString(g, "CONTROLS", Menu[0], subTitle);
				drawCenteredString(g, "Left and Right: Move gun side to side", Menu[1], bodyText);
				drawCenteredString(g, "Up: Shoot bullet", Menu[2], bodyText);
				drawCenteredString(g, "Shift: Pause/Unpause game", Menu[3], bodyText);
				drawCenteredString(g, "Delete: Quit game (during a pause)", Menu[4], bodyText);
				drawCenteredString(g, "Press space to go back", Menu[6], bodyText);
			} else if (rankings) {
				drawCenteredString(g, "RANKINGS", Menu[0], subTitle);
				for (int i = 0; i < scores.length; i++) {
					if (scores[i] != null) drawCenteredString(g, scores[i].print(), Menu[i+1], bodyText);
				}
				drawCenteredString(g, "Press space to go back", Menu[6], bodyText);
			} else if (settings) {
				drawCenteredString(g, "SETTINGS", Menu[0], subTitle);
				drawCenteredString(g, "Press right and left to choose color themes", Menu[1], bodyText);
				drawCenteredString(g, "Press space to go back", Menu[2], bodyText);
				
			} else if (nameInputting) {
				drawCenteredString(g, "GAME OVER", Menu[0], subTitle);
				drawCenteredString(g, "Please enter your name:", Menu[1], bodyText);
				drawCenteredString(g, nameInput, Menu[2], bodyText);
				if (nameInput.length() > 0) drawCenteredString(g, "Press enter to submit", Menu[3], bodyText);
			} else if (waiting) {
				drawCenteredString(g, "LEVEL " + level, Menu[0], subTitle);
				drawCenteredString(g, "Press shift to start.", Menu[2], bodyText);
			} else {
				
				g.drawString("Paused", 70, 70);
				g.drawString("Press shift to continue.", 70, 100);
				g.drawString("Press delete to quit.", 70, 130);
			}
		}
	}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!pause) {
			if (keyCode == KeyEvent.VK_LEFT) left = true;
			if (keyCode == KeyEvent.VK_RIGHT) right = true;
			if (keyCode == KeyEvent.VK_UP) {
				if (Bullets[(shotCount) % Bullets.length] == null) {
					Bullets[(shotCount) % Bullets.length] = new Rectangle(Gun.x + (Gun.width / 2), screenHeight - Gun.height, bulletSize, bulletSize);
					shotCount++;
				}
			}
			if (keyCode == KeyEvent.VK_SHIFT && gameStarted) pause = true;
		} else {
			if (keyCode == KeyEvent.VK_SPACE && !gameStarted) pause = false;
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				win = false;
				loss = false;
				controls = false;
				rankings = false;
				settings = false;
				nameInputting = true;
				waiting = false;
			}
			if (win) {
				if (keyCode == KeyEvent.VK_SHIFT) this.nextLevel();
			} else if (loss) { //no controls other than Q for loss screen
			} else if (controls) {
				if (keyCode == KeyEvent.VK_SPACE) controls = false;
			} else if (rankings) {
				if (keyCode == KeyEvent.VK_SPACE) rankings = false;
			} else if (settings) {
				if (keyCode == KeyEvent.VK_SPACE) settings = false;
				if (keyCode == KeyEvent.VK_RIGHT) currentTheme++;
				if (keyCode == KeyEvent.VK_LEFT) currentTheme--;
			} else if (nameInputting) {
				if (keyCode == KeyEvent.VK_1) nameInput += 1;
				if (keyCode == KeyEvent.VK_2) nameInput += 2;
				if (keyCode == KeyEvent.VK_3) nameInput += 3;
				if (keyCode == KeyEvent.VK_4) nameInput += 4;
				if (keyCode == KeyEvent.VK_5) nameInput += 5;
				if (keyCode == KeyEvent.VK_6) nameInput += 6;
				if (keyCode == KeyEvent.VK_7) nameInput += 7;
				if (keyCode == KeyEvent.VK_8) nameInput += 8;
				if (keyCode == KeyEvent.VK_9) nameInput += 9;
				if (keyCode == KeyEvent.VK_0) nameInput += 0;
				if (keyCode == KeyEvent.VK_A) nameInput += "A";
				if (keyCode == KeyEvent.VK_B) nameInput += "B";
				if (keyCode == KeyEvent.VK_C) nameInput += "C";
				if (keyCode == KeyEvent.VK_D) nameInput += "D";
				if (keyCode == KeyEvent.VK_E) nameInput += "E";
				if (keyCode == KeyEvent.VK_F) nameInput += "F";
				if (keyCode == KeyEvent.VK_G) nameInput += "G";
				if (keyCode == KeyEvent.VK_H) nameInput += "H";
				if (keyCode == KeyEvent.VK_I) nameInput += "I";
				if (keyCode == KeyEvent.VK_J) nameInput += "J";
				if (keyCode == KeyEvent.VK_K) nameInput += "K";
				if (keyCode == KeyEvent.VK_L) nameInput += "L";
				if (keyCode == KeyEvent.VK_M) nameInput += "M";
				if (keyCode == KeyEvent.VK_N) nameInput += "N";
				if (keyCode == KeyEvent.VK_O) nameInput += "O";
				if (keyCode == KeyEvent.VK_P) nameInput += "P";
				if (keyCode == KeyEvent.VK_Q) nameInput += "Q";
				if (keyCode == KeyEvent.VK_R) nameInput += "R";
				if (keyCode == KeyEvent.VK_S) nameInput += "S";
				if (keyCode == KeyEvent.VK_T) nameInput += "T";
				if (keyCode == KeyEvent.VK_U) nameInput += "U";
				if (keyCode == KeyEvent.VK_V) nameInput += "V";
				if (keyCode == KeyEvent.VK_W) nameInput += "W";
				if (keyCode == KeyEvent.VK_X) nameInput += "X";
				if (keyCode == KeyEvent.VK_Y) nameInput += "Y";
				if (keyCode == KeyEvent.VK_Z) nameInput += "Z";
				if (keyCode == KeyEvent.VK_SPACE) nameInput += " ";
				if (keyCode == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0) nameInput = nameInput.substring(0, nameInput.length()-1);
				if (keyCode == KeyEvent.VK_ENTER && nameInput.length() > 0) this.restart();
			} else if (waiting) {
				if (keyCode == KeyEvent.VK_SHIFT) {
					pause = false;
					waiting = false;
				}
			} else {
				if (keyCode == KeyEvent.VK_SHIFT) pause = false;
			}
		}
	}
 	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) left = false;
		if (keyCode == KeyEvent.VK_RIGHT) right = false;
	}
}
