import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XMLParser {
	
	public static String fileName = "sample.xml";
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
		String line = "";
	 
		try {
	 
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
				
				line = line.trim();
				
				if (line.contains(xmlID)) {
					line = line.replace(xmlID, "");
					line = line.replace(xmlStr, "");
					System.out.print(line + ",");
				}
				if (line.contains(xmlContent)) {
					line = line.replace(xmlContent, "");
					line = line.replace(xmlStr, "");
					line = line.replace(xmlDoc, "");
					line = line.replace(xmlComma, "");
					line = line.replace(xmlRT, "");
					line = line.replace(xmlRt, "");
					line = line.split(xmlHTTP, 2)[0];
					System.out.println(line);
				}
			}
	 
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