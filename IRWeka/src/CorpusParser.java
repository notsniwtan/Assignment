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

	public static final String RELATION_NAME = "testing";
	public static final String FILE_WRITTEN = RELATION_NAME + ".arff";
	public static final String FILE_READ = "corpus.txt";
	public static final String ATTRIBUTE_CLASS = "{Politics,Economy,Social,Technology}";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_ID_TYPE = "NUMERIC";
	public static final String ATTRIBUTE_CONTENT = "content";
	public static final String ATTRIBUTE_CONTENT_TYPE = "STRING";

	public static void main(String[] args) throws IOException, java.text.ParseException, org.json.simple.parser.ParseException{

		int count = 0;
		
		JSONParser parser = new JSONParser();     
		Object obj;
		System.out.println("[DEBUG] PARSING JSON FILE");

		BufferedWriter bw = null;

		try {
			File file = new File(FILE_WRITTEN);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			obj = parser.parse(new FileReader(FILE_READ));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray documentList = (JSONArray) jsonObject.get("Documents");

			//
			bw.write("@RELATION\t" + RELATION_NAME + "\n");
			bw.write("@ATTRIBUTE\tclass\t\t" + ATTRIBUTE_CLASS + "\n");
			bw.write("@ATTRIBUTE\t" + ATTRIBUTE_ID + "\t\t\t" + ATTRIBUTE_ID_TYPE + "\n");
			bw.write("@ATTRIBUTE\t" + ATTRIBUTE_CONTENT + "\t\t" + ATTRIBUTE_CONTENT_TYPE + "\n\n");
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
				// (5) toLowerCase()
				// (6) http				
				// (7) whitespace
				String content = (String) innerObj.get("Content");
				content = content.replace("\n", "");
				content = content.replace("\r", "");
				content = content.replace(",", "");
				content = content.replace("'", "");
				content = content.toLowerCase();
				content = content.replaceAll("http.*", "");
				content = content.trim();

				System.out.println("id:" + id);
				System.out.println("content:" + content);

				bw.write(id + ",");
				bw.write("'" + content + "'\n");

				count++;
			}
			
			System.out.println("\n[DEBUG] COUNT = " + count);

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
