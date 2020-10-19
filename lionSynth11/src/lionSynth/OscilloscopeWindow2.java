package lionSynth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class OscilloscopeWindow2 extends JFrame {
	
	final int SCREEN_SIZE = 800;
	final int WEST_PANEL_SIZE_X = 200;
	JButton bgenerate;
	JPanel westMainPanel;
	
	public OscilloscopeWindow2() {
		westMainPanel = new JPanel(new GridLayout(8, 1));
		westMainPanel.setSize(WEST_PANEL_SIZE_X, SCREEN_SIZE);
		westMainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Option panel", TitledBorder.CENTER, TitledBorder.TRAILING));
		westMainPanel.setBackground(new Color(100, 100, 100, 30));
		
		JPanel oscilloscope = new Oscilloscope();
		oscilloscope.setSize(SCREEN_SIZE, SCREEN_SIZE);
		bgenerate = new JButton("Reload wave");
		
		bgenerate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				oscilloscope.repaint(); } } );
		
		
		westMainPanel.add(bgenerate);
		
		add(westMainPanel, BorderLayout.WEST);
		add(oscilloscope, BorderLayout.CENTER);
		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Oscilloscope");
		setSize(SCREEN_SIZE + WEST_PANEL_SIZE_X, SCREEN_SIZE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	
	class Oscilloscope extends JPanel {
		double[] array;
		
		public void paintComponent(Graphics g) {	
			refreshArray();
			super.paintComponent(g);
			
			if (array == null) { // if the array is empty, the output sample doesn't exist by now, so we won't draw anything
				System.out.println("LionSynth's outputSignal is empty.");
				JOptionPane.showMessageDialog(null, "Signal could not be drawn because there is none generated.", "Signal Missing!", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int verticalAlign = 400; // we will align the waveform to the center of the screen
			int x = 1;
			
			// we look for the loudest value in the sample part that is going to be shown to stretch the waveform so it stretches to the window size
			double loudest = 0;
			for (int i = 0; i < SCREEN_SIZE; i++) {
				loudest = Math.max(Math.abs(array[i]), loudest);
			}
			double stretchValue = (SCREEN_SIZE / 2) / loudest;
			// 
			
			do {
				// a line is drawn from the previous sample to the next. Values from the array are increased to fit better the screen. 
				// the value is inverted at Y position because otherwise the wave would be drawn upside down
				// (because positions are relative to the upper right corner of the screen)
				double firstValue =  array[x-1];
				double secondValue = array[x];
				
				g.drawLine(x, -(int) (firstValue* stretchValue) + verticalAlign, x, -(int) (secondValue * stretchValue)+ verticalAlign);
					
				x++;
			} while (x < SCREEN_SIZE);
			
			// a red line that cuts the waveform in a half
			g.setColor(Color.RED);
			g.drawLine(0, verticalAlign, SCREEN_SIZE + WEST_PANEL_SIZE_X, verticalAlign);
			g.setColor(Color.BLACK);
		}
		
		public void refreshArray(){
			array = LionSynth.outputSample;
		}

	}
}

