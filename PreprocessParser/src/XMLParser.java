import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class XMLParser {
	
	public static String fileRead = "sample20.xml";
	public static String fileWritten = "sample20.arff";
	
	public static String classA = "Politics";
	public static String classB = "Economy";
	public static String classC = "Social";
	public static String classD = "Technology";
	
	public static String arffRelation = "Sample";
	public static String arffClass = "{" + classA + "," + classB + "," + classC + "," + classD + "}";
	public static String arffID = "id";
	public static String arffIDType = "NUMERIC";
	public static String arffContent = "content";
	public static String arffContentType = "STRING";
	
	public static String xmlID = "<str name=\"id\">";
	public static String xmlContent = "<str name=\"content\">";
	public static String xmlStr = "</str>";
	public static String xmlDoc = "</doc>";
	public static String xmlComma = ",";
	public static String xmlRT = "RT ";
	public static String xmlRt = "Rt ";
	public static String xmlHTTP = "http";
	
	public static void main(String[] args) {
		 
		XMLParser obj = new XMLParser();
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
				
				if (line.contains(xmlID)) {
					line = line.replace(xmlID, "");
					line = line.replace(xmlStr, "");
					line = line.trim();
					
					System.out.print(line + ",");
					bw.write(line + ",");
				}
				if (line.contains(xmlContent)) {
					line = line.replace(xmlContent, "");
					line = line.replace(xmlStr, "");
					line = line.replace(xmlDoc, "");
					line = line.replace(xmlComma, "");
					line = line.replace(xmlRT, "");
					line = line.replace(xmlRt, "");
					line = line.split(xmlHTTP, 2)[0];
					line = line.trim();
					
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