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
		
		width = 800; height = 800;
		walls.add(new wall(0,0,width,0,  0));
		walls.add(new wall(0,0,0,height,  0));
		walls.add(new wall(width,0,width,height,  0));
		walls.add(new wall(0,height,width,height,  0));
		
		walls.add(new wall(50,50,50,75,1));
		walls.add(new wall(50,50,75,50,1));
		walls.add(new wall(75,50,50,75,1));
		
		walls.add(new hingedoor(250,799,250,750,Math.PI/2,2));
		
		walls.add(new wall(800,600,600,800,3));

		
	}
}
