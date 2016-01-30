package com.elromantico.client.gestures.classifier;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elromantico.client.gestures.Gesture;
import com.elromantico.client.gestures.classifier.featureExtraction.IFeatureExtractor;

public class GestureClassifier {

	protected List<Gesture> trainingSet = Collections.emptyList();
	protected IFeatureExtractor featureExtractor;
	protected String activeTrainingSet = "";
	private final Context context;

	public GestureClassifier(IFeatureExtractor fE, Context context) {
		trainingSet = new ArrayList<>();
		featureExtractor = fE;
		this.context = context;
	}


	@SuppressWarnings("unchecked")
	public void loadTrainingSet(String trainingSetName) {
		if (!trainingSetName.equals(activeTrainingSet)) {
			activeTrainingSet = trainingSetName;
			FileInputStream input;
			ObjectInputStream o;
			try {
				input = new FileInputStream(new File(context.getExternalFilesDir(null), activeTrainingSet + ".gst"));
				o = new ObjectInputStream(input);
				trainingSet = (ArrayList<Gesture>) o.readObject();
				try {
					o.close();
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				trainingSet = new ArrayList<>();
			}
		}
	}

	public Distribution classifySignal(String trainingSetName, Gesture signal) {
		if (trainingSetName == null) {
			System.err.println("No Training Set Name specified");
			trainingSetName = "default";
		}
		if (!trainingSetName.equals(activeTrainingSet)) {
			loadTrainingSet(trainingSetName);
		}

		Distribution distribution = new Distribution();
		Gesture sampledSignal = featureExtractor.sampleSignal(signal);

		for (Gesture s : trainingSet) {
			double dist = DTWAlgorithm.calcDistance(s, sampledSignal);
			distribution.addEntry(s.getLabel(), dist);
		}
		if (trainingSet.isEmpty()) {
			System.err.printf("No training data for trainingSet %s available.\n", trainingSetName);
		}

		return distribution;
	}

}