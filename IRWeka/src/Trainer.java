import weka.core.*;
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

public class Trainer {

	public static String classifierType = "naiveBayes";
	public static String trainingSet = "smsspam.small.arff";
	
	public static String trainedClassifier = classifierType + "Classifier.dat";

	public static int fold = 10;

	static Instances trainData;
	StringToWordVector filter;
	static FilteredClassifier classifier;

	public static String text;
	static Instances instances;

	public static void main (String[] args) {

		Trainer trainer;

		System.out.println("Classifier:\t\t" + classifierType);
		System.out.println("TrainingSet:\t\t" + trainingSet);
		System.out.println("TrainedClassifier:\t" + trainedClassifier);

		// Training Classifier
		trainer = new Trainer();
		trainer.loadTrainingSet(trainingSet);
		trainer.evaluateModel();
		trainer.learn();
		trainer.saveModel(trainedClassifier);
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
			System.out.println("Problem found when reading: " + fileName);
		}
	}

	public void evaluateModel() {
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
}