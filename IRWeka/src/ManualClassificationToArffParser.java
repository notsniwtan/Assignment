import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.porterStemmer;

import weka.core.Stopwords;
import weka.core.tokenizers.WordTokenizer;

public class ManualClassificationToArffParser {

	public static final String RELATION_NAME = "TestingSet";
	//public static final String RELATION_NAME = "TrainingSet";

	public static final String FILE_READ = "comparison1.txt";
	//public static final String FILE_READ = "UnprocessedTrainingSet.txt";
	public static final String FILE_WRITTEN = RELATION_NAME + ".arff";

	public static final String CLASS_A = "Politics";
	public static final String CLASS_B = "Economy";
	public static final String CLASS_C = "Social";
	public static final String CLASS_D = "Technology";

	public static final String ARFF_CLASS = "{" + CLASS_A + "," + CLASS_B + "," + CLASS_C + "," + CLASS_D + "}";
	public static final String ARFF_ID = "id";
	public static final String ARFF_ID_TYPE = "NUMERIC";
	public static final String ARFF_CONTENT = "content";
	public static final String ARFF_CONTENT_TYPE = "STRING";

	public static void main(String[] args) {

		ManualClassificationToArffParser obj = new ManualClassificationToArffParser();
		obj.run();

	}

	public void run() {

		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = "";
		
		List duplicatesList = new ArrayList();
		
		try {

			File file = new File(FILE_WRITTEN);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			Stopwords stopWords = new Stopwords();
			System.out.println("stop words: "+stopWords);
			stopWords.clear();
			System.out.println("stop words: "+stopWords);
			stopWords.read("stopwords.txt");
			System.out.println("stop words: "+stopWords);
		
			
			porterStemmer stem = new porterStemmer();
			//stem.setStemmer("english");
			
			br = new BufferedReader(new FileReader(FILE_READ));
			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			bw.write("@RELATION\t" + RELATION_NAME + "\n\n");

			bw.write("@ATTRIBUTE\tclass\t\t" + ARFF_CLASS + "\n");
			bw.write("@ATTRIBUTE\t" + ARFF_ID + "\t\t\t" + ARFF_ID_TYPE + "\n");
			bw.write("@ATTRIBUTE\t" + ARFF_CONTENT + "\t\t" + ARFF_CONTENT_TYPE + "\n\n");

			bw.write("@DATA\n");

			while ((line = br.readLine()) != null) {

				String[] splitStr = line.split(",");

				String content = splitStr[2];
				
				// Preprocess to remove
				// (1) /n
				// (2) /r
				// (3) comma
				// (4) singleQuote
				// (5) toLowerCase()
				// (6) http				
				// (7) whitespace
				content = content.replace("\\n", " ");
				content = content.replace("\r", "");
				content = content.replace(",", "");
				content = content.replace("'", "");				
				content = content.toLowerCase();
				content = content.replaceAll("http.*", "");
				// Preprocessing for anything besides whitespace and alphabets
				String pattern = "[^a-zA-Z\\s@]";
				content = content.replaceAll(pattern, "");
				// Preprocessing for @users
				String userPattern = "@[a-zA-Z0-9]*";
				content = content.replaceAll(userPattern, "");
				content = content.trim();
				
				
				WordTokenizer t = new WordTokenizer();
				t.tokenize(content);
				StringBuffer sb = new StringBuffer();
				while(t.hasMoreElements()){
					String s = (String) t.nextElement();
					System.out.println(s);
					if(stopWords.is(s)){
						System.out.println("stop word: "+s);
						continue;
					}
					System.out.println("before stem: "+s);
					stem.setCurrent(s);
					stem.stem();
					s=stem.getCurrent();
					System.out.println("after stem: "+s);
					sb.append(s+ " ");
				}
				
				content = sb.toString();
				content = content.trim();
				
				//detect duplicates
				if(duplicatesList.contains(content)){
					continue;
				}
				else{
					duplicatesList.add(content);
				}
				splitStr[2] = content;

				bw.write(splitStr[0] + "," + splitStr[1] + ",\'" + splitStr[2] + "\'\n");				
			}

			bw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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