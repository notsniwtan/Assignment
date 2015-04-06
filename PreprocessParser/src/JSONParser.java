import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class JSONParser {
	
	public static String relationName = "training4000";
	
	public static String fileRead = "sample4000.json";
	public static String fileWritten = relationName + ".arff";
	
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
		 
		JSONParser obj = new JSONParser();
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
			
			bw.write("@RELATION\t" + arffRelation + "\n\n");
			
			bw.write("@ATTRIBUTE\tclass\t\t" + arffClass + "\n");
			bw.write("@ATTRIBUTE\t" + arffID + "\t\t\t" + arffIDType + "\n");
			bw.write("@ATTRIBUTE\t" + arffContent + "\t\t" + arffContentType + "\n\n");
			
			bw.write("@DATA\n");
			
			while ((line = br.readLine()) != null) {
				
				if (line.contains(jsonID)) {
					line = line.replace(jsonID, "");
					line = line.replace(jsonIDEnd, "");
					line = line.trim();
					
					System.out.print(line + ",");
					bw.write(line + ",");
				}
				if (line.contains(jsonContent)) {
					// Preprocessing for json content tag
					line = line.replace(jsonContent, "");
					line = line.replace(jsonContentEnd, "");
					// Preprocessing for punctuation
					line = line.replace(jsonComma, "");
					line = line.replace(jsonQuote, "");
					// Preprocessing for twitter content tag
					line = line.replace(jsonRT, "");
					line = line.replace(jsonRt, "");
					// Preprocessing for tweet HTTP link
					line = line.split(jsonHTTP, 2)[0];
					// Preprocessing for json format tag
					line = line.split(jsonFormatEnd, 2)[0];
					// Preprocessing for whitespace
					line = line.trim();
					// Preprocessing for content toLowerCase()
					line = line.toLowerCase();
					
					System.out.println("\'" + line + "\'");
					bw.write("\'" + line + "\'\n");
				}
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