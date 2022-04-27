package Algorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Jobs.Job;
import Machines.Machine;
import Data.LogrithmicSearch;

public class AlgPGL {
	
	private LinkedList<Machine> machines = new LinkedList<Machine>();
	private LinkedList<Job> rejectedJobs = new LinkedList<Job>();
	private double processedLoad;
	private double rejectedLoad;

	public AlgPGL(int totalMachines) {
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
		for(int n=0; n<jobs.size(); n++) {
			//Sort machines such that they align based on decreasing order of the load
			Collections.sort(machines, Machine.sortAvailableTimeDescending);
			//Reading job properties
			int jobProcessingTime = jobs.get(n).getProcessingTime();
			int jobReleaseTime = jobs.get(n).getReleaseTime();
			int jobDueTime = jobs.get(n).getDueTime();
			int jobCore = jobs.get(n).getJobCore();
			
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
					processedLoad = processedLoad + (jobProcessingTime * jobCore);
					LogrithmicSearch lFunc = new LogrithmicSearch();
					int mIndexL = lFunc.findIndex(machines, jobs.get(n).clone());
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
}
