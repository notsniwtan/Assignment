import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class ManualParser {

	public static String relationName = "testSet";

	public static String fileRead = "comparison1.txt";
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

	public static String jsonComma = ",";
	public static String jsonQuote = "'";
	public static String jsonHTTP = "http";

	public static void main(String[] args) {

		ManualParser obj = new ManualParser();
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

				String[] splitStr = line.split(",");

				String contentStr = splitStr[2];
				// Preprocessing for punctuation
				contentStr = contentStr.replace(jsonComma, "");
				contentStr = contentStr.replace(jsonQuote, "");
				// Preprocessing for content toLowerCase()
				contentStr = contentStr.toLowerCase();
				// Preprocessing for http
				contentStr = contentStr.split(jsonHTTP, 2)[0];
				// Preprocessing for whitespace
				contentStr = contentStr.trim();
				splitStr[2] = contentStr;

				bw.write(splitStr[0] + "," + splitStr[1] + ",\'" + splitStr[2] + "\'\n");				
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

		System.out.println("[DEBUG] END OF RUN");
	}

}