package lionSynth;

import java.io.Serializable;

public class Filter implements Serializable, SingleSignalProcessor {
	/**
	 * 1st version:
	 * cutoff, amount, type
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double cutoff; // CUTOFF FRECUENCY (FILTER THRESHOLD)
	private double amount; // (pick) 12 / 24 DB
	private FilterType type;
	
	
// CONSTRUCTORES
	public Filter(double cutoff, double amount, FilterType type) {
		super();
		this.cutoff = cutoff;
		this.amount = amount;
		this.type = type;
	}
	
	public Filter() {
		super();
		this.cutoff = 22000;
		this.amount = 12;
		this.type = FilterType.HIGH_CUT;
	}
	
	public Filter(Filter f) {
		super();
		this.cutoff = f.cutoff;
		this.amount = f.amount;
		this.type = f.type;
	}


	public double getCutoff() {
		return cutoff;
	}

	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}
	
	public void cloneFrom(Filter f) {
		this.amount = f.amount;
		this.cutoff = f.cutoff;
		this.type = f.type;
	}

	@Override
	public String toString() {
		return "Filter [Cutoff: " + cutoff + ", Amount: " + amount + ", Type: " + type + "]";
	}

	
	
	
	// SUBSTRACTION OF FRECUENCIES
	public double[] process(double[] inputSignal) {
		int SAMPLE_RATE = 44100;  
		int n = (inputSignal.length); 
		//double[] a = new double[n]; 
		
		// esto está mal, muy mal, hay que decomponer la señal en armónicos con fourier y retirar los indeseados.
		for (int j = (int) this.cutoff; j < 22000; j+=10) {
			for (int i = 0; i <= n-1; i++) {
				inputSignal[i] -=  Math.sin(Math.PI * i * j / SAMPLE_RATE);
			}  
			
			
		}
		
		
		return inputSignal;
	}
	
}
