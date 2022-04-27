package Data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import Jobs.Job;

public class ReadGoogleJobs {
	
	public ReadGoogleJobs() {
		
	}
	
	public LinkedList<Job> readTraceJobs(String readFile){
		LinkedList<Job> jobs = new LinkedList<Job>();
		
		try {
			//Opening buffered reader/file reader to read from a txt file
			BufferedReader bReader = new BufferedReader(new FileReader(readFile));
			//System.out.println("Loading data...");
			String rLine;
			int totalProcessingTime = 0;
			while((rLine = bReader.readLine())!= null){
				//Splits the line at ;
				String[] lineContents = rLine.split(";");
					
				//Reading job properties from refined file
				String jobID = lineContents[0];
				int processingTime = Integer.parseInt(lineContents[1]);
				int releaseTime = Integer.parseInt(lineContents[2]);
				int dueTime = Integer.parseInt(lineContents[3]);
				int jobCore = Integer.parseInt(lineContents[4]);
				
				Job job = new Job(jobID, processingTime, releaseTime, dueTime, jobCore);
				
				//creating a linked list out of job element
				jobs.add(job);
			}
			bReader.close();
			//System.out.println("Data successfully read!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//returning the job linked list
		return jobs;
	}

}
