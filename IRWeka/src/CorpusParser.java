import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CorpusParser {

	//public static String fileWritten = "fileWritten.txt";
	public static String fileWritten = "testSet.arff";

	public static void main(String[] args) throws IOException, java.text.ParseException, org.json.simple.parser.ParseException{

		JSONParser parser = new JSONParser();     
		Object obj;
		System.out.println("Parsing JSON file");

		BufferedWriter bw = null;

		try {
			File file = new File(fileWritten);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			obj = parser.parse(new FileReader("corpus.txt"));
			JSONObject jsonObject = (JSONObject) obj;
			String name = (String) jsonObject.get("Name");
			JSONArray documentList = (JSONArray) jsonObject.get("Documents");

			//
			bw.write("@RELATION\ttestSet\n");
			bw.write("@ATTRIBUTE\tclass\t\t{Politics,Economy,Social,Technology}\n");
			bw.write("@ATTRIBUTE\tid\t\t\tNUMERIC\n");
			bw.write("@ATTRIBUTE\tcontent\t\tSTRING\n\n");
			bw.write("@DATA\n");
			//

			Iterator<JSONObject> iterator = documentList.iterator();

			while(iterator.hasNext()){
				JSONObject innerObj = (JSONObject) iterator.next();

				String id = String.valueOf(innerObj.get("Id"));

				// Preprocess to remove
				// (1) /n
				// (2) /r
				// (3) comma
				// (4) singleQuote
				// (5) whitespace
				// (6) toLowerCase()
				String content = (String) innerObj.get("Content");
				content = content.replace("\n", "");
				content = content.replace("\r", "");
				content = content.replace(",", "");
				content = content.replace("'", "");
				content = content.toLowerCase();
				content = content.trim();

				System.out.println("id:" + id);
				System.out.println("content:" + content);



				bw.write(id + ",");
				bw.write(content + "\n");

			}

			bw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("[DEBUG] END OF RUN");

	}
}
