package lionSynth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Keyboard extends JPanel {
	
	static final int SCREEN_SIZE_Y = 200;
	static final int SCREEN_SIZE_X = 400;
	JButton firstKey;
	JButton thirdKey;
	JButton fifthKey;
	JButton sixthKey;
	JButton eighthKey;
	JButton tenthKey;
	JButton twelfthKey;
	JButton secondKey;
	JButton fourthKey;
	JButton seventhKey;
	JButton ninthKey;
	JButton eleventhKey;
	
	public Keyboard() {
		this.setLayout(new GridLayout(1, 12));
		firstKey = new JButton();
		firstKey.setBackground(Color.white);
		thirdKey = new JButton();
		thirdKey.setBackground(Color.white);
		fifthKey = new JButton();
		fifthKey.setBackground(Color.white);
		sixthKey = new JButton();
		sixthKey.setBackground(Color.white);
		eighthKey = new JButton();
		eighthKey.setBackground(Color.white);
		tenthKey = new JButton();
		tenthKey.setBackground(Color.white);
		twelfthKey = new JButton();
		twelfthKey.setBackground(Color.white);
		secondKey = new JButton();
		secondKey.setBackground(Color.BLACK);
		fourthKey = new JButton();
		fourthKey.setBackground(Color.BLACK);
		seventhKey = new JButton();
		seventhKey.setBackground(Color.BLACK);
		ninthKey = new JButton();
		ninthKey.setBackground(Color.BLACK);
		eleventhKey = new JButton();
		eleventhKey.setBackground(Color.BLACK);
		
		add(firstKey);
		add(secondKey);
		add(thirdKey);
		add(fourthKey);
		add(fifthKey);
		add(sixthKey);
		add(seventhKey);
		add(eighthKey);
		add(ninthKey);
		add(tenthKey);
		add(eleventhKey);
		add(twelfthKey);
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
		setVisible(true);
	}

}
