import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class TempParser {
	
	public static String relationName = "temp4000";
	
	public static String fileRead = "sample4000.txt";
	public static String fileWritten = relationName + ".txt";
	
	public static String classA = "Politics";
	public static String classB = "Economy";
	public static String classC = "Social";
	public static String classD = "Technology";
	
	public static String arffRelation = relationName;
	public static String arffClass = "{" + classA + "," + classB + "," + classC + "," + classD + "}";
	public static String arffID = "id";
	public static String arffIDType = "NUMERIC";
	public static String arffContent = "content";
	public static String arffContentType = "STRING";
	
	public static String jsonID = "\"id\":\"";
	public static String jsonContent = "\"content\":\"";
	public static String jsonIDEnd = "\",";
	public static String jsonContentEnd = "\"},";
	public static String jsonComma = ",";
	public static String jsonQuote = "'";
	public static String jsonRT = "RT ";
	public static String jsonRt = "Rt ";
	public static String jsonHTTP = "http";
	public static String jsonFormatEnd = "\"}]";
	
	public static void main(String[] args) {
		 
		TempParser obj = new TempParser();
		obj.run();
	 
	  }
	
	public void run() {
		 
		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = "";
		
		try {
	 
			File file = new File(fileWritten);
			
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			br = new BufferedReader(new FileReader(fileRead));
			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			
			while ((line = br.readLine()) != null) {
				
				String[] arr = line.split(",");
				
				String tempStr = arr[2];
				tempStr = tempStr.replace("'", "");
				arr[2] = tempStr;
				
				bw.write(arr[0] + "," + arr[1] + ",\'" + arr[2].toLowerCase() + "\'\n");				
			}
			
			bw.close();
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
	  }

}