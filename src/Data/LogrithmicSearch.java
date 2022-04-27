package Data;

import java.util.Collections;
import java.util.LinkedList;

import Jobs.Job;
import Machines.Machine;

public class LogrithmicSearch {

	public LogrithmicSearch() {
		
	}
	
	public int findIndex(LinkedList<Machine> machines, Job job) {
		Collections.sort(machines, Machine.sortAvailableTimeDescending);
		
		int mIndex = Integer.MAX_VALUE;
		
		int jobCore = job.getJobCore();
		int mLow = 0;
		int mHigh = machines.size() - jobCore;
		int mMid = (int)Math.ceil((double)(mLow + mHigh)/2);
		
		int machineAvailableTimeL = machines.get(mLow).getAvailableTime();
		int machineAvailableTimeM = machines.get(mMid).getAvailableTime();
		int machineAvailableTimeH = machines.get(mHigh).getAvailableTime();
		
		int jobReleaseTime = job.getReleaseTime();
		int jobProcessingTime = job.getProcessingTime();
		int jobDueTime = job.getDueTime();
		
		int jobStartTimeL = Integer.max(jobReleaseTime, machineAvailableTimeL);
		int jobStartTimeM = Integer.max(jobReleaseTime, machineAvailableTimeM);
		int jobStartTimeH = Integer.max(jobReleaseTime, machineAvailableTimeH);
		
		int jobCompletionL = jobStartTimeL + jobProcessingTime;
		int jobCompletionM = jobStartTimeM  + jobProcessingTime;
		int jobCompletionH = jobStartTimeH  + jobProcessingTime;
		
		int idleL = jobDueTime - jobCompletionL;
		int idleM = jobDueTime - jobCompletionM;
		int idleH = jobDueTime - jobCompletionH;
		
		if(jobCompletionL <= jobDueTime) {
			idleL = jobStartTimeL - machineAvailableTimeL;
		}
		
		if(jobCompletionM <= jobDueTime) {
			idleM = jobStartTimeM - machineAvailableTimeM;
		}
		
		if(jobCompletionH <= jobDueTime) {
			idleH = jobStartTimeH - machineAvailableTimeH;
		}
		
		//System.out.println(idleL + " - " + job.getJobID() + " - " + jobCompletionL + " - " + jobCompletionM + " - " + jobCompletionH);
		
		if(idleL >= 0) {
			mIndex = mLow;
		}else {
			//int counter = 0;
			if(mMid == mHigh) {
				mIndex = mMid;
			}
			while(mMid != mHigh) {
				/*
				counter = counter + 1;
				if(counter == 10) {
					break;
				}
				
				
				System.out.println("mLow : " + mLow + "; mMid : " + mMid + "; mHigh : " + mHigh);
				System.out.println("idleL : " + idleL + "; idleM : " + idleM + "; idleH : " + idleH);
				*/
				
				if(idleM <= idleH && idleM >= 0) {
					mHigh = mMid;
				}else {
					mLow = mMid;
				}
				mMid = (int)Math.ceil((double)(mLow + mHigh)/2);
				
				machineAvailableTimeL = machines.get(mLow).getAvailableTime();
				machineAvailableTimeM = machines.get(mMid).getAvailableTime();
				machineAvailableTimeH = machines.get(mHigh).getAvailableTime();
				
				jobStartTimeL = Integer.max(jobReleaseTime, machineAvailableTimeL);
				jobStartTimeM = Integer.max(jobReleaseTime, machineAvailableTimeM);
				jobStartTimeH = Integer.max(jobReleaseTime, machineAvailableTimeH);
				
				jobCompletionL = jobStartTimeL + jobProcessingTime;
				jobCompletionM = jobStartTimeM + jobProcessingTime;
				jobCompletionH = jobStartTimeH + jobProcessingTime;
				
				if(jobCompletionL <= jobDueTime) {
					idleL = jobStartTimeL - machineAvailableTimeL;
				}else {
					idleL = jobDueTime - jobCompletionL;
				}
				
				if(jobCompletionM <= jobDueTime) {
					idleM = jobStartTimeM - machineAvailableTimeM;
				}else {
					idleM = jobDueTime - jobCompletionM;
				}
				
				if(jobCompletionH <= jobDueTime) {
					idleH = jobStartTimeH - machineAvailableTimeH;
				}else {
					idleH = jobDueTime - jobCompletionH;
				}
				
				if(mMid == mHigh) {
					mIndex = mMid;
				}
			}
		}
	
		return mIndex;
	}
	
	
	
}
