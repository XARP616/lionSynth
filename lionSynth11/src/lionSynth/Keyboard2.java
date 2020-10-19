package lionSynth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Keyboard2 extends JPanel {
	
	final int KEY_SIZE_X = 15;
	final int KEY_SIZE_Y = 60;
	static final int SCREEN_SIZE_Y = 400;
	static final int SCREEN_SIZE_X = 400;
	
	public Keyboard2() {
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				createAndShowGUI();
				
			}
		});
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 1; i <= 12; i++) {
			if (i == 2 | i == 4 | i == 7 | i == 9 | i == 11 ) {
				g.fillRect((i*KEY_SIZE_X) + 80, (SCREEN_SIZE_Y / 2) - KEY_SIZE_Y, KEY_SIZE_X, KEY_SIZE_Y + 1);
			} else {
				g.drawRect((i*KEY_SIZE_X) + 80, (SCREEN_SIZE_Y / 2) - KEY_SIZE_Y, KEY_SIZE_X, KEY_SIZE_Y);				
			}
		}
	}

	
	
	public static void createAndShowGUI() {
		JFrame frame = new JFrame();
		frame.add( new Keyboard2() );
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
		frame.setVisible(true);
	}
}
