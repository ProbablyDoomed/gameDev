package gameDev.game;

import gameDev.graphics.SpriteSheet;
import gameDev.graphics.sprite3d;
import gameDev.graphics.wallTexture;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 360;
	public static final int HEIGHT = WIDTH / 16 * 10;
	public static final int SCALE = 2;
	
	public static final double FOV = 30*Math.PI/180;
	
	public static final String NAME = "Test Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	public List<sprite3d> draw3dList = new ArrayList<sprite3d>();
	public static final double rayStep = 0.2;
	public static final int maxDrawDist = 1000;
	public static final double wallMargin = 0.2;
	
	
	public inputHandler input;
	public player dude;
	
	public level world = new level(0);
	public wallTexture greystone = new wallTexture("/wolfen/greystone.png");
	public wallTexture eagle = new wallTexture("/wolfen/eagle.png");
	public wallTexture pinkgrid = new wallTexture("/sprite1.png");

	public List<projectile> lasers = new ArrayList<projectile>();
	
	
	Image lasersprite,crosshair,shootsprite;
	boolean shooting = false;
	
	
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
			
		SpriteSheet playerSprites = new SpriteSheet("/spriteArturas.png",32);
		input = new inputHandler(this);
		dude = new player(100,100,0);
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
		
		if(input.up.isPressed()) dude.accel(dude.heading);
		else if(input.down.isPressed()) dude.accel(dude.heading + Math.PI);
		
		
		if(input.strafe.isPressed()){
			if(input.left.isPressed()) dude.accel(dude.heading - Math.PI/2);
			else if(input.right.isPressed()) dude.accel(dude.heading + Math.PI/2);
		}
		else{
			if(input.left.isPressed()) dude.turn(-1);
			if(input.right.isPressed()) dude.turn(1);
		}

		
		dude.tickMovement();
		dude.applyFriction();
		//lasers.trimToSize();
		if(input.fire.isPressed() && lasers.size() < 12){
			
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
			
			shooting = true;
			
			lasers.add(new projectile(dude.x,dude.y, relativeAngle , 8 ,lasersprite));
			lasers.add(new projectile(dude.x - 8,dude.y, relativeAngle , 8 ,lasersprite));
		}
		else{
			shooting = false;
		}
		
		for(int i=0; i<lasers.size(); i++){
			if( lasers.get(i) != null ){
				lasers.get(i).tickMovement();
				if (lasers.get(i).x < 0 || lasers.get(i).y < 0 ||  lasers.get(i).x >= world.width || lasers.get(i).y >= world.height){
					lasers.remove(i);
				}
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
		
		r.setColor(Color.GRAY);
		r.fillRect(0, 0, WIDTH, HEIGHT/2);
		r.setColor(Color.darkGray);
		r.fillRect(0, HEIGHT/2, WIDTH, HEIGHT/2);
		
		for(int ray = 0; ray < WIDTH; ray++){
			
			int steps = 0;
			boolean foundWall = false;
			int wallTextureIndex = 0;
			double xRay,yRay;
			
			double angle = dude.heading - Game.FOV + (ray * 2 * Game.FOV/Game.WIDTH);
			double xStep = rayStep * Math.cos(angle);
			double yStep = rayStep * Math.sin(angle);
			
			while( steps*rayStep <= maxDrawDist && foundWall == false){
				
				steps++;
				xRay = dude.x + xStep * steps;
				yRay = dude.y + yStep * steps;
				
				for(int w = 0; w < world.walls.size(); w++){ //for each wall in level
					if( world.walls.get(w).testIntersection(xRay, yRay, wallMargin) ){
						wallTextureIndex = world.walls.get(w).getTextureColumn(xRay, yRay);
						foundWall = true;
						break;
					}
				}
				
			}
			
			if(foundWall){
				draw3dList.add(new sprite3d(ray, steps*rayStep, dude.heading, true, 
						greystone.segment[wallTextureIndex]));
			}
			
		}
		
		for(int i=0; i < draw3dList.size(); i++){
			r.drawImage(draw3dList.get(i).sprite,
					(int)(draw3dList.get(i).screenX - (draw3dList.get(i).drawWidth/2)) ,
					(int)((Game.HEIGHT - draw3dList.get(i).drawHeight) /2 ) ,
					draw3dList.get(i).drawWidth, draw3dList.get(i).drawHeight, null );
			
			/*r.fillRect((int)(draw3dList.get(i).screenX - (draw3dList.get(i).drawWidth/2)) ,
					(int)((Game.HEIGHT - draw3dList.get(i).drawHeight) /2 ) ,
					draw3dList.get(i).drawWidth, draw3dList.get(i).drawHeight);*/
			
		}
		
		draw3dList.clear();
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		r.dispose();
		bs.show();
	}
	
	public static void main(String[] args){
		new Game().start();
		
	}

}
