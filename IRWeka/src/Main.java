import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.filters.unsupervised.attribute.StringToWordVector;



public class Main {
	public static Instances trainData;
	public static StringToWordVector filter;
	public static FilteredClassifier classifier;
	public static void main(String[] args){
		
		Instances instances;
		
		//for classifying
		// Create the attributes, class and text
		/*FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("technology");
		fvNominalVal.addElement("politics");
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		Attribute attribute2 = new Attribute("text",(FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		instances = new Instances("Test relation", fvWekaAttributes, 1);
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance
		DenseInstance instance = new DenseInstance(2);
		instance.setValue(attribute2, "testtest");
		// Another way to do it:
		// instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
		instances.add(instance);
		
		
		instance .setValue(attribute2, "helhelhel");
		instances.add(instance);
		
		//System.out.println("===== Instance created with reference dataset =====");
		System.out.println(instances);
		
		
		
		
		
		instances.setClassIndex(0);
		filter = new StringToWordVector();
		filter.setAttributeIndices("last");
		classifier = new FilteredClassifier();
		classifier.setFilter(filter);
		classifier.setClassifier(new NaiveBayes());
		try {
			classifier.buildClassifier(instances);
			// Uncomment to see the classifier
			 System.out.println(classifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		loadTrainingSet("sample20.arff");
		//evaluate();
		learn();
	}
	
	public static void evaluate() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			//ANN - Machine Learning Based
			MultilayerPerceptron ann = new MultilayerPerceptron();
			
			classifier.setClassifier(ann);
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 4, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
			}
			catch (Exception e) {
			System.out.println("Problem found when evaluating");
			}
		}
	
	public static void learn() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			//ANN - Machine Learning Based
			MultilayerPerceptron ann = new MultilayerPerceptron();
			
			classifier.setClassifier(ann);
			classifier.buildClassifier(trainData);
			// Uncomment to see the classifier
			 System.out.println(classifier);
			System.out.println("===== Training on filtered (training) dataset done =====");
			}
			catch (Exception e) {
			System.out.println("Problem found when training");}
	}
	
	public static void loadTrainingSet(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			
			trainData = arff.getData();
			System.out.println("===== Loaded dataset: " + fileName + " =====");
			reader.close();
			}
			catch (IOException e) {
			System.out.println("Problem found when reading: " + e);
			}
		}

	
}
