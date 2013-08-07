package gameDev.game;

import gameDev.graphics.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 480;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 2;
	public static final String NAME = "Test Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private boolean shooting = false;
	
	//private Screen screen;
	public inputHandler input;
	public player dude;
	public projectile laser;
	Image lasersprite,crosshair,shootsprite;
	
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
		dude = new player(100,100,playerSprites.getSprite(0,0,false,false));
		lasersprite = playerSprites.getSprite(1, 1, false, false);
		crosshair = playerSprites.getSprite(0, 1, false, false);
		shootsprite = playerSprites.getSprite(1,0,false,false);
	}
	
	private synchronized void start() {
		running = true;
		new Thread(this).start();	
	}
	
	@SuppressWarnings("unused")
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
				//System.out.println(""+);
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
		
		if(input.fire.isPressed() && laser == null){
			
			double relativeAngle;
			
			double diffY = (input.mY - dude.y) , diffX = (input.mX - dude.x);
			
			if(diffX == 0){
				if(diffY >= 0){
					relativeAngle = Math.PI/2;
				}
				else{
					relativeAngle = 3*Math.PI/2;
				}
			}
			else{
				relativeAngle = Math.atan( diffY/diffX );
				if(diffX < 0) relativeAngle += (Math.PI);
			}
			
			laser = new projectile(dude.x,dude.y, relativeAngle , 10 ,lasersprite);
			shooting = true;
		}
		
		if(laser!=null){
			laser.tickMovement();
			if (laser.x < 0 || laser.y < 0 ||  laser.x >= WIDTH || laser.y >= HEIGHT){
				laser = null;
				shooting = false;
			}
		}
		
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if( bs == null){
			createBufferStrategy(3);
			return;
		}
		
		
		Graphics r = image.createGraphics();
		
		r.setColor(Color.WHITE);
		r.fillRect(0, 0, getWidth(), getHeight());
		
		if(shooting){
			r.drawImage(shootsprite, (int)dude.x - 16, (int)dude.y - 16, 32, 32, null);
		}
		else{
			r.drawImage(dude.sprite, (int)dude.x - 16, (int)dude.y - 16, 32, 32, null);
		}
		r.drawImage(crosshair, input.mX - 16, input.mY - 16, 32, 32, null);
		
		if(laser != null) r.drawImage(laser.sprite, (int)laser.x - 16, (int)laser.y - 16, 32, 32, null);
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		//r.dispose();
		bs.show();
	}
	
	public static void main(String[] args){
		new Game().start();
		
	}

}
