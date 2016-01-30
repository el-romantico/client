package com.elromantico.client;

import android.support.annotation.IntegerRes;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Martin Doychev on 30-Jan-16.
 */
public class AudioMap {

    public static Map<Integer, Integer> connectSounds = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> effectSounds = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> failSounds = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> successSounds = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> waitingSounds = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> winSounds = new HashMap<Integer, Integer>();

    static {
        connectSounds.put(0, R.raw.connect_1);
        connectSounds.put(1, R.raw.connect_2);
        connectSounds.put(2, R.raw.connect_3);
        connectSounds.put(3, R.raw.connect_4);

        effectSounds.put(0, R.raw.effect_1);
        effectSounds.put(1, R.raw.effect_2);

        failSounds.put(0, R.raw.fail_1);

        successSounds.put(0, R.raw.success_1);
        successSounds.put(1, R.raw.success_2);

        waitingSounds.put(0, R.raw.waiting_1);
        waitingSounds.put(1, R.raw.waiting_2);
        waitingSounds.put(2, R.raw.waiting_3);
        waitingSounds.put(3, R.raw.waiting_4);

        winSounds.put(0, R.raw.win_1);
        winSounds.put(1, R.raw.win_2);
        winSounds.put(2, R.raw.win_3);
    }

    public static Integer getRandomSound(Map<Integer, Integer> map) {
        Random rand = new Random();
        int value = map.get(rand.nextInt(map.size()));
        return value;
    }
}
