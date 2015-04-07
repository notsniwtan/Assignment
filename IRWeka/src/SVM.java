import weka.core.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;
import java.util.Random;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffLoader.ArffReader;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

import weka.classifiers.bayes.NaiveBayes;

public class SVM {

	public static String trainingSet = "smsspam.small.arff";
	public static String testSet = "smstest.txt";
	public static String trainedClassifier = "trainedClassifier.dat";
	public static String relationName = "testRelation";

	public static int fold = 10;

	Instances trainData;
	StringToWordVector filter;
	FilteredClassifier classifier;

	public static String text;
	Instances instances;

	public static void main (String[] args) {

		SVM svmClassification;

		System.out.println("Classification:\t\tSVM");
		System.out.println("TrainingSet:\t\t" + trainingSet);
		System.out.println("TrainedClassifier:\t" + trainedClassifier);
		System.out.println("TestSet:\t\t" + testSet + "\n");

		// Training Classifier
		svmClassification = new SVM();
		svmClassification.loadDataset(trainingSet);
		svmClassification.evaluate();
		svmClassification.learn();
		svmClassification.saveModel(trainedClassifier);

		// Classifying with trainedClassifier
/*		svmClassification.load(testSet);
		svmClassification.loadModel(trainedClassifier);
		svmClassification.makeInstance();
		svmClassification.classify();
*/		
		svmClassification.loadModel(trainedClassifier);
		// Multiple line classification
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testSet));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
				text = line;
				svmClassification.makeInstance();
				svmClassification.classify();
				System.out.print(line + "\n");
			}
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + testSet);
		}
	}

	public void loadDataset(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
			System.out.println("===== Loaded TrainingSet: " + fileName + " =====");
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
		}
	}

	public void evaluate() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			classifier.setClassifier(new NaiveBayes());
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, fold, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when evaluating");
		}
	}

	public void learn() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
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
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
				text = text + " " + line;
			}
			System.out.println("\n===== Loaded TestSet: " + fileName + " =====");
			reader.close();
			System.out.println(text);
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
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

	public void makeInstance() {
		// Create the attributes, class and text
		FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("spam");
		fvNominalVal.addElement("ham");
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		Attribute attribute2 = new Attribute("text",(FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		instances = new Instances(relationName, fvWekaAttributes, 1);
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance
		DenseInstance instance = new DenseInstance(2);
		instance.setValue(attribute2, text);
		// Another way to do it:
		// instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
		instances.add(instance);
//		System.out.println("===== Instance created with reference dataset =====");
//		System.out.println(instances);
	}

	public void classify() {
		try {
			double pred = classifier.classifyInstance(instances.instance(0));
//			System.out.println("===== Classified instance =====");
//			System.out.println("Class predicted: " + instances.classAttribute().value((int) pred));
			System.out.print(instances.classAttribute().value((int) pred) + ",");
		}
		catch (Exception e) {
			System.out.println("Problem found when classifying the text");
		}
	}
}