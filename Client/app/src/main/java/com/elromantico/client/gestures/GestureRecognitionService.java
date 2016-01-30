package com.elromantico.client.gestures;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elromantico.client.gestures.classifier.Distribution;
import com.elromantico.client.gestures.classifier.GestureClassifier;
import com.elromantico.client.gestures.classifier.featureExtraction.NormedGridExtractor;
import com.elromantico.client.gestures.recorder.GestureRecorder;
import com.elromantico.client.gestures.recorder.GestureRecorderListener;

public class GestureRecognitionService extends Service implements GestureRecorderListener {

    public class GestureRecognitionServiceBinder extends Binder {

        public GestureRecognitionService getService() {
            return GestureRecognitionService.this;
        }
    }

    private final IBinder binder = new GestureRecognitionServiceBinder();

    GestureRecorder recorder;
    GestureClassifier classifier;
    String activeTrainingSet;
    String activeLearnLabel;

    Set<GestureRecognitionListener> listeners = new HashSet<>();

    public void deleteTrainingSet(String trainingSetName) {
        if (classifier.deleteTrainingSet(trainingSetName)) {
            for (GestureRecognitionListener listener : listeners) {
                listener.onTrainingSetDeleted(trainingSetName);
            }
        }
    }

    public void registerListener(GestureRecognitionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void startClassificationMode(String trainingSetName) {
        activeTrainingSet = trainingSetName;
        recorder.start();
        classifier.loadTrainingSet(trainingSetName);
    }

    public void unregisterListener(GestureRecognitionListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            stopClassificationMode();
        }
    }

    public void stopClassificationMode() {
        recorder.stop();
    }

    public IBinder onBind(Intent intent) {
        recorder.registerListener(this);
        return binder;
    }

    @Override
    public void onCreate() {
        recorder = new GestureRecorder(this);
        classifier = new GestureClassifier(new NormedGridExtractor(), this);
        super.onCreate();
    }

    @Override
    public void recognizeGesture(List<float[]> values) {
        recorder.pause(true);
        Distribution distribution = classifier.classifySignal(activeTrainingSet, new Gesture(values, null));
        recorder.pause(false);
        if (distribution != null && distribution.size() > 0) {
            for (GestureRecognitionListener listener : listeners) {
                listener.onGestureRecognized(distribution);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        recorder.unregisterListener(this);
        return super.onUnbind(intent);
    }

}
