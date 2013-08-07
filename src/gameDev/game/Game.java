package gameDev.game;

import gameDev.graphics.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 480;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 2;
	public static final String NAME = "Test Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	//private Screen screen;
	public inputHandler input;
	public player dude;
	
	public Game(){
		setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void init(){
			
		//screen = new Screen(WIDTH,HEIGHT, new SpriteSheet("/sprite1.png",32));
		SpriteSheet playerSprites = new SpriteSheet("/spriteArturas.png",32);
		input = new inputHandler(this);
		dude = new player(100,100,playerSprites.getSprite(0,0));
	}
	
	private synchronized void start() {
		running = true;
		new Thread(this).start();	
	}
	
	private synchronized void stop() {
		
	}
	
	public void run() {
		
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		int ticks = 0;
		int frames = 60;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while(running){
		
			long now = System.nanoTime();
			delta += (now - lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while (delta >= 1){
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (shouldRender) {
				frames++;
				render();
			}
			
			if ((System.currentTimeMillis() - lastTimer) >= 1000){
				lastTimer += 1000;
				System.out.println("FramesPerSec: "+frames+"     TicksPerSec: "+ticks);
				//System.out.println("X:"+dude.x+" Y:"+dude.y);
				frames = 0;
				ticks= 0;
			}
			
		}
	}
	
	public void tick() {
		tickCount++;
		
		if(input.up.isPressed()) dude.accel(0, -3);
		else if(input.down.isPressed()) dude.accel(0, 3);
		else dude.applyFriction(1, 0.33);
		
		if(input.left.isPressed()) dude.accel(-3, 0);
		else if(input.right.isPressed()) dude.accel(3, 0);
		else dude.applyFriction(0.33, 1);
		
		dude.tickMovement();
		
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if( bs == null){
			createBufferStrategy(3);
			return;
		}
		
		
		/*for (int y=0; y<32; y++){
			for (int x=0; x<32; x++){
				screen.render(x<<3,y<<3, 0, Colours.get(555, 500, 050, 005));
			}
		}
		
		for (int y=0; y<screen.height; y++){		
			for (int x=0; x<screen.width; x++){
				int colourCode = screen.pixels[x+y*screen.width];
				if(colourCode < 255) pixels[x + y * WIDTH] = colours[colourCode];
			}
		}	*/
		
		//image = screen.render();
		
		Graphics r = image.createGraphics();
		
		r.setColor(Color.WHITE);
		r.fillRect(0, 0, getWidth(), getHeight());
		
		r.drawImage(dude.sprite, (int)dude.x - 10, (int)dude.y - 10, 20, 20, null);
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		//r.dispose();
		bs.show();
	}
	
	public static void main(String[] args){
		new Game().start();
		
	}

}
