import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import java.util.Random;

import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.CSVLoader;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.WordTokenizer;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;

public class Classification {

	public static String trainingSet = "TrainingSet.arff";
	public static String testSet = "TestingSet.arff";
	public static String trainedClassifier = "trainedClassifier.dat";
	public static String relationName = "testRelation";
	static Classifier cls;

	public static int fold = 10;

	static Instances trainData;
	static Instances testData;
	StringToWordVector filter;
	static FilteredClassifier classifier;
	static AdaBoostM1 adaBoost; 

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
	//	svmClassification.learn();
		svmClassification.evaluateModel();
	
		//svmClassification.saveModel(trainedClassifier);

		// Classifying with trainedClassifier
	//	svmClassification.load(testSet);
		//svmClassification.loadModel(trainedClassifier);
		//svmClassification.makeInstance();
		//svmClassification.classify();
		
		//load classifier
	//	svmClassification.loadModel(trainedClassifier);		
		
		//setup the attributes for the instance
		// Create the attributes, class and text
		FastVector fvNominalVal = new FastVector(4);
		fvNominalVal.addElement("Technology");
		fvNominalVal.addElement("Politics");
		fvNominalVal.addElement("Economy");
		fvNominalVal.addElement("Social");
		Attribute classAttr = new Attribute("class", fvNominalVal);
		Attribute contentAttr = new Attribute("content",(FastVector) null);
		Attribute idAttr = new Attribute("id");
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(3);
		fvWekaAttributes.addElement(classAttr);
		fvWekaAttributes.addElement(idAttr);
		fvWekaAttributes.addElement(contentAttr);
		instances = new Instances(relationName, fvWekaAttributes, 10);
		instances.setClassIndex(0);
		
		Instance current = new DenseInstance(3);
		current.setValue(idAttr, 1);
		current.setValue(contentAttr, "facebook data privaci case hellow compani");
		current.setDataset(instances);
		current.setClassMissing();
		//current.setDataset(instances);
		// Multiple line classification
		try {
			//BufferedReader reader = new BufferedReader(new FileReader(testSet));
			//String line;
			text = "google";
			//while ((line = reader.readLine()) != null) {
				
				double pred = cls.classifyInstance(current);
				System.out.println(instances.classAttribute().value((int) pred) + ",");
			//	System.out.print(line + "\n");
			//}
			//reader.close();
		}
		catch (Exception e) {
			System.out.println("Problem found when reading: " + e);
		}
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
			trainData.setClassIndex(0);
			testData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			filter.setDoNotOperateOnPerClassBasis(true);
			filter.setIDFTransform(true);
			filter.setTFTransform(true);
			StringBuffer predsBuffer = new StringBuffer();
			PlainText pt = new PlainText();
			pt.setHeader(trainData);
			pt.setBuffer(predsBuffer);
			classifier = new FilteredClassifier();
			/*SMO svm = new SMO();
			PolyKernel pk = new PolyKernel();
			pk.setExponent(1);
			svm.setC(0.19); svm.setKernel(pk);
			classifier.setClassifier(svm);*/

			classifier.setDebug(true);
		//	classifier.setClassifier(new PART());
		//	classifier.setClassifier(new J48());
		//	classifier.setClassifier(new NaiveBayes());
			classifier.setClassifier(new NaiveBayesMultinomial());
			classifier.setFilter(filter);
			classifier.buildClassifier(trainData);
			System.out.println("done");
		
			StringToWordVector f2 = new StringToWordVector();
			f2.setAttributeIndices("last");
			FilteredClassifier c2 = new FilteredClassifier();
			SMO svm = new SMO();
			PolyKernel pk = new PolyKernel();
			pk.setExponent(1);
			svm.setC(1); svm.setKernel(pk);
			c2.setClassifier(svm);
			c2.setFilter(f2);
			//c2.buildClassifier(trainData);
			
			adaBoost = new AdaBoostM1();
			adaBoost.setClassifier(c2);
			adaBoost.setNumIterations(6);
			adaBoost.setDebug(true);
			adaBoost.buildClassifier(trainData);
			saveModel(trainedClassifier);
			
			/*Bagging bagging = new Bagging();
			bagging.setClassifier(classifier);
			bagging.setBagSizePercent(100);
			bagging.setSeed(9);
			bagging.setNumIterations(30);
			bagging.setDebug(true);
			bagging.buildClassifier(trainData);*/
			
			/*Vote v = new Vote();
			SelectedTag st = new SelectedTag(v.MAJORITY_VOTING_RULE,v.TAGS_RULES);
			v.setCombinationRule(st);
			Classifier[] classa = new Classifier[]{classifier,c2,c,c2,classifier};
			v.setClassifiers(classa);
			v.buildClassifier(trainData);*/
			
			Evaluation eval = new Evaluation(trainData);
			eval.evaluateModel(adaBoost, testData,pt);
			//eval.crossValidateModel(v, trainData, 3, new Random(9), pt);
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			//System.out.println(predsBuffer.toString());
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
			//filter.setIDFTransform(true);
			//filter.setTFTransform(true);
			//filter.setStopwords(new File("stopwords.txt"));
			//filter.setStemmer(new SnowballStemmer());
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			//classifier.setClassifier(new SMO());
			//classifier.setClassifier(new MultilayerPerceptron());
			classifier.setClassifier(new NaiveBayes());
			//classifier.setClassifier(new NaiveBayesMultinomialText());
			//classifier.setClassifier(new NaiveBayesMultinomial());
			//classifier.setClassifier(new J48());
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
			out.writeObject(adaBoost);
			out.flush();
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
			cls = (Classifier) weka.core.SerializationHelper.read(trainedClassifier);
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


