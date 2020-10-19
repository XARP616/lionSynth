package lionSynth;

import java.io.Serializable;

public class Envelope implements Serializable, SingleSignalProcessor {
	private double attack; // value in seconds
	private double decay;  // value in seconds
	private double sustain; // value in % (between 0 and 1)
	private double release; // value in seconds
	
	public Envelope(double attack, double decay, double sustain, double release) {
		super();
		this.attack = attack;
		this.decay = decay;
		this.sustain = ((sustain >= 0 && sustain <= 1) ? sustain : 1); // if (sustain >= 0 && sustain <= 1) { return sustain; } else { return 1; }
		this.release = release;
	} 
	
	public Envelope() {
		super();
		this.attack = 0;
		this.decay = 0;
		this.sustain = 0;
		this.release = 0;
	} 
	
	public Envelope(Envelope e) {
		super();
		this.attack = e.attack;
		this.decay = e.decay;
		this.sustain = e.sustain;
		this.release = e.release;
	}

	public double getAttack() {
		return attack;
	}

	public void setAttack(double attack) {
		this.attack = attack;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public double getSustain() {
		return sustain;
	}

	public void setSustain(double sustain) {
		this.sustain = ((sustain >= 0 && sustain <= 1) ? sustain : 1);
	}

	public double getRelease() {
		return release;
	}

	public void setRelease(double release) {
		this.release = release;
	}
	
	public void cloneFrom(Envelope e) {
		this.attack = e.attack;
		this.decay = e.decay;
		this.release = e.decay;
		this.sustain = e.sustain;
	}

	@Override
	public String toString() {
		return "Envelope [Attack: " + attack + ", Decay: " + decay + ", Sustain: " + sustain + ", Release: " + release
				+ "]";
	} 
	
	
	public synchronized double[] process(double[] inputSignal) {
		int n = inputSignal.length - 1;
		System.out.println(n);
		int SAMPLE_RATE = 44100;
		System.out.println(this);
		
		// the amount of samples per phase is computed
		int sampleLengthAttack = (int) (this.attack * SAMPLE_RATE); // attack, decay and release will be in seconds
		int sampleLengthDecay = (int) (this.decay * SAMPLE_RATE);
		int sampleLengthRelease = (int) (this.release * SAMPLE_RATE);
		// the sustain will save the remaining samples
		int sampleLengthSustain = n - sampleLengthRelease - sampleLengthDecay - sampleLengthAttack;
		

		
		// CASE THE SUM OF THE SAMPLES SURPASS THE LENGTH OF THE ORGINAL SAMPLE
		
		sampleLengthRelease += Math.min(sampleLengthSustain, 0);
		sampleLengthSustain = Math.max(sampleLengthSustain, 0);
		
		sampleLengthDecay += Math.min(sampleLengthRelease, 0);
		sampleLengthRelease = Math.max(sampleLengthRelease, 0);
		
		sampleLengthAttack += Math.min(sampleLengthDecay, 0);
		sampleLengthDecay = Math.max(sampleLengthDecay, 0);
		
		sampleLengthAttack = Math.min(sampleLengthAttack, n);
		
		/* worst case scenario:
				n = 1000 ms; a = 1400 ms; d = 100ms; r = 100ms
				s = 1000 - 1400 - 100 - 100 = -400 < 0 => sustain exceeds
				r = (r = 100) + (s = -400) = -300 < 0 => release exceeds; s = 0
				d = (d = 100) + (r = -300) = -200 < 0 => decay exceeds; r = 0
				a = (a = 1400) + (d = -200) = 1200; a > n => attack exceeds; d = 0
				a = (a = 1200) - (1200 - (n = 1000)) => a = 1000 = n => finally, signal suits
		 */
		
		//
		
		final double maxVolume = 1; // 100 %
		double sustainVolume = this.getSustain();
		final double minVolume = 0; // 0 %
		final double attenuationRatio = 1;
		
		// ADSR sample lengths are inside an array because the threads are not able to read the variables for some reason
		int[] localSampleLengths = {sampleLengthAttack, sampleLengthDecay, sampleLengthSustain, sampleLengthRelease};

		
	// THREADS
		Thread attackWorker = new Thread() {
			public void run() {
				int lastPosition = localSampleLengths[0]; // attack ends at its length, obviously
				int firstPosition = 0; // attack starts at 0
				int offsetEnd = localSampleLengths[0];
				
				
				for (int offset = 0; offset < offsetEnd; offset++) {
					
					// realistic attenuation (Function.attenuator()); //
					//double attenuation = Function.attenuator(Function.linear(firstPosition, minVolume, lastPosition, maxVolume, offset), attenuationRatio); 

					double attenuation = Function.linear(firstPosition, minVolume, lastPosition, maxVolume, offset);
					inputSignal[firstPosition + offset] *= attenuation;
				}
				
				System.out.println("Attack Thread Ended");

			}
			
		};
		
		
		Thread decayWorker = new Thread() {
			public void run() {
				int lastPosition = localSampleLengths[1] + localSampleLengths[0]; // decay ends at its length, plus attack's length
				int firstPosition = localSampleLengths[0]; // decay starts when attack ends, thus, first position of the decay equals the length of the attack
				int offsetEnd = localSampleLengths[1];

				// SLOPE = (lastValue - firstValue) / (lastPos - firstPos);
				double decaySlope = (sustainVolume - maxVolume) / (lastPosition - firstPosition); // volume will go from the max to the sustain (decay's definition)
				
				for (int offset = 0; offset < offsetEnd; offset++) {
					
					// realistic attenuation (Function.attenuator());
					double attenuation = Function.attenuator(Function.linear(firstPosition, maxVolume, lastPosition, sustainVolume, offset), attenuationRatio);
					//double attenuation = Function.linear(firstPosition, maxVolume, lastPosition, sustainVolume, offset);
					inputSignal[firstPosition + offset] *= attenuation;
				}

				
				System.out.println("Decay Thread Ended");
			}
		};
		
		
		Thread sustainWorker = new Thread() {
			public void run() {
				int lastPosition = localSampleLengths[2] + localSampleLengths[1] + localSampleLengths[0]; // sustain ends at its length, plus attack's and decay's.
				int firstPosition = localSampleLengths[1] + localSampleLengths[0]; // sustain starts where decay stopped
				int offsetEnd = localSampleLengths[2];
				
				for (int offset = 0; offset < offsetEnd; offset++) {
					
					// realistic attenuation
					//double attenuation = Function.attenuator(sustainValue, attenuationRatio);
					double attenuation = sustainVolume;
					inputSignal[firstPosition + offset] *= attenuation; // the whole sustain should remain the same
				}
				
				System.out.println("Sustain Thread Ended");
			}
		};
		
		
		Thread releaseWorker = new Thread() {
			public void run() {
				int lastPosition = n; // ends at the length of the sample
				int firstPosition = localSampleLengths[0] + localSampleLengths[1] + localSampleLengths[2]; // starts at sustain end
				int offsetEnd = localSampleLengths[3];
				
				// SLOPE = (lastValue - firstValue) / (lastPos - firstPos);
				double releaseSlope = (minVolume - sustainVolume) / (lastPosition - firstPosition);
				
				for (int offset = 0; offset < offsetEnd; offset++) {

	
					// x = offset, y = sustainValue (initalValue), m = releaseSlope -- f(x) = m*x + y
					//double attenuation = Function.attenuator(Function.linear(offset, sustainValue, releaseSlope), attenuationRatio); 
					double attenuation = Function.linear(offset, sustainVolume, releaseSlope); 
					inputSignal[firstPosition + offset] *= attenuation;
				}

				System.out.println("Release Thread Ended");
			}
		};
		
		attackWorker.start();
		decayWorker.start();
		sustainWorker.start();
		releaseWorker.start();
		
		try {
			attackWorker.join();
			decayWorker.join();
			sustainWorker.join();
			releaseWorker.join();
			
		} catch (Exception e) {
			System.out.println("Error joining threads?");
			e.printStackTrace();
		}
		System.out.println("All the threads are now dead.");
		
		return inputSignal;
	}
	
	
}

