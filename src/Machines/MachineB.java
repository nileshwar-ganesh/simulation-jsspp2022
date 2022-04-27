package Machines;

import java.util.Comparator;
import java.util.LinkedList;

import Jobs.Job;

public class MachineB {
	private String machineID;
	private Job allocatedJob;
	private int availableStartTime;
	private int availableEndTime;
	
	public MachineB() {
		this.availableStartTime = 0;
		this.availableEndTime = 0;
	}

	public void createSlot(String machineID, int availableStartTime, int availableEndTime) {
		this.machineID = machineID;
		this.availableStartTime = availableStartTime;
		this.availableEndTime = availableEndTime;
	}
	
	public void allocateJob(String machineID, Job job, int availableStartTime, int availableEndTime) {
		this.machineID = machineID;
		this.allocatedJob = job;
		this.availableStartTime = availableStartTime;
		this.availableEndTime = availableEndTime;
	}

	public int getAvailableStartTime() {
		return availableStartTime;
	}
	
	public int getAvailableEndTime() {
		return availableEndTime;
	}
	
	public String getMachineID() {
		return machineID;
	}

	public Job getAllocatedJob() {
		return allocatedJob;
	}



	public static Comparator<MachineB> sortAvailableStartTimeAscending = new Comparator<MachineB>(){
		public int compare(MachineB M1, MachineB M2) {
			return Integer.compare(M1.getAvailableStartTime(), M2.getAvailableStartTime());
		}
	};
	
	public static Comparator<MachineB> sortAvailableStartTimeDescending = new Comparator<MachineB>(){
		public int compare(MachineB M1, MachineB M2) {
			return Integer.compare(M2.getAvailableStartTime(), M1.getAvailableStartTime());
		}
	};
}
