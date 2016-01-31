package com.elromantico.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin Doychev on 30-Jan-16.
 */
public class DrawablesMap {

    public static Map<Integer, Integer> drawablesMap = new HashMap<Integer, Integer>();

    static {
        drawablesMap.put(0, R.drawable.dishwashing);
        drawablesMap.put(1, R.drawable.mouthwashing);
        drawablesMap.put(2, R.drawable.turntablescratching);
        drawablesMap.put(3, R.drawable.swordsharpening);
        drawablesMap.put(4, R.drawable.handbell);
        drawablesMap.put(5, R.drawable.guitar);
    }
}
