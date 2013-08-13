package gameDev.game;

import java.util.ArrayList;
import java.util.List;

public class level {
	
	public List<wall> walls = new ArrayList<wall>();
	public int width,height;
	
	public level( int levelNum ){
		buildLevel(levelNum);
	}
	
	private void buildLevel( int levelNum ){
		
		width = 1600; height = 800;
		walls.add(new wall(0,0,width,0,  0));
		walls.add(new wall(0,0,0,height,  0));
		walls.add(new wall(width,0,width,height,  0));
		walls.add(new wall(0,height,width,height,  0));
		
		walls.add(new wall(800,0,800,400-32,0));
		walls.add(new hingedoor(801,400-32,801,400,-Math.PI*0.8,  4));
		walls.add(new hingedoor(801,400+32,801,400,Math.PI*0.8,  4));
		walls.add(new wall(800,800,800,400+32,0));
		
		walls.add(new wall(50,50,50,75,1));
		walls.add(new wall(50,50,75,50,1));
		walls.add(new wall(75,50,50,75,1));
		
		//walls.add(new slidedoor(1200,400,1200,800,400,  2));
		
		walls.add(new wall(800,700,700,800,3));

		
	}
}
