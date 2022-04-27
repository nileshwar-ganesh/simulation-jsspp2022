package Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import Jobs.Job;

public class GenStatisticalTrace {
	
	public GenStatisticalTrace() {
		
	}
	
	public static void generateTrace(int dayNr, int jobCoreLimit, double slack, double denominator) {
		double slackLL = 0.1;
		double slackUL = 0.9;
		double denomLL = 2.0;
		double denomUL = 3.01;//3.01
		
		String readFolderPathG = "./src/GoogleTrace/GoogleTraceD/";
		String readFileNameG = "GoogleTraceD";
		String readFileExtensionG = ".txt";
		
		String readFolderPathL = "./src/GoogleTrace/LognormalSlacks/";
		String readFileNameL = "Lognormal2";
		String readFileExtensionL = ".txt";
		
		String writeFolderPath = "./src/StatisticalTraceC" + jobCoreLimit + "/Day" + dayNr + "/";
		String writeFileName = "GoogleTraceD";
		String writeFileExtension = ".txt";
		
		double standardDeviation = Math.round(slack/denominator *1000d)/1000d;
						
		LinkedList<Job> jobs = new LinkedList<Job>();
				
		String readFileAddressG = readFolderPathG + readFileNameG + dayNr + readFileExtensionG;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(readFileAddressG));
			int lineNr = 0;
			String readLine = "";
			while((readLine = bReader.readLine()) != null) {
				String[] lineContents = readLine.split("\t");
				String status = lineContents[10];
				int jobCore = Integer.parseInt(lineContents[11]); 
				if(status.equalsIgnoreCase("Accept") && jobCore <= jobCoreLimit) {
					String jobID = lineContents[0];
					int jobProcessingTime = Integer.parseInt(lineContents[3]);
					int jobReleaseTime = Integer.parseInt(lineContents[8]);
					int jobDueTime = 0;
					Job job = new Job(jobID, jobProcessingTime, jobReleaseTime, jobDueTime, jobCore);
					jobs.add(job);
				}
			}
			bReader.close();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		for(int set=1; set<=10; set++) {
			System.out.println("GEnerating for set " + set);
			String readFileAddressL = readFolderPathL + readFileNameL + "M" + slack + "SD" + standardDeviation + "S" + set +  readFileExtensionL;
			ArrayList<Double> lognormalSlacks = new ArrayList<Double>();
			try {
				BufferedReader slackReader =  new BufferedReader(new FileReader(readFileAddressL));
				String readLine = "";
				while((readLine = slackReader.readLine())!=null) {
					lognormalSlacks.add(Double.parseDouble(readLine));
				}
				slackReader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			String writeFileAddress = writeFolderPath + writeFileName + dayNr + "M" + slack + "SD" + standardDeviation + "C" + jobCoreLimit + "S" + set + writeFileExtension;
			try {
				BufferedWriter traceWriter = new BufferedWriter(new FileWriter(writeFileAddress));
				for(int n=0; n<jobs.size(); n++ ) {
					String jobID = jobs.get(n).getJobID();
					int jobProcessingTime = jobs.get(n).getProcessingTime();
					int jobReleaseTime = jobs.get(n).getReleaseTime();
					int jobDueTime = (int) ((jobReleaseTime) + ((1 + lognormalSlacks.get(n))* jobProcessingTime));
					int jobCore = jobs.get(n).getJobCore();
					String writeString = jobID + ";" + jobProcessingTime + ";" + jobReleaseTime + ";" + jobDueTime + ";" + jobCore + ";" + lognormalSlacks.get(n)+";\n";
					traceWriter.write(writeString);
				}
				traceWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
