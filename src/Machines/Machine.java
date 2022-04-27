package Machines;

import java.util.Comparator;
import java.util.LinkedList;

import Jobs.Job;

public class Machine {
	
	private LinkedList<Job> acceptedJobs = new LinkedList<Job>();
	private int availableTime;
	private String machineID;
	
	public Machine(String machineID) {
		this.machineID = machineID;
		this.availableTime = 0;
	}

	public void allocateJob(Job acceptedJob, int availableTime) {
		this.acceptedJobs.add(acceptedJob);
		this.availableTime = availableTime;
	}

	public LinkedList<Job> getAcceptedJobs() {
		return acceptedJobs;
	}

	public int getAvailableTime() {
		return availableTime;
	}
	
	public String getMachineID() {
		return machineID;
	}
	
	public static Comparator<Machine> sortAvailableTimeAscending = new Comparator<Machine>(){
		public int compare(Machine M1, Machine M2) {
			return Integer.compare(M1.getAvailableTime(), M2.getAvailableTime());
		}
	};
	
	public static Comparator<Machine> sortAvailableTimeDescending = new Comparator<Machine>(){
		public int compare(Machine M1, Machine M2) {
			return Integer.compare(M2.getAvailableTime(), M1.getAvailableTime());
		}
	};
}
