package Project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import Algorithms.AlgBFPGB;
import Algorithms.AlgBFPGL;
import Algorithms.AlgPGB;
import Algorithms.AlgPGI;
import Algorithms.AlgPGL;
import Algorithms.ThresholdAlgorithmMC;
import Data.GenStatisticalTrace;
import Data.ReadGoogleJobs;
import Jobs.Job;

public class RunSimulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		//-----------------------------------------------------------------------------------------//
		//USER INPUT BEGINS.
		//Please go through the comments and select the configuration required for your simulation
		
		
		//Provide the required day number
		// inputDay = 1 or 2 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 10
		//				 or 11  or 12 or 13 or 14 or 15 or 16 or 17 or 18 or 19 or 20
		//				  or 21 or 22 or 23 or 24 or 25 or 26 or 27 or 28 or 29 
		int inputDay = 11;
		
		//Provide the slack value
		// inputSlack = 0.1 or 0.2 or 0.3 or 0.4 or 0.5 or 0.6 or 0.7 or 0.8 or 0.9
		double inputSlack = 0.5;
		
		//Provide the input for standard deviation
		// standard deviation is calculated as StD = (inputSlack / denom) where
		// denom = 2.0 or 2.1 or 2.2 or 2.3 or 2.4 or 2.5 or 2.6 or 2.7 or 2.8 or 2.9 or 3.0
		double inputDenom = 2.5;
		
		//Provide number of cores to pick jobs from the trace
		// inputCore = 1 or 30 or 120 or 5000
		int inputCore = 30;
		
		//Provide a value for core set up
		// minimum starting cores will be as follows : for inputCore = 1, minimum Cores = 10
		// for the rest, minimum Cores = inputCore
		
		// provide a value for maximum number of cores
		// e.g. if totalCores = 500 and inputCore = 30; then the simulation will run from 30 cores upto 500 cores 
		int totalCores = 120;
		
		// provide a value by which cores need to increment
		// e.g. if incrementCore = 30, then for each run, cores will be incremented by 30
		// i.e. for every iteration, cores will go 30, 60, 90, ... till it reaches the value provided for totalCores
		int incrementCore = 30;
		
		//Provide a value 
		
		
		
		//Set algorithms selected of simulation to 1, else set it to 0
		int GREEDYBALANCED = 1;
		int GREEDYBESTFIT = 1;
		int THRESHOLD = 1;
		int GREEDYMINIDLE = 1;
		
		int GREEDYBALANEDBACKFILL = 1; 
		int GREEDYBESTFITBACKFILL = 1;
		//NOTE : Backfilling works only in multi-core environment
		//-----------------------------------------------------------------------------------------//
		//USER INPUT ENDS
		//-----------------------------------------------------------------------------------------//
		//-----------------------------------------------------------------------------------------//
		//INFORMATION ON ACCESSING THE SIMULATION RESULTS
		//Once the simulation is over, the consolidated results will be available inside the results
		//folder in src. The value table is in the following order
		// COL 1 : Set number, ranges from 1 to 10 
		// COL 2 : Slack
		// COL 3 : Standard Deviation
		// COL 4 : Total Machines in this particular simulation
		// COL 5 : Denominator Value. Standard Deviation = Slack / Denominator
		// COL 6 : Accepted Load - Greedy Balanced
		// COL 7 : Rejected Load - Greedy Balanced
		// COL 8 : Maximum Load - Greedy Balanced 
		// COL 9 : Accepted Load - Greedy BestFit
		// COL 10 : Rejected Load - Greedy BestFit
		// COL 11 : Maximum Load - Greedy BestFit
		// COL 12 : Accepted Load - Threshold 
		// COL 13 : Rejected Load - Threshold
		// COL 14 : Maximum Load - Threshold
		// COL 15 : Accepted Load - MinIdle 
		// COL 16 : Rejected Load - MinIdle
		// COL 17 : Maximum Load - MinIdle
		// COL 18 : Accepted Load - Greedy Balanced BackFill
		// COL 19 : Rejected Load - Greedy Balanced BackFill
		// COL 20 : Maximum Load - Greedy Balanced BackFill
		// COL 21 : Accepted Load - Greedy BestFit BackFill
		// COL 22 : Rejected Load - Greedy BestFit BackFill
		// COL 23 : Maximum Load - Greedy BestFit BackFill
		//-----------------------------------------------------------------------------------------//
		//-----------------------------------------------------------------------------------------//
		//-----------------------------------------------------------------------------------------//
		//Please DO NOT change the script from now on
		
		//Consolidating algorithm choices
		ArrayList<Integer> algorithms = new ArrayList<Integer>();
		algorithms.add(GREEDYBALANCED);
		algorithms.add(GREEDYBESTFIT);
		algorithms.add(THRESHOLD);
		algorithms.add(GREEDYMINIDLE);
		algorithms.add(GREEDYBESTFITBACKFILL);
		algorithms.add(GREEDYBESTFITBACKFILL);
		
		//Generating the statistical trace required for simulation on a particular day
		System.out.println("Generating complete Statistical Trace for Day " + inputDay);
		generateStatisticalTrace(inputDay, inputCore, inputSlack, inputDenom);
		System.out.println("Trace generation completed.");
		
		//Running the simulation
		runSimulation(inputDay, inputCore, inputSlack, inputDenom, totalCores, incrementCore, algorithms);
		System.out.println("End of simulation.");
		
	}
	
	private static void generateStatisticalTrace(int dayNr, int jobCoreCount, double slack, double denominator) {
		GenStatisticalTrace gTrace = new GenStatisticalTrace();
		gTrace.generateTrace(dayNr, jobCoreCount, slack, denominator);
	}
	
	private static void runSimulation(int dayNr, int coreNr, double slack, double denominator, int totalMachines, int machineIncrement, ArrayList<Integer> algorithms) {
		String readFolderPath = "./src/StatisticalTraceC" + coreNr + "/Day" + dayNr + "/";
		String readFileName = "GoogleTraceD";
		String readFileExtension = ".txt";
		String writeFolderPath = "./src/ResultsC" + coreNr + "/Day" + dayNr + "/";
		String writeFileName = "ConsolidatedD" + dayNr;
		String writeFileExtension = ".txt";
		
		double standardDeviation = Math.round(slack / denominator * 1000d) / 1000d;
		
		for(int set=1; set<=10; set=set+1) {
			String writeFileAddress = writeFolderPath + writeFileName + "SET" + set + writeFileExtension;
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(writeFileAddress));
				
				String readFileAddress = readFolderPath + readFileName + dayNr + "M" + slack + "SD" + standardDeviation + "C" + coreNr + "S" + set + readFileExtension;
						
				ReadGoogleJobs gClass = new ReadGoogleJobs();
				LinkedList<Job> jobs =  gClass.readTraceJobs(readFileAddress);
						
				for(int machines = coreNr; machines <= totalMachines; machines = machines + machineIncrement) {
					String writeString = "";
					
					writeString = writeString + set + ";" + slack + ";" + standardDeviation + ";" + machines + ";" + denominator + ";";
					
					if(algorithms.get(0) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						AlgPGB algBalanced = new AlgPGB(machines);
						ArrayList<Long> resultBal = algBalanced.runAlgorithm((LinkedList<Job>) jobs.clone(), false);
						Long acceptBalL = resultBal.get(0);
						Long rejectBalL = resultBal.get(1);
						Long totalBalL = resultBal.get(2);
						writeString = writeString + acceptBalL + ";" + rejectBalL + ";" + totalBalL + ";";
					}
					
					if(algorithms.get(1) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						AlgPGL algLoaded = new AlgPGL(machines);
						ArrayList<Long> resultLoad = algLoaded.runAlgorithm((LinkedList<Job>) jobs.clone(), false);
						Long acceptLoadL = resultLoad.get(0);
						Long rejectLoadL = resultLoad.get(1);
						Long totalLoadL = resultLoad.get(2);
						writeString = writeString + acceptLoadL + ";" + rejectLoadL + ";" + totalLoadL + ";";
					}
					
					if(algorithms.get(2) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						ThresholdAlgorithmMC algThreshold = new ThresholdAlgorithmMC(slack, machines);
						ArrayList<Long> resultThres = algThreshold.runAlgorithm((LinkedList<Job>) jobs.clone(), false);
						Long acceptThresL = resultThres.get(0);
						Long rejectThresL = resultThres.get(1);
						Long totalThresL = resultThres.get(2);
						writeString = writeString + acceptThresL + ";" + rejectThresL + ";" + totalThresL + ";";
					}
					
					if(algorithms.get(3) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						AlgPGI algMinIdle = new AlgPGI(machines);
						ArrayList<Long> resultMinIdle = algMinIdle.runAlgorithm((LinkedList<Job>) jobs.clone(),false);
						Long acceptMinIdleL = resultMinIdle.get(0);
						Long rejectMinIdleL = resultMinIdle.get(1);
						Long totalMinIdleL = resultMinIdle.get(2);
						writeString = writeString + acceptMinIdleL + ";" + rejectMinIdleL + ";" + totalMinIdleL + ";";
					}
					
					if(algorithms.get(4) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						AlgBFPGB algBalBF = new AlgBFPGB(machines);
						ArrayList<Long> resultBalBackfill = algBalBF.runAlgorithm((LinkedList<Job>) jobs.clone(),false);
						Long acceptBalBackfillL = resultBalBackfill.get(0);
						Long rejectBalBackfillL = resultBalBackfill.get(1);
						Long totalBalBackfillL = resultBalBackfill.get(2);
						writeString = writeString + acceptBalBackfillL + ";" + rejectBalBackfillL + ";" + totalBalBackfillL + ";";
					}
					
					if(algorithms.get(5) == 0) {
						writeString = writeString + "0; 0; 0;";
					}else {
						AlgBFPGL algLoadBF = new AlgBFPGL(machines);
						ArrayList<Long> resultalgLoadBF = algLoadBF.runAlgorithm((LinkedList<Job>) jobs.clone(),false);
						Long acceptLoadBFL = resultalgLoadBF.get(0);
						Long rejectLoadBFL = resultalgLoadBF.get(1);
						Long totalLoadBFL = resultalgLoadBF.get(2);
						writeString = writeString + acceptLoadBFL + ";" + rejectLoadBFL + ";" + totalLoadBFL + ";";
					}
					writeString = writeString + "\n";
							
					System.out.println(writeString);
					bWriter.write(writeString);
				}
				bWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	
}
