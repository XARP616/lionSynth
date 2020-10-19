package lionSynth;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Function {
	public static int square(double value) {
		double period = 2 * Math.PI;
		
		// IF THE VALUE IS LESS THAN MATH.PI AND GREATER OR EQUAL TO ZERO, IT WILL RETURN 1; 
		//OTHERWISE, IF THE VALUE IS GREATER OR EQUAL TO MATH.PI AND LESS THAN 2 TIMES MATH.PI, IT WILL RETURN 0.
		// THE VALUE SHOULDN'T EXCEED 2 TIMES PI
		
		value = value % period;
		if (value >= 0 && value < period / 2) {
			return 1;
		} else {
			return -1;
			
		}
	}
	
	public static double pulse(double value) {
		double period = 2 * Math.PI;

		value = value % period;
		if (value >= 0 && value < period / 4) {
			return 1;
		} else {
			return -1;
			
		}
	}
	
	public static double triangle(double value) {
		double period = 2 * Math.PI;
		
		value = value % period;
		
		if (value >= 0 && value < Math.PI) {
			return value;
		} else {
			return period - value;
		}
	}
	
	public static double saw(double value) {
		double period = 2 * Math.PI;
		
		value = value % period;
		
		return value;
	}
	
	public static double attenuator(double value, double attenuationRatio) {
		return Math.pow(value, attenuationRatio); // recommended attenuationRatio = 1.1
	}
	
	public static double linear(double x, double y, double m) {
		// f(x) = mx + y
		return (m*x + y);
	}
	
	public static double linear(double ax, double ay, double bx, double by, double x) {
		double m = (by - ay) / (bx - ax);
		//System.out.println("ax: " + ax + ", ay: " + ay + ", bx: " + bx + ", by: " + by + ", x: " + x + ", m: " + m);
		// y = m(x - ax) + ay
		return m*(x - ax) + ay;
	}
	
	public static double linear(Point a, Point b, double x) {
		double m = (b.y - a.y) / (b.x - a.x);
		// y - y1 = m(x - x1)
		return m*(x - a.x) + a.y;
	}
	
	public static double adsrSlider(double sliderValue) { // value returned is 10^-1 milliseconds
		HashMap<Integer, Integer> valueMap = new HashMap<Integer, Integer>();
		valueMap.put(0, 0);
		valueMap.put(17, 41); // 17% of the slider -> 4.1 milliseconds
		valueMap.put(33, 1230); // 33% -> 123 milliseconds
		valueMap.put(50, 10000); // 50% -> 1 s
		valueMap.put(67, 38100); // 67% -> 3.18 s
		valueMap.put(83, 129000); // 83% -> 12.9 s
		valueMap.put(100, 320000); // 100% -> 32 s
		ArrayList<Integer> sliderValues = new ArrayList<Integer>();
		
		// keys -> ArrayList
		for (Object o: valueMap.keySet().toArray()) {
			sliderValues.add( (Integer) o );
		}
		
		
		int previous = sliderValues.get(0);
		for (int i = 1; i < sliderValues.size(); i++) {
			int current = sliderValues.get(i);
			if (sliderValue <= current) {
				
				// calculating the value in between the previous value and the current one
				Point a = new Point(current, valueMap.get(current));
				Point b = new Point(previous, valueMap.get(previous));
				//System.out.println("[ ! ] Value at " + sliderValue + " is: " + Function.linear(a, b, sliderValue));
				return Function.linear(a, b, sliderValue);
				
			} else {
				previous = current;
			}
		}
		
		return 0.0; // if nothing is returned previously (which should never happen)
	}
	
	
}