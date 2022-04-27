package Algorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Data.EpsilonValue;
import Data.LogrithmicSearch;
import Jobs.Job;
import Machines.Machine;

public class ThresholdAlgorithmMC {
	
	private LinkedList<Machine> machines = new LinkedList<Machine>();
	private LinkedList<Job> rejectedJobs = new LinkedList<Job>();
	private double processedLoad;
	private double rejectedLoad;
	private double deadlineThreshold;
	ArrayList<Double> functionValuesEpsilon = new ArrayList<Double>();
	private double slack;
	
	public ThresholdAlgorithmMC(double slack, int totalMachines) {
		this.slack = slack;
		this.deadlineThreshold = 0;
		
		for(int m=0; m<totalMachines; m++) {
			String machineID = "M" + m;
			Machine machine = new Machine(machineID);
			machines.add(machine);
		}
	}
	
	public ArrayList<Long> runAlgorithm(LinkedList<Job> jobs, boolean displayStatus) {
		ArrayList<Long> results = new ArrayList<Long>();
		processedLoad = 0;
		rejectedLoad = 0;
		int rejectCount = 0;
		EpsilonValue eValueClass = new EpsilonValue(machines.size());
		functionValuesEpsilon = eValueClass.findFunctionValuesForEpsilon(slack);
			
		for(int n=0; n<jobs.size(); n++) {
			//Sort machines such that they align based on decreasing order of the load
			Collections.sort(machines, Machine.sortAvailableTimeAscending);
			//Reading job properties
			int jobProcessingTime = jobs.get(n).getProcessingTime();
			int jobReleaseTime = jobs.get(n).getReleaseTime();
			int jobDueTime = jobs.get(n).getDueTime();
			int jobCore = jobs.get(n).getJobCore();
			
			calculateDeadlineThreshold(jobReleaseTime);
			
			if(jobDueTime < deadlineThreshold) {
				rejectedLoad = rejectedLoad + (jobProcessingTime * jobCore);
				rejectedJobs.add(jobs.get(n).clone());
				//rejectCount = rejectCount + 1;
			}else {
				Collections.sort(machines, Machine.sortAvailableTimeDescending);
				int mIndex = machines.size() - jobCore;
				if(mIndex < 0) {
					rejectedLoad = rejectedLoad + (jobProcessingTime * jobCore);
					rejectedJobs.add(jobs.get(n).clone());
				}else {
					int machineAvailableTime = machines.get(mIndex).getAvailableTime();
					int jobCompletionTime = Integer.max(machineAvailableTime, jobReleaseTime) + jobProcessingTime;
				
					boolean isSchedulable = false;
					if(jobCompletionTime <= jobDueTime) {
						isSchedulable = true;
					}
				
					if(isSchedulable) {
						//System.out.println(jobs.get(n).getJobID());
						processedLoad = processedLoad + (jobProcessingTime * jobCore);
						LogrithmicSearch lFunc = new LogrithmicSearch();
						int mIndexL = lFunc.findIndex(machines, jobs.get(n).clone());
						//System.out.println(machines.get(mIndexL).getMachineID() + "-" + jobs.get(n).getJobID());
						
						//System.out.println(machines.get(mIndexL).getMachineID() + "-" + jobs.get(n).getJobID());
						
						//System.out.println("n = " + n + "; " + mIndexL );
						jobCompletionTime = Integer.max(jobReleaseTime, machines.get(mIndexL).getAvailableTime()) + jobProcessingTime;
						//System.out.println(jobCompletionTime);
						for(int c=0; c<jobCore; c++) {
							machines.get(mIndexL+c).allocateJob(jobs.get(n).clone(), jobCompletionTime);
						}
					}else {
						rejectedLoad = rejectedLoad + (jobProcessingTime * jobCore);
						rejectedJobs.add(jobs.get(n).clone());
					}
				}
			}
		
			
		}
		
		results.add((long)processedLoad);
		results.add((long)rejectedLoad);
		
		Collections.sort(machines, Machine.sortAvailableTimeDescending);
		int makeSpan = machines.getFirst().getAvailableTime();
		Long totalLoad = Long.min((long)machines.size() * makeSpan, (long)(processedLoad + rejectedLoad));
		results.add(totalLoad);
		
		if(displayStatus) {
			displayMachineDetails();
		}
		
		return results;
	}
	
	private void displayMachineDetails() {
		String writeFileAddress = "./src/GoogleTrace/Result/AcceptedJobsB.txt";
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(writeFileAddress));
			for(int m=0; m<machines.size(); m++) {
				String displayString = machines.get(m).getMachineID() + " -> ";
				for(int j=0; j<machines.get(m).getAcceptedJobs().size(); j++) {
					displayString = displayString + machines.get(m).getAcceptedJobs().get(j).getJobID() + "";
				}
				bWriter.write(displayString + "\n");;
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void calculateDeadlineThreshold(int jobReleaseTime) {
		ArrayList<Double> valueDeadlineThresholds = new ArrayList<Double>();
		
		for(int i=1; i<functionValuesEpsilon.size(); i++) {
			int machineIndex = functionValuesEpsilon.size() - i - 1;
			if(functionValuesEpsilon.get(i) > 0) {
				//System.out.println(functionValuesEpsilon.get(i) + " - " + i);
				double loadOnMachine = Integer.max(machines.get(machineIndex).getAvailableTime() - jobReleaseTime, 0);
				double valueDeadlineThreshold = jobReleaseTime + (loadOnMachine * functionValuesEpsilon.get(i));
				//System.out.println("Load : " + loadOnMachine + "; Deadline Threshold : " + valueDeadlineThreshold);
				
				valueDeadlineThresholds.add(valueDeadlineThreshold); 
			}
		}
		deadlineThreshold = Collections.max(valueDeadlineThresholds);
	}
}
