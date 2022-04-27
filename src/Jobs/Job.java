package Jobs;

import java.util.Comparator;

public class Job {
	
	private final String jobID;
	private final int processingTime;
	private final int releaseTime;
	private final int dueTime;
	private final int jobCore;
	
	public Job(String jobID, int processingTime, int releaseTime, int dueTime, int jobCore) {
		this.jobID = jobID;
		this.releaseTime = releaseTime;
		this.processingTime = processingTime;
		this.dueTime = dueTime;
		this.jobCore = jobCore;
	}

	public String getJobID() {
		return jobID;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public int getReleaseTime() {
		return releaseTime;
	}

	public int getDueTime() {
		return dueTime;
	}
	
	public int getJobCore() {
		return jobCore;
	}
	
	//Various sort funtions based on job properties
	public static Comparator<Job> sortProcessingTimeAscending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J1.getProcessingTime(), J2.getProcessingTime());
		}
	};
	
	public static Comparator<Job> sortProcessingTimeDescending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J2.getProcessingTime(), J1.getProcessingTime());
		}
	};
	
	public static Comparator<Job> sortReleaseTimeAscending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J1.getReleaseTime(), J2.getReleaseTime());
		}
	};
	
	public static Comparator<Job> sortReleaseTimeDescending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J2.getReleaseTime(), J1.getReleaseTime());
		}
	};
	
	public static Comparator<Job> sortDueTimeAscending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J1.getDueTime(), J2.getDueTime());
		}
	};
	
	public static Comparator<Job> sortDueTimeDescending = new Comparator<Job>(){
		public int compare(Job J1, Job J2) {
			return Integer.compare(J2.getDueTime(), J1.getDueTime());
		}
	};
	
	public String getJobDetails() {	
		return "ID : " + jobID + "; pj : " + processingTime + "; rj : " + releaseTime + "; dj : " + dueTime + "; cores : " + jobCore;
	}
	
	//Cloning a job (useful if a job had to be assigned/copied)
	public Job clone() {
		Job clonedJob = new Job(jobID, processingTime, releaseTime, dueTime, jobCore);
		return clonedJob;
	}
	
}
