package Algorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import Jobs.Job;
import Machines.Machine;
import Machines.MachineB;

public class AlgBFPGB {
	
	private LinkedList<Machine> machines = new LinkedList<Machine>();
	private LinkedList<MachineB> machinesB = new LinkedList<MachineB>();
	private LinkedList<Job> rejectedJobs = new LinkedList<Job>();
	private LinkedList<MachineB> backfillJobs = new LinkedList<MachineB>();
	private double processedLoad;
	private double rejectedLoad;

	public AlgBFPGB(int totalMachines) {
		for(int m=0; m<totalMachines; m++) {
			String machineID = "M" + m;
			Machine machine = new Machine(machineID);
			machines.add(machine);
		}
		this.processedLoad = 0;
		this.rejectedLoad = 0;
	}
	
	public ArrayList<Long> runAlgorithm(LinkedList<Job> jobs, boolean displayStatus) {
		ArrayList<Long> results = new ArrayList<Long>();
		
		for(int n=0; n<jobs.size(); n++) {
			//Sort machines such that they align based on decreasing order of the load
			Collections.sort(machines, Machine.sortAvailableTimeDescending);
			//Reading job properties
			int jobProcessingTime = jobs.get(n).getProcessingTime();
			int jobReleaseTime = jobs.get(n).getReleaseTime();
			int jobDueTime = jobs.get(n).getDueTime();
			int jobCore = jobs.get(n).getJobCore();
			
			//for(int l=0; l<machinesB.size(); l++ ) {
			//	System.out.println("Start : " + machinesB.get(l).getAvailableStartTime() + "; End : " + machinesB.get(l).getAvailableEndTime());
			//}
									
			int mIndex = machines.size() - jobCore;
			if(mIndex < 0) {
				rejectedLoad = rejectedLoad + (jobProcessingTime * jobCore);
				rejectedJobs.add(jobs.get(n).clone());
			}else {
				
				boolean isBackFillable = checkBackFilling(jobs.get(n).clone());				
				
				if(!isBackFillable) {
					int machineAvailableTime = machines.get(mIndex).getAvailableTime();
					int jobStartTime = Integer.max(machineAvailableTime, jobReleaseTime);
					int jobCompletionTime = jobStartTime + jobProcessingTime;
					
					boolean isSchedulable = false;
					if(jobCompletionTime <= jobDueTime) {
						isSchedulable = true;
					}
					
					if(isSchedulable) {
						processedLoad = processedLoad + (jobProcessingTime * jobCore);
						for(int m=mIndex; m<machines.size(); m++) {
							if(machines.get(m).getAvailableTime() < jobStartTime) {
								MachineB machine = new MachineB();
								machine.createSlot(machines.get(m).getMachineID(), machines.get(m).getAvailableTime(), jobStartTime);
								machinesB.add(machine);
							}
							machines.get(m).allocateJob(jobs.get(n).clone(), jobCompletionTime);
						}
					}else {
						rejectedLoad = rejectedLoad + (jobProcessingTime * jobCore);
						rejectedJobs.add(jobs.get(n).clone());
					}
				}else {
					processedLoad = processedLoad + (jobProcessingTime * jobCore);
				}
			}
		}
		
		//System.out.println("Total Size : " + machinesB.size());
		//for(int l=0; l<machinesB.size(); l++ ) {
		//	System.out.println("Start : " + machinesB.get(l).getAvailableStartTime() + "; End : " + machinesB.get(l).getAvailableEndTime());
		//}
		
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
			
			for(int j=0; j<backfillJobs.size(); j++) {
				String displayString = backfillJobs.get(j).getMachineID() + " -> " + backfillJobs.get(j).getAllocatedJob().getJobID() + " : " + backfillJobs.get(j).getAvailableStartTime() + " - " + backfillJobs.get(j).getAvailableEndTime() + "\n";
				bWriter.write(displayString);
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkBackFilling(Job job) {
		boolean isBackFillable = false;
		ArrayList<Integer> machineIndices = new ArrayList<Integer>();
		
		//is only possible if slots are available for backfilling
		if(machinesB.size() > 0) {
			if(machinesB.size() < job.getJobCore()) {
			//not enough cores available, so no backfilling	
			}else {
				
				Collections.sort(machinesB, MachineB.sortAvailableStartTimeAscending);
				
				for(int mB = 0; mB < machinesB.size(); mB++) {
					int availCore = 0;
					//we start with available slots
					machineIndices = new ArrayList<Integer>();
					
					int mAvailS = machinesB.get(0).getAvailableStartTime();
					int mAvailE = machinesB.get(0).getAvailableEndTime();
					
					int slotStartTime = Integer.max(mAvailS, job.getReleaseTime());
					int slotEndTime = Integer.min(mAvailE, job.getDueTime());
					
					int availTime = slotEndTime - slotStartTime;
					
					//if the machine is capable of handling the job
					if(job.getProcessingTime() <= availTime) {
						//System.out.println("Processing job " + job.getJobID());
						int jobStartTime = slotStartTime;
						int jobCompletionTime = jobStartTime + job.getProcessingTime();
						availCore = availCore + 1;
						//the machine is added to list of indices
						machineIndices.add(0);
						//if job is a single core job
						if(availCore == job.getJobCore()) {
							isBackFillable = true;
							String machineID = machinesB.get(0).getMachineID();
							mAvailS = machinesB.get(0).getAvailableStartTime();
							mAvailE = machinesB.get(0).getAvailableEndTime();
							
							if(mAvailS < jobStartTime) {
								MachineB sMachine = new MachineB();
								sMachine.createSlot(machineID, mAvailS, jobStartTime);
								machinesB.add(sMachine);
							}
								
							if(mAvailE > jobCompletionTime) {
								MachineB eMachine = new MachineB();
								eMachine.createSlot(machineID, jobCompletionTime, mAvailE);
								machinesB.add(eMachine);
							}
								
							MachineB backfillJob = new MachineB();
							backfillJob.allocateJob(machineID, job.clone(), jobStartTime, jobCompletionTime);
							backfillJobs.add(backfillJob);
							
							machinesB.removeFirst();
							
						}else {
							if(machinesB.size()>1) {
								for(int mC = 1; mC < machinesB.size(); mC++) {
									mAvailS = machinesB.get(mC).getAvailableStartTime();
									mAvailE = machinesB.get(mC).getAvailableEndTime();
									
									//System.out.println("mS : " + mAvailS + "; jS : " + jobStartTime + "; mE : " + mAvailE + "; jE : " + jobCompletionTime);
										
									if(mAvailS <= jobStartTime && mAvailE >= jobCompletionTime) {
										availCore = availCore + 1;
										machineIndices.add(mC);
									}
									
									if(availCore == job.getJobCore()) {
										isBackFillable = true;
										break;
									}
								}
								
								if(isBackFillable) {
									for(int index = 0; index < machineIndices.size(); index++) {
										int mID = machineIndices.get(index);
										String machineID = machinesB.get(mID).getMachineID();
										mAvailS = machinesB.get(mID).getAvailableStartTime();
										mAvailE = machinesB.get(mID).getAvailableEndTime();
										
										if(mAvailS < jobStartTime) {
											MachineB sMachine = new MachineB();
											sMachine.createSlot(machineID, mAvailS, jobStartTime);
											machinesB.add(sMachine);
										}
										
										if(mAvailE > jobCompletionTime) {
											MachineB eMachine = new MachineB();
											eMachine.createSlot(machineID, jobCompletionTime, mAvailE);
											machinesB.add(eMachine);
										}
										MachineB backfillJob = new MachineB();
										backfillJob.allocateJob(machineID, job.clone(), jobStartTime, jobCompletionTime);
										backfillJobs.add(backfillJob);
									}
									/*
									for(int m=0;m<machinesB.size();m++) {
										System.out.println(machinesB.get(m).getMachineID() + " : " + machinesB.get(m).getAvailableStartTime() + " - " + machinesB.get(m).getAvailableEndTime());
									}*/
									
									int offset = 0;
									for(int i=0; i<machineIndices.size(); i++) {
										int dI = machineIndices.get(i);
										machinesB.remove(dI - offset);
										offset = offset + 1;
									}//end of removing backfilled slots
								}//end of processing if job is backfillable
							}//end of checking if job is a multicore job
						}//end of else condition of multicore job
						
						if(isBackFillable) {
							break;
						}
					//when no sufficient space is available, remove the job and add it again
					}
					MachineB mRemove = machinesB.removeFirst();
					machinesB.add(mRemove);
				}
			}
		}
		return isBackFillable;
	}
}
