package Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DaySplitGoogleJobs {
	
	public DaySplitGoogleJobs() {
		
	}
	
	public static void splitDay(int dayNr, String readFolderPath, String readFileName, String readFileExtension, String writeFolderPath, String writeFileName, String writeFileExtension) {
		String readFileAddress = readFolderPath + readFileName + readFileExtension;
		String writeFileAddress = writeFolderPath + writeFileName + dayNr + writeFileExtension;
		
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(readFileAddress));
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(writeFileAddress));
			String readLine = "";
			while((readLine = bReader.readLine())!=null) {
				String[] lineContents = readLine.split("\t");
				
				int jobSubmitDay = Integer.parseInt(lineContents[9]);
				
				if(jobSubmitDay == dayNr) {
					String writeString = readLine + "\n";
					bWriter.write(writeString);
				}
			}
			bReader.close();
			bWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
