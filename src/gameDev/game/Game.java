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

	public static final int WIDTH = 480;
	public static final int HEIGHT = WIDTH / 16 * 10;
	public static final int SCALE = 1;
	
	public static final double FOV = 20*Math.PI/180;
	
	public static final String NAME = "Test Game";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	public int tps = 0, fps = 0;
	public static boolean trippinballs = false;
	public static final boolean mouselook = false;
	
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
	
	
	Image gunsprite_idle,gunsprite_shoot,lasersprite,fireball,shootsprite,logo;
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
		SpriteSheet gunsheet = new SpriteSheet("/nerfgun_2frame_test.png",128);
		input = new inputHandler(this);
		dude = new player(250,250,0);
		lasersprite = playerSprites.sprite[2][1];
		fireball = playerSprites.sprite[1][1];
		shootsprite = playerSprites.sprite[1][0];
		logo = logoSheet.sprite[0][0];
		gunsprite_idle = gunsheet.sprite[0][0];
		gunsprite_shoot = gunsheet.sprite[1][0];
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
				//System.out.println("FramesPerSec: "+frames+"     TicksPerSec: "+ticks);
				//System.out.println(""+);
				fps = frames;
				tps = ticks;
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
		
		if(input.strafeleft.isPressed()) dude.accel(dude.heading - Math.PI/2);
		else if(input.straferight.isPressed()) dude.accel(dude.heading + Math.PI/2);
		
		if(mouselook){
			dude.turn((double)input.mdX / 4);
			input.mdX = 0;
		}


		
		dude.tickMovement( world );
		dude.applyFriction();
		//lasers.trimToSize();
		if(input.fire.isPressed()){ 			
			
			if(lasers.size() < 16){
				shooting = true;
				lasers.add(new projectile(dude.x,dude.y, 
						dude.heading + (-0.005 + Math.random()*0.01) , 16 + Math.random()*8 ,lasersprite));
			}
			else shooting = false;
			
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
				else{
					for(int w = 0; w < world.walls.size(); w++){
						if( world.walls.get(w).testIntersection(lasers.get(i).x, lasers.get(i).y, lasers.get(i).speed) ){
							//draw3dList.add(new sprite3d(lasers.get(i).x-dude.x,lasers.get(i).y-dude.y,dude.heading,false,fireball));
							lasers.remove(i);
							break;
						}
					}
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
			
			int wallTexture = 0;
			int wallTextureSegment = 0;
			boolean foundwall = false;
			
			double angle = dude.heading - Game.FOV + (ray * 2 * Game.FOV/Game.WIDTH);	
				
			double m2 = Math.tan(angle); //y = m*x + c for ray 
			double c2 = dude.y - (m2*dude.x);
			double intersect[] = new double[2];
			
			double lowdist = maxDrawDist;
			double xWall = 0,yWall = 0;
			int closestwall = -1;
			
			for(int w = 0; w < world.walls.size(); w++){ //for each wall in level
				
				intersect = world.walls.get(w).getIntersectPoint(m2,c2);
				
				double anglediff = Math.atan((intersect[1]-dude.y)/(intersect[0]-dude.x)) - dude.heading;
				
				if(intersect[0] < dude.x) anglediff += (Math.PI);
				if (anglediff >= Math.PI) anglediff -= 2*Math.PI;
				if (anglediff <= -Math.PI) anglediff += 2*Math.PI;
				
				if(anglediff > -Math.PI/2 && anglediff < Math.PI/2){
					if( world.walls.get(w).testIntersection(intersect[0], intersect[1], wallMargin) ){
						
						foundwall = true;
						
						double dist = Math.sqrt( ((intersect[0]-dude.x)*(intersect[0]-dude.x)) 
								+ ((intersect[1]-dude.y)*(intersect[1]-dude.y)) );
						
						if(dist < lowdist) {
							lowdist = dist;
							closestwall = w;
							xWall = intersect[0];
							yWall = intersect[1];
						}
						
						//break;
						
					}
				}

				
			}
			
			if(foundwall){
				wallTexture = world.walls.get(closestwall).texture;
				wallTextureSegment = world.walls.get(closestwall).getTextureColumn(xWall, yWall);
				
				draw3dList.add(new sprite3d(ray, lowdist, dude.heading, true, 
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
		Image gunsprite = null;
		if(shooting){
			gunsprite = gunsprite_shoot;
		}
		else{
			gunsprite = gunsprite_idle;
		}
		r.drawImage(gunsprite, WIDTH/2 - WIDTH/6, HEIGHT - WIDTH/4, WIDTH/3, WIDTH/3, null);
		r.setColor(Color.WHITE);
		//r.drawString("[Z]fire      [C]strafe      [Space]use            [Arrows]move",8, HEIGHT - 8);
		r.drawString("Framerate:"+fps+"   Tickrate:"+tps,8, HEIGHT - 8);
		
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
