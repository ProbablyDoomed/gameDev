package gameDev.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class inputHandler implements KeyListener, MouseListener, MouseMotionListener{
	
	public int mX=0,mY=0;
	
	public inputHandler(Game game){
		game.addKeyListener(this);
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
	}
	
	public class Key{
		
		private boolean pressed = false;
		private int numTimesPressed = 0;
		
		public boolean isPressed(){
			return pressed;
		}
		
		public int getNumTimesPressed(){
			return numTimesPressed;
		}
		
		public void toggle(boolean isPressed){
			pressed = isPressed;
			if (pressed) numTimesPressed++;
		}
	}

	public List<Key> keys = new ArrayList<Key>();

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key fire = new Key();
	public Key strafe = new Key();
	public Key use = new Key();
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {

		
	}
	
	public void toggleKey(int keyCode, boolean isPressed){
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) up.toggle(isPressed);
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) down.toggle(isPressed);
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) left.toggle(isPressed);
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) right.toggle(isPressed);
		if (keyCode == KeyEvent.VK_C ) strafe.toggle(isPressed);
		if (keyCode == KeyEvent.VK_Z ) fire.toggle(isPressed);
		if (keyCode == KeyEvent.VK_SPACE ) use.toggle(isPressed);
	}


	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseEntered(MouseEvent e) {
		updateMouseXY(e);
	}


	public void mouseExited(MouseEvent e) {
		updateMouseXY(e);
	}


	public void mousePressed(MouseEvent e) {
		updateMouseXY(e);
		//fire.toggle(true);
	}

	public void mouseReleased(MouseEvent e) {
		updateMouseXY(e);
		//fire.toggle(false);
	}
	
	private void updateMouseXY(MouseEvent e) {
		mX = e.getX()/Game.SCALE;
		mY = e.getY()/Game.SCALE;
	}


	public void mouseDragged(MouseEvent e) {
		updateMouseXY(e);
	}

	public void mouseMoved(MouseEvent e) {
		updateMouseXY(e);
	}

}
