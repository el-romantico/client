package com.elromantico.client.gestures;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GestureRecorder implements SensorEventListener {

    public interface GestureRecorderHandler {

        void handle(float[][] values);
    }

    private SensorManager sensorManager;
    private Context context;
    private GestureRecorderHandler handler;
    private CircularFifoQueue<float[]> currentValues = new CircularFifoQueue<>(20);
    private ExecutorService tpe = Executors.newFixedThreadPool(1);

    public GestureRecorder(Context context) {
        this.context = context;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // do nothing
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        tpe.execute(new Runnable() {
            @Override
            public void run() {
                float[] value = {
                        sensorEvent.values[SensorManager.DATA_X],
                        sensorEvent.values[SensorManager.DATA_Y],
                        sensorEvent.values[SensorManager.DATA_Z]
                };

                if (currentValues.size() == currentValues.maxSize()) {
                    handler.handle(currentValues.toArray(new float[currentValues.size()][]));
                }
                currentValues.add(value);
            }
        });
    }

    public void registerListener(GestureRecorderHandler handler) {
        this.handler = handler;
        start();
    }

    public void start() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void unregisterListener() {
        this.handler = null;
        stop();
    }

    public void pause(boolean b) {
        if (b) {
            sensorManager.unregisterListener(this);
        } else {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void resetBuffer(int bufferSize) {
        currentValues = new CircularFifoQueue(bufferSize); // TODO
    }
}