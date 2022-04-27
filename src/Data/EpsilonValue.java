package Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpsilonValue {
	ArrayList<Double> functionValues = new ArrayList<Double>();
	ArrayList<ArrayList<Double>> initialFunctionValues = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> functionLimits = new ArrayList<ArrayList<Double>>();

	public EpsilonValue(int machineNr) {
		for(int m=0;m<=machineNr;m++) {
			functionValues.add(0.0);
		}
	}
	
	public void solve() {
		solveForEpsilon();
		displayInitialFunctionValues();		
		//calculateFunctionLimits();
		//displayFunctionLimits();
		//calculateAllEpsilonValues();
	}
	
	public ArrayList<Double> findFunctionValuesForEpsilon(double valueEpsilon){
		ArrayList<Double> functionValuesForEpsilon = new ArrayList<Double>();
		solveForEpsilon();
		//displayInitialFunctionValues();		
		calculateFunctionLimits();
		//displayFunctionLimits();
		//calculateAllEpsilonValues();
		
		for(int interval = 0; interval<functionLimits.size(); interval++) {
			double lowerLimit = functionLimits.get(interval).get(0);
			double upperLimit = functionLimits.get(interval).get(1);
			
			int i = functionLimits.size() - interval;
			
			ArrayList<Double> lowerFunctionValues = (ArrayList<Double>) calculateEpsilonValues(i, lowerLimit).clone();
			ArrayList<Double> upperFunctionValues = (ArrayList<Double>) calculateEpsilonValues(i, upperLimit).clone();
			
			double valueUpperEpsilon = lowerFunctionValues.get(0); 
			double valueLowerEpsilon = upperFunctionValues.get(0);
			double valueMidEpsilon = Double.MAX_VALUE;
			//System.out.println("Upper : " + valueUpperEpsilon + "; Lower : " + valueLowerEpsilon);
			int counter = 1;
			if(valueEpsilon >= valueLowerEpsilon && valueEpsilon <= valueUpperEpsilon) {
				//System.out.println("Selected interval : " + lowerLimit + " - " + upperLimit + ": Selected i : " + i);
				
				while(Math.round(valueMidEpsilon*1000000000000d)/1000000000000d != valueEpsilon) {
					double midValue = (upperLimit + lowerLimit)/2;
					//System.out.println(midValue);
					
					lowerFunctionValues = (ArrayList<Double>) calculateEpsilonValues(i, lowerLimit).clone();
					upperFunctionValues = (ArrayList<Double>) calculateEpsilonValues(i, upperLimit).clone();
					
					valueUpperEpsilon = lowerFunctionValues.get(0); 
					valueLowerEpsilon = upperFunctionValues.get(0);
					
					ArrayList<Double> midFunctionValues = (ArrayList<Double>) calculateEpsilonValues(i, midValue).clone();
					valueMidEpsilon = midFunctionValues.get(0);
					
					//System.out.println(valueEpsilon + " - " + valueMidEpsilon);
					
					if(valueEpsilon <= valueMidEpsilon && valueEpsilon >= valueLowerEpsilon) {
						lowerLimit = midValue;
					}else if(valueEpsilon >= valueMidEpsilon && valueEpsilon <= valueUpperEpsilon) {
						upperLimit = midValue;
					}
					functionValuesForEpsilon = (ArrayList<Double>) midFunctionValues.clone();
				}
				break;
			}
		}
		return functionValuesForEpsilon;
	}
	
	
	private void solveForEpsilon() {
		int machineNr = functionValues.size() - 1;
		
		for(int i=machineNr-1; i>=1; i--) {
			//System.out.println("Value of i : " + i);
			for(int m=1; m<=machineNr; m++) {
				functionValues.set(m, 0.0);
			}
			functionValues.set(i, 2.0);
			double valueLeft = (functionValues.get(i) * machineNr + 1)/i;
			//System.out.println("Value of left hand side : " + valueLeft);
			for(int h=i+1; h<=machineNr; h++) {
				double denominator = i;
				for(int k=i; k<=h-1; k++) {
					denominator = denominator + functionValues.get(k) - 1; 
				}
				//System.out.println("Value of the denominator : " + denominator);
				double funcValue = (valueLeft * denominator - 1)/machineNr;
				//System.out.println("Value of the function : " + funcValue);
				functionValues.set(h,funcValue);
			}
			
			double valueEpsilon = 1 / (functionValues.get(machineNr)-1) ;
			
			initialFunctionValues.add((ArrayList<Double>) functionValues.clone());
			//System.out.println("Machine Nr : " + machineNr + "; i = " + i + "; epsilon = " + Math.round(valueEpsilon * 100000000d)/100000000d + "   (" + convertDecimalToFraction(valueEpsilon) + ")");
		}
	}
	
	private void displayInitialFunctionValues() {
		String writeDestination = "./src/CloudTraces/EpsilonValues.txt";
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(writeDestination));
			for(int i=0; i<initialFunctionValues.size(); i++) {
				String displayText = "";
				for(int j=0; j<initialFunctionValues.get(i).size(); j++) {
					displayText = displayText + Math.round(initialFunctionValues.get(i).get(j) * 1000d) / 1000d + ";";
				}
				double valueEpsilon = Math.round(1 / (initialFunctionValues.get(i).get(initialFunctionValues.get(i).size() - 1) - 1) * 1000d) / 1000d;
				System.out.println(displayText);
				bWriter.write(displayText + ";" + valueEpsilon + "\n");
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void calculateFunctionLimits() {
		ArrayList<Double> dummyList = new ArrayList<Double>();
		dummyList.add(2.0);
		dummyList.add(initialFunctionValues.get(0).get(initialFunctionValues.get(0).size()-1));
		functionLimits.add((ArrayList<Double>) dummyList.clone());
		for(int i=0; i<initialFunctionValues.size() - 1; i++) {
			for(int j=0; j<initialFunctionValues.get(i).size(); j++) {
				if(initialFunctionValues.get(i).get(j) == 2.0) {
					double lowerLimit = initialFunctionValues.get(i).get(j);
					double upperLimit = initialFunctionValues.get(i + 1).get(j);
					dummyList.clear();
					dummyList.add(lowerLimit);
					dummyList.add(upperLimit);
					functionLimits.add((ArrayList<Double>) dummyList.clone());
					break;
				}
			}
		}
		dummyList.clear();
		dummyList.add(2.0);
		dummyList.add(5.0);
		functionLimits.add((ArrayList<Double>) dummyList.clone());
	}
	
	private void displayFunctionLimits() {
		for(int i=0; i<functionLimits.size(); i++) {
			String displayString  = functionLimits.get(i).get(0) + " - " + functionLimits.get(i).get(1); 
			System.out.println(displayString);
		}
	}
	
	
	private ArrayList<Double> calculateEpsilonValues(int i, double fValue) {
		int machineNr = functionValues.size() - 1;
		
		for(int m=0; m<=machineNr; m++) {
			functionValues.set(m, 0.0);
		}
		
		functionValues.set(i, fValue);
		
		double valueLeft = (functionValues.get(i) * machineNr + 1)/i;
		
		for(int h=i+1; h<=machineNr; h++) {
			double denominator = i;
			for(int k=i; k<=h-1; k++) {
				denominator = denominator + functionValues.get(k) - 1; 
			}
			//System.out.println("Value of the denominator : " + denominator);
			double funcValue = (valueLeft * denominator - 1)/machineNr;
			//System.out.println("Value of the function : " + funcValue);
			functionValues.set(h,funcValue);
		}
		
		double valueEpsilon = 1 / (functionValues.get(machineNr)-1);
		functionValues.set(0, valueEpsilon);
		
		return functionValues;
	}
	
	private void calculateAllEpsilonValues() {
		int machineNr = functionValues.size() - 1;
		for(int f=functionLimits.size() - 1; f>=0; f--) {
			double lowerLimit = functionLimits.get(f).get(0);
			double upperLimit = functionLimits.get(f).get(1);
			double incrementValue = 0.001;
			int i = functionLimits.size() - f; 
			//System.out.println("I" + i + "Lower : " + lowerLimit + "Upper: " + upperLimit);
			
			try {
				String writeFile = "./src/CloudTraces/M" + machineNr + "I" + i + ".txt";
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(writeFile));
			
				for(double fValue=lowerLimit; fValue<= upperLimit; fValue=fValue+incrementValue) {
					ArrayList<Double> funcValues = calculateEpsilonValues(i, fValue);
					String writeString = "";
					for(int n=0; n<funcValues.size(); n++) {
						writeString = writeString + funcValues.get(n) + ";";	
					}
					bWriter.write(writeString + "\n");
				}
				bWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
				
			
		}
	}
	
	static private String convertDecimalToFraction(double x){
	    if (x < 0){
	        return "-" + convertDecimalToFraction(-x);
	    }
	    double tolerance = 1.0E-6;
	    double h1=1; double h2=0;
	    double k1=0; double k2=1;
	    double b = x;
	    do {
	        double a = Math.floor(b);
	        double aux = h1; h1 = a*h1+h2; h2 = aux;
	        aux = k1; k1 = a*k1+k2; k2 = aux;
	        b = 1/(b-a);
	    } while (Math.abs(x-h1/k1) > x*tolerance);

	    return h1+"/"+k1;
	}
}