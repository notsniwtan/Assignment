import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVParser {
	
	public static String fileName = "sample.csv";
	public static String punctuationSymbol = ",";
	
	public static void main(String[] args) {
		 
		CSVParser obj = new CSVParser();
		obj.run();
	 
	  }
	
	public void run() {
		 
		BufferedReader br = null;
		String line = "";
	 
		try {
	 
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
	 
			    // use comma as separator
				String[] splitLine = line.split(punctuationSymbol);
	 
				System.out.println(splitLine[0] + "\t" + splitLine[1]);
	 
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
