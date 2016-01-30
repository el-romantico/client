package com.elromantico.client.gestures;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashSet;
import java.util.Set;

import com.elromantico.client.gestures.classifier.Distribution;
import com.elromantico.client.gestures.classifier.GestureClassifier;
import com.elromantico.client.gestures.classifier.featureExtraction.NormedGridExtractor;
import com.elromantico.client.gestures.GestureRecorder.GestureRecorderHandler;

public class GestureRecognitionService extends Service implements GestureRecorderHandler {

    public interface GestureRecognitionHandler {

        void handle(Distribution distribution);
    }

    public class GestureRecognitionServiceBinder extends Binder {

        public GestureRecognitionService getService() {
            return GestureRecognitionService.this;
        }
    }

    private final IBinder binder = new GestureRecognitionServiceBinder();

    private GestureRecorder recorder;
    private GestureClassifier classifier;
    private String activeTrainingSet;
    private Set<GestureRecognitionHandler> handlers = new HashSet<>();

    public void registerListener(GestureRecognitionHandler listener) {
        if (listener != null) {
            handlers.add(listener);
        }
    }

    public void startClassificationMode(String trainingSetName) {
        activeTrainingSet = trainingSetName;
        recorder.start();
        classifier.loadTrainingSet(trainingSetName);
    }

    public void unregisterListener(GestureRecognitionHandler handler) {
        handlers.remove(handler);
        if (handlers.isEmpty()) {
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
    public void handle(float[][] values) {
        recorder.pause(true);
        Distribution distribution = classifier.classifySignal(activeTrainingSet, new Gesture(values, null));
        recorder.pause(false);
        if (distribution != null && distribution.size() > 0) {
            for (GestureRecognitionHandler handler : handlers) {
                handler.handle(distribution);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        recorder.unregisterListener();
        return super.onUnbind(intent);
    }
}
