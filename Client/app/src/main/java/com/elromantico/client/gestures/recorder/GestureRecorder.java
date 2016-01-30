package com.elromantico.client.gestures.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Arrays;

public class GestureRecorder implements SensorEventListener {

    SensorManager sensorManager;

    Context context;
    GestureRecorderListener listener;
    CircularFifoQueue<float[]> currentValues;

    public GestureRecorder(Context context) {
        this.context = context;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] value = {
                sensorEvent.values[SensorManager.DATA_X],
                sensorEvent.values[SensorManager.DATA_Y],
                sensorEvent.values[SensorManager.DATA_Z]
        };

        if (currentValues.size() == currentValues.maxSize()) {
            listener.recognizeGesture(currentValues.toArray(new float[currentValues.size()][]));
        }
        currentValues.add(value);
    }

    public void registerListener(GestureRecorderListener listener) {
        this.listener = listener;
        start();
    }

    public void start() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void unregisterListener(GestureRecorderListener listener) {
        this.listener = null;
        stop();
    }

    public void pause(boolean b) {
        if (b) {
            sensorManager.unregisterListener(this);
        } else {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void resetBuffer(int runeIndex) {
        currentValues = new CircularFifoQueue(4); // TODO
    }

}