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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 320;
	public static final int HEIGHT = WIDTH / 16 * 10;
	public static final int SCALE = 1;
	
	public static final double FOV = 25*Math.PI/180;
	
	public static final String NAME = "Test Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	public List<sprite3d> draw3dList = new ArrayList<sprite3d>();
	public static final double rayStep = 1;
	public static final int maxDrawDist = 2000;
	public static final double wallMargin = rayStep;
	
	
	public inputHandler input;
	public player dude;
	
	public level world = new level(0);
	public wallTexture[] wallTextures = {
			new wallTexture("/wolfen/redbrick.png"),
			new wallTexture("/wolfen/greystone.png"),
			new wallTexture("/wolfen/wood.png"),
			new wallTexture("/whitebrick.png"),
			new wallTexture("/wooddoor.png")
	};

	public List<projectile> lasers = new ArrayList<projectile>();
	
	
	Image lasersprite,crosshair,shootsprite,logo;
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
	
	public class distComparator implements Comparator<sprite3d>{ //order by farthest away to closest for drawing
		public int compare(sprite3d s1, sprite3d s2) {
			return  ((int)s2.dist - (int)s1.dist);
		}	
	}
	
	public void init(){
			
		SpriteSheet playerSprites = new SpriteSheet("/spriteArturas.png",32);
		SpriteSheet logoSheet = new SpriteSheet("/drinksplusplus.png",64);
		input = new inputHandler(this);
		dude = new player(250,250,0);
		lasersprite = playerSprites.sprite[1][1];
		crosshair = playerSprites.sprite[0][1];
		shootsprite = playerSprites.sprite[1][0];
		logo = logoSheet.sprite[0][0];
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

		
		dude.tickMovement( world );
		dude.applyFriction();
		//lasers.trimToSize();
		if(input.fire.isPressed() && lasers.size() < 4){
						
			shooting = true;
			
			lasers.add(new projectile(dude.x,dude.y, dude.heading , 16 ,lasersprite));
			
		}
		else{
			shooting = false;
		}
		
		for(int w = 0; w < world.walls.size(); w++){
			world.walls.get(w).tickMovement();
			if(input.use.isPressed()){
				world.walls.get(w).toggle();
			}
		}
		input.use.toggle(false);
		
		for(int i=0; i<lasers.size(); i++){
			if( lasers.get(i) != null ){
				lasers.get(i).tickMovement();
				if (lasers.get(i).x < 0 || lasers.get(i).y < 0 
					||  lasers.get(i).x >= world.width || lasers.get(i).y >= world.height){
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
			int wallTexture = 0;
			int wallTextureSegment = 0;
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
						wallTexture = world.walls.get(w).texture;
						wallTextureSegment = world.walls.get(w).getTextureColumn(xRay, yRay);
						foundWall = true; 
						break;
					}
				}
				
			}
			
			if(foundWall){
				draw3dList.add(new sprite3d(ray, steps*rayStep, dude.heading, true, 
						wallTextures[wallTexture].segment[wallTextureSegment]));
			}
			
		}
		
		draw3dList.add(new sprite3d(400-dude.x,400-dude.y,dude.heading,false,shootsprite));
		draw3dList.add(new sprite3d(200-dude.x,450-dude.y,dude.heading,false,logo));
		
		for(int i=0; i<lasers.size(); i++){
			if( lasers.get(i) != null ){
				draw3dList.add(new sprite3d(lasers.get(i).x-dude.x,lasers.get(i).y-dude.y,dude.heading,false,lasersprite));
			}
		}	
		
		Collections.sort(draw3dList , new distComparator() );
		
		for(int i=0; i < draw3dList.size(); i++){
			
			double screenX = draw3dList.get(i).screenX;
			int drawWidth = draw3dList.get(i).drawWidth;
			
			if(screenX >= 0 - drawWidth && screenX < WIDTH + drawWidth){
				if(draw3dList.get(i).isWall || draw3dList.get(i).drawHeight < HEIGHT * 3){
					r.drawImage(draw3dList.get(i).sprite,
							(int)(draw3dList.get(i).screenX - (draw3dList.get(i).drawWidth/2)) ,
							(int)((Game.HEIGHT - draw3dList.get(i).drawHeight) /2 ) ,
							draw3dList.get(i).drawWidth, draw3dList.get(i).drawHeight, null );
				}
			}
									
		}
		
		draw3dList.clear();
		
		//r.drawImage(crosshair, WIDTH/2 - 12, HEIGHT/2 - 12, 24, 24, null);
		r.setColor(Color.WHITE);
		r.drawString("[Z]fire      [C]strafe      [Space]use            [Arrows]move",8, HEIGHT - 8);
		
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
