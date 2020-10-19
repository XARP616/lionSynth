package lionSynth;

import java.io.Serializable;

public class Mixer implements Serializable {
	/**
	 * 1st version
	 *  input1Gain, input2Gain, masterGain, attenuationRatio
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double input1Gain; // values from 0 to 1 (IllegalArgumentException)
	private double input2Gain;
	private double masterGain;
	private double attenuationRatio;
	
	public Mixer(double input1Gain, double input2Gain, double masterGain, double attenuationRatio) {
		super();
		this.input1Gain = input1Gain;
		this.input2Gain = input2Gain;
		this.masterGain = masterGain;
		this.attenuationRatio = attenuationRatio;
	}
	
	public Mixer() {
		super();
		this.input1Gain = 0;
		this.input2Gain = 0;
		this.masterGain = 1;
		this.attenuationRatio = 1.1;
	}
	
	public Mixer(Mixer m) {
		super();
		this.input1Gain = m.input1Gain;
		this.input2Gain = m.input2Gain;
		this.masterGain = m.masterGain;
		this.attenuationRatio = m.attenuationRatio;
	}
	
	
	public double getInput1Gain() {
		return input1Gain;
	}

	public void setInput1Gain(double input1Gain) {
		this.input1Gain = input1Gain;
	}

	public double getInput2Gain() {
		return input2Gain;
	}

	public void setInput2Gain(double input2Gain) {
		this.input2Gain = input2Gain;
	}

	public double getMasterGain() {
		return masterGain;
	}

	public void setMasterGain(double masterGain) {
		this.masterGain = masterGain;
	}
	
	public double getAttenuationRatio() {
		return attenuationRatio;
	}

	public void setAttenuationRatio(double attenuationRatio) {
		this.attenuationRatio = attenuationRatio;
	}
	
	public void cloneFrom(Mixer m) {
		this.attenuationRatio = m.attenuationRatio;
		this.input1Gain = m.input1Gain;
		this.input2Gain = m.input2Gain;
		this.masterGain = m.masterGain;
	}
	
	
	@Override
	public String toString() {
		return "Mixer [Input 1: " + input1Gain + ", Input 2: " + input2Gain + ", Output: " + masterGain
				+ ", Attenuation: " + attenuationRatio + "]";
	}

	public double[] process(double[] input1Signal, double[] input2Signal) {
		int n = input1Signal.length;
		
		// input1 and 2 length must be equal
		if (n != input2Signal.length) {
			System.out.println("[Mixer] Warning: size of the samples mismatch");
			return input1Signal;
		}
		
		double[] outputSignal = new double[n];
		double attenuation1 = Function.attenuator(input1Gain, attenuationRatio); // THIS SMOOTHES OUT THE ATTENUATION; LINEAR -> EXPONENTIAL (MORE REALISTIC FEEL)
		double attenuation2 = Function.attenuator(input2Gain, attenuationRatio);
		double masterAttenuation = Function.attenuator(masterGain, attenuationRatio);
		
		// ADITTION AND ATTENUATION OF THE FIRST SIGNAL
		for (int i = 0; i < n; i++) {
			//outputSignal[i] = input1Signal[i] * input1Gain;
			outputSignal[i] = input1Signal[i] * attenuation1;
		}
		
		// ADITTION AND ATTENUATION OF THE SECOND SIGNAL
		for (int i = 0; i < n; i++) {
			//outputSignal[i] += input2Signal[i] * input2Gain;
			outputSignal[i] += input2Signal[i] * attenuation2;
		}
		
		// OUTPUT ATTENUATION ( ATTENUATION OF THE SUM OF BOTH SIGNALS)
		for (int i = 0; i < n; i++) {
			outputSignal[i] *= masterAttenuation;
		}
		
		return outputSignal;
	}
	
}
