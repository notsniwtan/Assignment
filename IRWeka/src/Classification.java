import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import java.util.Random;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.CSVLoader;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;

public class Classification {

	public static String trainingSet = "TrainingSet.arff";
	public static String testSet = "TestingSet.arff";
	public static String trainedClassifier = "trainedClassifier.dat";
	public static String relationName = "testRelation";

	public static int fold = 10;

	static Instances trainData;
	static Instances testData;
	StringToWordVector filter;
	static FilteredClassifier classifier;

	public static String text;
	static Instances instances;

	public static void main (String[] args) {

		Classification svmClassification;

		
		System.out.println("Classification:\t\tSVM");
		System.out.println("TrainingSet:\t\t" + trainingSet);
		System.out.println("TrainedClassifier:\t" + trainedClassifier);
		System.out.println("TestSet:\t\t" + testSet + "\n");

		// Training Classifier
		svmClassification = new Classification();
		svmClassification.loadTrainingSet(trainingSet);
		svmClassification.loadTestSet(testSet);
		svmClassification.learn();
		svmClassification.evaluateModel();
	
		//svmClassification.saveModel(trainedClassifier);

		// Classifying with trainedClassifier
	//	svmClassification.load(testSet);
/*		svmClassification.loadModel(trainedClassifier);
		svmClassification.makeInstance();
		svmClassification.classify();
*/		
		//load classifier
	//	svmClassification.loadModel(trainedClassifier);		
		
		//setup the attributes for the instance
		// Create the attributes, class and text
		/*FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("Technology");
		fvNominalVal.addElement("Politics");
		fvNominalVal.addElement("Economy");
		fvNominalVal.addElement("Social");
		Attribute classAttr = new Attribute("class", fvNominalVal);
		Attribute contentAttr = new Attribute("content",(FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(classAttr);
		fvWekaAttributes.addElement(contentAttr);
		instances = new Instances(relationName, fvWekaAttributes, 1);
		instances.setClassIndex(0);
		
		Instance current = new DenseInstance(2);
		current.setDataset(instances);
		// Multiple line classification
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testSet));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
				current.setValue(contentAttr, line);
				double pred = classifier.classifyInstance(current);
				System.out.println(instances.classAttribute().value((int) pred) + ",");
				System.out.print(line + "\n");
			}
			reader.close();
		}
		catch (Exception e) {
			System.out.println("Problem found when reading: " + e);
		}*/
	}

	public void loadTrainingSet(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
			System.out.println("===== Loaded TrainingSet: " + fileName + " =====");
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + e);
		}
	}
	
	public void loadTestSet(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			testData = arff.getData();
			System.out.println("===== Loaded TestSet: " + fileName + " =====");
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + e);
		}
	}

	public void evaluateModel() {
		try {
			testData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			StringBuffer predsBuffer = new StringBuffer();
			PlainText pt = new PlainText();
			pt.setHeader(testData);
			pt.setBuffer(predsBuffer);
			/*classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			classifier.setClassifier(new NaiveBayes());*/
			Evaluation eval = new Evaluation(trainData);
			eval.evaluateModel(classifier, testData,pt);
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(predsBuffer.toString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when evaluating: "+e);
		}
	}

	public void learn() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setIDFTransform(true);
			filter.setTFTransform(true);
			filter.setStopwords(new File("stopwords.txt"));
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			//classifier.setClassifier(new SMO());
			//classifier.setClassifier(new MultilayerPerceptron());
			classifier.setClassifier(new NaiveBayes());
			classifier.buildClassifier(trainData);
			// Uncomment to see the classifier
			// System.out.println(classifier);
			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when training");
		}
	}

	public void saveModel(String fileName) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
			out.writeObject(classifier);
			out.close();
			System.out.println("===== Saved TrainedClassifier: " + fileName + " =====");
		}
		catch (IOException e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}

	public void load(String fileName) {
		try {
			/*BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
				text = text + " " + line;
			}*/
			//BufferedReader reader = new BufferedReader(new FileReader(fileName));
			CSVLoader csv = new CSVLoader();
			csv.setSource(new File(testSet));
			testData = csv.getDataSet();
			System.out.println("\n===== Loaded TestSet: " + fileName + " =====");
			//reader.close();
			System.out.println(text);
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + e);
		}
	}

	public void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			Object tmp = in.readObject();
			classifier = (FilteredClassifier) tmp;
			in.close();
			System.out.println("===== Loaded TrainedClassifier: " + fileName + " =====");
		}
		catch (Exception e) {
			// Given the cast, a ClassNotFoundException must be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
	}


}


