/*
Notes:
	- Game starts out paused so unpause to start.
	- You can change levels whenever - even during a pause.
	- All number variables such as sizes and speeds are determined by the very first variable: the screenWidth.
	- The power up toggle is just for testing a possible power up.
	- When the program runs or is replayed, gameStarted is false (the game hasn't started yet).
		When you press shift, the gameStarted becomes true. This makes a different pause screen.

Controls: 
	- Left and Right: Move gun side to side.
	- Up: Shoot bullet.
	- Shift: Pause/Unpause game.
	- Enter: Restart game.
	- P: Power up toggle (Ball speeds up).
	- C: Show Controls.
	- 1~5: Level Selection.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BrickShooter extends JPanel implements KeyListener, ActionListener, Runnable {
	//board size. try to keep screenWidth a multiple of 300 min = 300, max = 6000
	static int screenWidth = 900, screenHeight = screenWidth * 2 / 3;
	//movement keys
	boolean right = false, left = false;
	//gun dimensions
	int gunWidth = screenWidth / 10;
	int gunHeight = gunWidth / 5;
	//gun starting spot
	int gunX = (screenWidth / 2) - (gunWidth / 2), gunY = screenHeight - gunHeight;
	//brick dimensions
	int brickWidth = screenWidth / 15, brickHeight = brickWidth * 2 / 3;
	// bricks starting position
	int brickX = 0, brickY = 0;
	// bullet dimensions 
	int bulletSize = screenWidth / 100;
	// declaring bullet, gun, & bricks
	Rectangle Gun = new Rectangle(gunX, gunY, gunWidth, gunHeight);
	Brick[] Bricks = new Brick[45]; //2 layers
	Rectangle[] Bullet = new Rectangle[25];
	
	//game over conditions
	boolean brickFellDown = false, bricksOver = false;
	
	//bullet shot?
	int shotCount = 0;
	
	//count of broken bricks
	int count = 0;
	//bricks dropping rate. When brickDrop is higher, the game is harder. This can act as levels.
	int dropCount = 0, dropCountRate = screenHeight / 2;
	//"win" or "lose"
	String status;
	//start the game paused
	boolean pause = true;
	boolean poweredUp = false;
	boolean gameStarted = false;
	boolean controls = false;
	private Image backgroundImageLoss;
	
	public void initializeVariables(){
		Gun.x = (screenWidth / 2) - (screenWidth / 20);
		brickX = 0;
		brickY = 0;
		Bricks = new Brick[45];
		Bullet = new Rectangle[25];
		brickFellDown = false;
		bricksOver = false;
		count = 0;
		dropCountRate = screenHeight / 2;
		pause = true;
		poweredUp = false;
		gameStarted = false;
	}
	public void getImage() {
		try {
			backgroundImageLoss = ImageIO.read(new File("/Users/masakiosato/Desktop/pepe.jpg"));
		} catch (IOException e) {
			System.out.println("Couldn't get backround image");
		}
	}
	public static void main (String[] args) {
		BrickShooter game = new BrickShooter();
		JFrame frame = new JFrame();
		game.getImage();
		
		//height+22 takes the top border into account
		frame.setSize(screenWidth,screenHeight+22);
		
		//quits when you close the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//enters game into the jframe
		frame.add(game);
		
		//idk what this does but it won't work if i take it out
		frame.setVisible(true);
		
		//lets just keep the window at a fixed size
		frame.setResizable(false);
		
		//keeps the game window in the center of the screen
		frame.setLocationRelativeTo(null);
		
		//idk what this does but it won't work if i take it out
		game.addKeyListener(game);
		game.setFocusable(true);
		Thread t = new Thread(game);
		t.start();
	}
	public void paint(Graphics g) {		
		if (!brickFellDown && !bricksOver) {
			//background
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, screenWidth, screenHeight);
			
	 
			//grid drawing
		    g.setColor(Color.LIGHT_GRAY);
		    for (int i = 50; i < screenWidth; i += 50) {
		    	g.drawLine(i, 0, i, screenHeight); 
		        g.drawLine(0, i, screenWidth, i);
		    }
		    
		       
		    //gun
			g.setColor(Color.green);
			g.fillRect(Gun.x, Gun.y, Gun.width, Gun.height);
		       
			//draw if the brick exists
			for (int i = 0; i < Bricks.length; i++) {
				if (Bricks[i] != null) {
					if (Bricks[i].lives == 3) g.setColor(Color.red);
					else if (Bricks[i].lives == 2) g.setColor(Color.blue);
					else g.setColor(Color.green);
					g.fillRect(Bricks[i].x + 1, Bricks[i].y + 1, Bricks[i].width - 1, Bricks[i].height - 1);
				}
			}
			
			//draw bullets if shot.
			g.setColor(Color.red);
			for (int i = 0; i < Bullet.length; i++) {
				if (Bullet[i] != null) g.fillOval(Bullet[i].x, Bullet[i].y, Bullet[i].width, Bullet[i].height);			
			}
			
			if (pause) {
				g.setColor(new Color(255, 255, 255, 175)); //175 is the opacity
				g.fillRect(0, 0, screenWidth, screenHeight);
				g.setColor(Color.BLACK);
				if (controls) {
					g.drawString("Controls: ", 70, 70);
					g.drawString("Left and Right: Move gun side to side.", 70, 100);
					g.drawString("Up: Shoot bullet.", 70, 130);
					g.drawString("Shift: Pause/Unpause game.", 70, 160);
					g.drawString("Enter: Restart game.", 70, 190);
					g.drawString("P: Power up toggle (Ball speeds up).", 70, 220);
					g.drawString("1~5: Level Selection.", 70, 250);
					g.drawString("Press C to go back.", 70, 280);
				}
				else {
					String level = "Current level: " + ((dropCountRate * 4 / screenHeight) - 1);
					if (gameStarted) { //if the game already started, just say pause
						g.drawString("Paused", 70, 70);
						g.drawString("Press shift to continue.", 70, 100);
					} else {
						g.drawString("Brick Shooter", 70, 70);
						g.drawString("Press shift to begin.", 70, 100);
					}
					g.drawString("Press C for controls.", 70, 130);
					g.drawString(level, 70, 160);
				}
			}
		} else {
			//temp gg screen
			g.setColor(Color.WHITE);
			if (bricksOver) g.fillRect(0, 0, screenWidth, screenHeight);
			else if (brickFellDown) g.drawImage(backgroundImageLoss, 0, 0, screenWidth, screenHeight, this);
			g.setColor(Color.BLUE);
			g.drawString(status, 70, 100);
			g.drawString("Press enter to play again.", 70, 130);
		} 
	}
	public void run() {
		createBricks();
		while (true) { //this loops still happens when the game is pause
			while (!pause) { //in game
				//bricks falling
				dropCount += dropCountRate;
				if (dropCount >= 1200) {
					dropCount = 0;
					for (int i = 0; i < Bricks.length; i++) {
						if (Bricks[i] != null) {
							Bricks[i].y++;
						}
					}
				}
				
				// bullet movement
				for(int i = 0; i < Bullet.length; i++) {//check each bullet
					if (Bullet[i] != null) { // if bullet exists
						if (poweredUp) Bullet[i].y -= screenHeight / 100; // ball goes 2 times faster
						else Bullet[i].y -= screenHeight/200;
						if (Bullet[i].y <= 0) Bullet[i] = null;
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
				if (Gun.x >= screenWidth * 9 / 10) Gun.x = screenWidth * 9 / 10;
				
				//win or lose
				for (int i = 0; i < Bricks.length; i++) {//check each brick
					for(int j = 0; j < Bullet.length; j++) {//check each bullet
						if (Bullet[j] != null && Bricks[i] != null) { // if bullet & brick exists
							if (Bricks[i].intersects(Bullet[j])) {
								Bricks[i].lives--;
								Bullet[j] = null;	
								if (Bricks[i].lives == 0) {
									Bricks[i] = null;
									count++;
								}
							}
						}						
					}
					if (Bricks[i] != null) {
						if (Bricks[i].y + Bricks[i].height >= screenHeight) { // loss
							brickFellDown = true;
							status = "u suck";
							repaint();
						}
					}
				}
				
				// win
				if (count == Bricks.length) {
					bricksOver = true;
					status = "nice";
					repaint();
				}
				
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
	public void createBricks(){
		for (int i = 0; i < Bricks.length; i++) {
			Bricks[i] = new Brick(i % 15 * brickWidth , i / 15  * brickHeight, brickHeight, brickWidth, 3 - (i / 15));
			brickX += brickWidth;
			if (i % 15 == 14) brickX = 0;
			if (Bricks[i].lives < 1) Bricks[i].lives = 1;
		}
	}
	public void restart() {
		requestFocus(true);
		initializeVariables();
		createBricks();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!pause) { //These KeyEvents only happen if the game isn't paused
			if (keyCode == KeyEvent.VK_LEFT) left = true;
			if (keyCode == KeyEvent.VK_RIGHT) right = true;
			if ((keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE)) {
				if (Bullet[(shotCount) % Bullet.length] == null) {
					Bullet[(shotCount) % Bullet.length] = new Rectangle(Gun.x + (Gun.width / 2), screenHeight - Gun.height, bulletSize, bulletSize);
					shotCount++;
				}
			}
		}
		if (keyCode == KeyEvent.VK_1) dropCountRate = screenHeight / 2;
		if (keyCode == KeyEvent.VK_2) dropCountRate = screenHeight * 3 / 4;
		if (keyCode == KeyEvent.VK_3) dropCountRate = screenHeight;
		if (keyCode == KeyEvent.VK_4) dropCountRate = screenHeight * 5 / 4;
		if (keyCode == KeyEvent.VK_5) dropCountRate = screenHeight * 3 / 2;
		if (keyCode == KeyEvent.VK_ENTER && gameStarted) this.restart();
		if (keyCode == KeyEvent.VK_P) {
			if (poweredUp) poweredUp = false;
			else poweredUp = true;
		}
		if (keyCode == KeyEvent.VK_SHIFT && !controls && !bricksOver && !brickFellDown) {
			if (pause) pause = false;
			else pause = true;
			gameStarted = true;
		}
		if (keyCode == KeyEvent.VK_C) {
			if (pause) {
				if (controls) controls = false;
				else controls = true;
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) left = false;
		if (keyCode == KeyEvent.VK_RIGHT) right = false;
	}
}
