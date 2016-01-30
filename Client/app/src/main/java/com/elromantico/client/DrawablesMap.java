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
    }
}
