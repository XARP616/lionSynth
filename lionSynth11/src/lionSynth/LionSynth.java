package lionSynth;

import edu.princeton.cs.introcs.StdAudio;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class LionSynth implements Serializable {
		
	/**
	 * 1st version (using Serializable)
	 * o1, o2, o3, o4, mainEnvelope, oscillatorMixer1 & 2, mainMixer, mainFilter
	 * 
	 */
	private static final long serialVersionUID = 2L;
		// COMPONENTS
		Oscillator o1;
		Oscillator o2;
		Oscillator o3;
		Oscillator o4;
		
		Envelope mainEnvelope;
		Mixer oscillatorMixer1;
		Mixer oscillatorMixer2;
		Mixer mainMixer;
		Filter mainFilter;
		
		boolean isPlaying = false;
	 
	
		protected static double sampleDuration = 1.0; // measured in seconds
		protected static double[] outputSample;
		
		
	public void process() {
		Thread worker = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//double sampleDuration = 4.5;
				double[] os1 = o1.genSample(sampleDuration);
				double[] os2 = o2.genSample(sampleDuration);
				double[] os3 = o3.genSample(sampleDuration);
				double[] os4 = o4.genSample(sampleDuration);
				
				double[] om1 = oscillatorMixer1.process(os1, os2);
				double[] om2 = oscillatorMixer2.process(os3, os4);
				
				//OUTPUT
				double[] s3 = mainMixer.process(om1, om2);
				
				mainEnvelope.process(s3);
				
				outputSample = s3;
				LionSynth.playTheSample(); 
				
			}
		});
		worker.start();
			
	
	}
	
	public void processStream() {
		Thread workerOscillator = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(isPlaying) {
					o1.genStreamSignal();
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		
		Thread outputManager = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				double[] outputSample = new double[10000];
				int i = 0;
				while(isPlaying) {
					// if the oscillator is not empty, it will keep on taking samples
					if ( !o1.streamSignal.isEmpty() ) {
						outputSample[i] = o1.streamSignal.removeFirst();
						i++;
						if (i == 10000) {
							LionSynth.playSample(outputSample);
							System.out.println("play");
							i = 0;
						}
					}
					
				}
			}
		});
		
		workerOscillator.start();
		outputManager.start();
		
		
		
	}
	
	// METHODS
	public synchronized static void playSample(double[] sample) {
		StdAudio.play(sample);
	}
	
	public synchronized static void playTheSample() {
		if (outputSample== null) {
			return;
		} else {
			StdAudio.play(outputSample);
		}
		
	}
	
	public void loadPreset(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	
		LionSynth l = new LionSynth((LionSynth) ois.readObject());
		this.setO1(l.o1);
		this.setO2(l.o2);
		this.setO3(l.o3);
		this.setO4(l.o4);
		this.setMainEnvelope(l.mainEnvelope);
		this.setOscillatorMixer1(l.oscillatorMixer1);
		this.setOscillatorMixer2(l.oscillatorMixer2);
		this.setMainMixer(l.mainMixer);
		this.setMainFilter(l.mainFilter);
		System.out.println(this.o1);
		ois.close();
	}
	
	
	// CONSTRUCTOR
	public LionSynth(Oscillator o1, Oscillator o2, Oscillator o3, Oscillator o4, Envelope mainEnvelope,
			Mixer oscillatorMixer1, Mixer oscillatorMixer2, Mixer mainMixer, Filter mainFilter) {
		
		// every component will be a shallow copy
		super();
		this.o1 = o1;
		this.o2 = o2;
		this.o3 = o3;
		this.o4 = o4;
		this.mainEnvelope = mainEnvelope;
		this.oscillatorMixer1 = oscillatorMixer1;
		this.oscillatorMixer2 = oscillatorMixer2;
		this.mainMixer = mainMixer;
		this.mainFilter = mainFilter;
	}
	public LionSynth(LionSynth l) {

		super();
		this.o1 = new Oscillator(l.o1);
		this.o2 = new Oscillator(l.o2);
		this.o3 = new Oscillator(l.o3);
		this.o4 = new Oscillator(l.o4);
		this.mainEnvelope = new Envelope(l.mainEnvelope);
		this.oscillatorMixer1 = new Mixer(l.oscillatorMixer1);
		this.oscillatorMixer2 = new Mixer(l.oscillatorMixer2);
		this.mainMixer = new Mixer(l.mainMixer);
		this.mainFilter = new Filter(l.mainFilter);
	}

	public LionSynth() {

		// every component will be a shallow copy
		super();
		this.o1 = new Oscillator();
		this.o2 = new Oscillator();
		this.o3 = new Oscillator();
		this.o4 = new Oscillator();
		this.mainEnvelope = new Envelope();
		this.oscillatorMixer1 = new Mixer();
		this.oscillatorMixer2 = new Mixer();
		this.mainMixer = new Mixer();
		this.mainFilter = new Filter();
	}

	public Oscillator getO1() {
		return o1;
	}

	public void setO1(Oscillator o1) {
		this.o1 = new Oscillator(o1);
	}

	public Oscillator getO2() {
		return o2;
	}

	public void setO2(Oscillator o2) {
		this.o2 = new Oscillator(o2);
	}

	public Oscillator getO3() {
		return o3;
	}

	public void setO3(Oscillator o3) {
		this.o3 = new Oscillator(o3);
	}

	public Oscillator getO4() {
		return o4;
	}

	public void setO4(Oscillator o4) {
		this.o4 = new Oscillator(o4);
	}

	public Envelope getMainEnvelope() {
		return mainEnvelope;
	}

	public void setMainEnvelope(Envelope mainEnvelope) {
		this.mainEnvelope = new Envelope(mainEnvelope);
	}

	public Mixer getOscillatorMixer1() {
		return oscillatorMixer1;
	}

	public void setOscillatorMixer1(Mixer oscillatorMixer1) {
		this.oscillatorMixer1 = new Mixer(oscillatorMixer1);
	}

	public Mixer getOscillatorMixer2() {
		return oscillatorMixer2;
	}

	public void setOscillatorMixer2(Mixer oscillatorMixer2) {
		this.oscillatorMixer2 = new Mixer(oscillatorMixer2);
	}

	public Mixer getMainMixer() {
		return mainMixer;
	}

	public void setMainMixer(Mixer mainMixer) {
		this.mainMixer = new Mixer(mainMixer);
	}

	public Filter getMainFilter() {
		return mainFilter;
	}

	public void setMainFilter(Filter mainFilter) {
		this.mainFilter = new Filter(mainFilter);
	}

	public static double[] getOutputSample() {
		return outputSample;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean b) {
		this.isPlaying = b;
	}
	
	public void setSampleDuration(double d) {
		LionSynth.sampleDuration = d;
	}
	
	public double getSampleDuration() {
		return LionSynth.sampleDuration;
	}

}
