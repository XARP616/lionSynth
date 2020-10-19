package lionSynth;

import java.io.Serializable;
import java.util.LinkedList;

import edu.princeton.cs.introcs.StdAudio;

public class Oscillator implements Serializable {
	/**
	 * 1st version
	 * octave, ntoe, fine, waveshaper, detune, volume
	 * 
	 * 2nd version
	 * added: hz
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private int octave;
	private int note;
	private double fine;
	private WaveShape waveform;
	private double detune;
	private double volume;
	static private double hz = MusicalNote.DO.getHz();
	public LinkedList<Double> streamSignal = new LinkedList<Double>(); // IRT, stream signal
	
	
	public Oscillator(int octave, int note, double fine, WaveShape waveform, double detune, double volume) {
		super();
		this.octave = octave;
		this.note = note;
		this.fine = fine;
		this.waveform = waveform;
		this.detune = detune;
		this.volume = volume;
	}
	
	public Oscillator() {
		super();
		this.octave = 0;
		this.note = 0;
		this.fine = 0;
		this.waveform = WaveShape.SAW;
		this.detune = 0;
		this.volume = 1;
	}
	
	public Oscillator(Oscillator o) {
		super();
		this.octave = o.octave;
		this.note = o.note;
		this.fine = o.fine;
		this.waveform = o.waveform;
		this.detune = o.detune;
		this.volume = o.volume;
	}

	public int getOctave() {
		return octave;
	}

	public void setOctave(int octave) {
		this.octave = octave;
	}

	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		this.note = note;
	}

	public double getFine() {
		return fine;
	}

	public void setFine(double fine) {
		this.fine = fine;
	}

	public WaveShape getWaveShape() {
		return waveform;
	}

	public void setWaveShape(WaveShape waveShape) {
		this.waveform = waveShape;
	}

	public double getDetune() {
		return detune;
	}

	public void setDetune(double detune) {
		this.detune = detune;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getHz() {
		return hz;
	}
	
	public static void setHz(double hz) {
		Oscillator.hz = hz;
	}

	public void cloneFrom(Oscillator o) {
		this.detune = o.detune;
		this.fine = o.fine;
		this.note = o.note;
		this.octave = o.octave;
		this.volume = o.volume;
		this.waveform = o.waveform;
	}
	
	

	@Override
	public String toString() {
		return "Oscillator [Octave: " + octave + ", Note: " + note + ", Fine: " + fine + ", Wave: " + waveform
				+ ", Detune: " + detune + ", Volume: " + volume + "]";
	}

	// kind of the main of this class. Preferably, you should be using this one.
	public double[] genSample(double duration) { // WILL GENERATE A SAMPLE WITH THE CURRENT CONFIGURATION OF THE OSCILLATOR
		double[] outputSample;


		switch (this.waveform) {
		case SAW:
			outputSample = this.genSawSample(duration);
			break;
		case TRIANGLE:
			outputSample = this.genTriangleSample(duration);
			break;
		case SQUARE:
			outputSample = this.genSquareSample(duration);
			break;
		case PULSE:
			outputSample = this.genPulseSample(duration);
			break;
		case SINE:
			outputSample = this.genSineSample(duration);
			break;
		default:
			// Be warned: this line should never pop up.
			System.out.println("Something went really worng. waveform not selected?");
			outputSample = null;
			break;
		}

		return outputSample;
	}
	
	public synchronized void genStreamSignal() {
		int SAMPLE_RATE = 44100;
		short i = 0;
		double finalHz;
		while(streamSignal.size() < SAMPLE_RATE) {
			finalHz = Oscillator.hz * Math.pow(2, this.octave);
			switch (this.waveform) {
			case SINE:
				this.streamSignal.add(Math.sin(Math.PI * i * finalHz));
				break;
			case SAW:
				this.streamSignal.add(Function.saw(Math.PI * i * finalHz));
				break;
			case SQUARE:
				this.streamSignal.add((double) Function.square(Math.PI * i * finalHz));
				break;
			case PULSE:
				this.streamSignal.add(Function.pulse(Math.PI * i * finalHz));
				break;
			case TRIANGLE:
				this.streamSignal.add(Function.triangle(Math.PI * i * finalHz));
				break;
			default:
				this.streamSignal.add(0.0);
				System.err.println("No waveshape selected???");
				System.out.println(this.waveform + "\n");
				break;
			}
			i++;
			i = (short) Math.max(i, 0);
		}
		System.out.println("Stream Signal full");
	}

	
	
	
	
	public double[] genTriangleSample(double duration) {
		int SAMPLE_RATE = 44100;  
		int n = (int) (SAMPLE_RATE * duration); 
		double[] r = new double[n+1];

		// frequency taking into  account the value of the octave
		double finalHz = Oscillator.hz * Math.pow(2, this.octave);
		//System.out.println("Hz: "+ hz + ", Octave: " + this.octave + ", Result: " + finalHz);
		
		for (int i = 0; i <= n; i++) {
			
			r[i] = Function.triangle(Math.PI * i * finalHz / SAMPLE_RATE); 
		}
		
		return r;
	}
	
	public double[] genSawSample(double duration) {
		int SAMPLE_RATE = 44100;  
		int n = (int) (SAMPLE_RATE * duration); 
		double[] r = new double[n+1];
		
		// frequency taking into  account the value of the octave
		double finalHz = Oscillator.hz * Math.pow(2, this.octave);
		
		for (int i = 0; i <= n; i++) {
			
			r[i] = Function.saw(Math.PI * i * finalHz / SAMPLE_RATE); 
		}
		
		return r;
		
	}
	
	public double[] genSquareSample(double duration) {
		int SAMPLE_RATE = 44100;
		int n = (int) (SAMPLE_RATE * duration);
		double[] r = new double[n+1];

		// frequency taking into  account the value of the octave
		double finalHz = Oscillator.hz * Math.pow(2, this.octave);
		
		for (int i = 0; i <= n; i++) {     
			
			r[i] = Function.square(Math.PI * i * finalHz / SAMPLE_RATE);
		}  
		
		return r;
	}
	
	public double[] genPulseSample(double duration) {
		int SAMPLE_RATE = 44100;
		int n = (int) (SAMPLE_RATE * duration);
		double[] r = new double[n+1];

		
		// frequency taking into  account the value of the octave
		double finalHz = Oscillator.hz * Math.pow(2, this.octave);
		
		for (int i = 0; i <= n; i++) {     
			
			r[i] = Function.pulse(Math.PI * i * finalHz / SAMPLE_RATE);
		}  
		
		return r;
	}
	
	public double[] genSineSample(double duration) {
		int SAMPLE_RATE = 44100;
		int n = (int) (SAMPLE_RATE * duration);
		double[] r = new double[n+1];
		
		// frequency taking into  account the value of the octave
		double finalHz = Oscillator.hz * Math.pow(2, this.octave);
		
		for (int i = 0; i <= n; i++) {     
			r[i] = Math.sin(Math.PI * i * finalHz / SAMPLE_RATE);
		}  

		return r;
	}
	
	public static void playExperiment(double hz, double duration, int octave) {  
		int SAMPLE_RATE = 44100;
		int n = (int) (SAMPLE_RATE * duration); 
		double[] a = new double[n+1]; 
		double[] r = new double[n+1];
		//double detune = 1;
		

		for (int j = (int) hz; j < 22000; j*=2) {
			for (int i = 0; i <= n; i++) {     
				//a[i] = Math.sin(1 * Math.PI * i * hz / (SAMPLE_RATE - 1000*detune));
				//b[i] = Math.tan(1 * Math.PI * i * hz / (SAMPLE_RATE - 2000*detune));
				//c[i] = Math.cos(1 * Math.PI * i * hz / (SAMPLE_RATE - 3000*detune));
				a[i] = Math.sin(octave * Math.PI * i * j / SAMPLE_RATE);
				r[i] += a[i];
			}  
			
		}
		StdAudio.play(r); 		
	
	}
	
	public static void playExperiment2() {
		int SAMPLE_RATE = 44100;
		int n = (int) (SAMPLE_RATE * 1.5); 
		double[] a = new double[n+1]; 
		double[] r = new double[n+1];
		double[] b = new double[n+1];
		int hz = 440;
		
		for (int i = 0; i <= n; i++) {     
			a[i] = Function.square(1 * Math.PI * i * hz / SAMPLE_RATE);
		}
		
		for (int i = 5000; i <= n; i++) {
			b[i] = - Function.square(1 * Math.PI * i * hz / SAMPLE_RATE);
		}
		
		for (int i = 0; i <= n; i++) {
			r[i] = a[i] + b[i];
			if (i < 6000) {
				System.out.println(r[i]);
			}
		}
		
		//StdAudio.play(a);
		
		StdAudio.play(r);
		
		//StdAudio.play(b);
	
	}
}
