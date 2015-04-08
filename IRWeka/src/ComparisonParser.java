import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ComparisonParser {

	public static String fileReadOne = "comparison1.txt";
	public static String fileReadTwo = "comparison2.txt";
	public static String fileWritten = "comparisonResults.txt";

	public static final String CLASS_ONE = "Politics";
	public static final String CLASS_TWO = "Economy";
	public static final String CLASS_THREE = "Social";
	public static final String CLASS_FOUR = "Technology";

	public static void main(String[] args) {

		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = "";

		ArrayList<String> compareOne = new ArrayList<String>();
		ArrayList<String> compareTwo = new ArrayList<String>();

		int countOne = 0;
		int countTwo = 0;
		int x = 0;
		int y = 0;
		int lineCount = 0;

		int comparisonTable[][] = new int[4][4];

		for (int i=0 ; i < 4 ; i++) {
			for (int j=0 ; j < 4 ; j++) {
				comparisonTable[i][j] = 0;
			}
		}

		try {

			File file = new File(fileWritten);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			br = new BufferedReader(new FileReader(fileReadOne));

			while ((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				compareOne.add(lineSplit[0]);
				countOne++;
			}
			System.out.println("[DEBUG] CountOne = " + countOne);

			br = new BufferedReader(new FileReader(fileReadTwo));

			while ((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				compareTwo.add(lineSplit[0]);
				countTwo++;
			}
			System.out.println("[DEBUG] CountTwo = " + countTwo);

			if (compareOne.size() != compareTwo.size()) {
				System.out.println("[DEBUG] COMPARESIZE ERROR");
			}

			for (int i=0 ; i < compareTwo.size() ; i++) {

				switch (compareOne.get(i)) {
				case CLASS_ONE:
					x = 0;
					break;
				case CLASS_TWO:
					x = 1;
					break;
				case CLASS_THREE:
					x = 2;
					break;
				case CLASS_FOUR:
					x = 3;
					break;
				default:
					System.out.println("[DEBUG] COMPAREONE ERROR");
					break;
				}

				switch (compareTwo.get(i)) {
				case CLASS_ONE:
					y = 0;
					break;
				case CLASS_TWO:
					y = 1;
					break;
				case CLASS_THREE:
					y = 2;
					break;
				case CLASS_FOUR:
					y = 3;
					break;
				default:
					System.out.println("[DEBUG] COMPARETWO ERROR");
					break;
				}

				comparisonTable[x][y]++;

				if (x != y) {
					System.out.println("[DEBUG] Different = " + lineCount);
					bw.write(lineCount + 1 + "\n");
				}
				lineCount++;
			}

			System.out.println("[DEBUG] lineCount = " + lineCount);

			System.out.print("Table\t\t");
			System.out.print(CLASS_ONE + "\t");
			System.out.print(CLASS_TWO + "\t\t");
			System.out.print(CLASS_THREE + "\t\t");
			System.out.print(CLASS_FOUR + "\n");

			System.out.print(CLASS_ONE + "\t");

			bw.write("Table\t\t");
			bw.write(CLASS_ONE + "\t");
			bw.write(CLASS_TWO + "\t\t");
			bw.write(CLASS_THREE + "\t\t");
			bw.write(CLASS_FOUR + "\n");

			bw.write(CLASS_ONE + "\t");

			for (int i=0 ; i < 4 ; i++) {
				for (int j=0 ; j < 4 ; j++) {
					System.out.print(comparisonTable[i][j] + "\t\t");
					bw.write(comparisonTable[i][j] + "\t\t");
				}
				System.out.print("\n");
				if (i==0) {
					System.out.print(CLASS_TWO + "\t\t");
					bw.write(CLASS_TWO + "\t\t");
				}
				else if (i==1) {
					System.out.print(CLASS_THREE + "\t\t");
					bw.write(CLASS_THREE + "\t\t");
				}
				else if (i==2) {
					System.out.print(CLASS_FOUR + "\t");
					bw.write(CLASS_FOUR + "\t");
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

		System.out.println("[DEBUG] END OF RUN");
	} 
}
