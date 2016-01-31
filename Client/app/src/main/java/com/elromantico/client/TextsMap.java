package com.elromantico.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Martin Doychev on 31-Jan-16.
 */
public class TextsMap {

    public static Map<Integer, Map<Integer, String>> textsMap = new HashMap<>();

    public static Map<Integer, String> vinylMap = new HashMap<Integer, String>();
    public static Map<Integer, String> dishwashingMap = new HashMap<Integer, String>();
    public static Map<Integer, String> swordMap = new HashMap<Integer, String>();
    public static Map<Integer, String> teethMap = new HashMap<Integer, String>();
    public static Map<Integer, String> handbellMap = new HashMap<Integer, String>();

    static {
        dishwashingMap.put(0, "\"Something something make me a sandwitch\" \n- Men");
        dishwashingMap.put(1, "Now you’re thinking about that greasy lasagna you had earlier, aren’t you?");
        dishwashingMap.put(2, "There's vomit on his sweater already: mom's spaghetti \n- The real Slim Shady");
        textsMap.put(0, dishwashingMap);

        teethMap.put(0, "\"You gotta do what you gotta do\" \n- Everyone you know");
        teethMap.put(1, "\"And remember to floss\" \n- Your dentist probably.");
        textsMap.put(1, teethMap);

        vinylMap.put(0, "\"It’s hard to be a DJ\" \n- Avicii");
        vinylMap.put(1, "\"Wub-wub-dub dum-dub-dub-wub\" \n- Skrillex");
        vinylMap.put(2, "The faster you spin it, the screechier it gets");
        vinylMap.put(3, "\"Free-styler, wacka-macka four\" \n- Everyone, except Bomfunk MC’s");
        textsMap.put(2, vinylMap);

        swordMap.put(0, "Your fingers may be bleeding but at least your game is on edge");
        swordMap.put(1, "\"Do I look cool doing this?\" \n- Nobody that played this game");
        swordMap.put(2, "Look at you, so fun and edgy!");
        swordMap.put(3, "Does this remind you of that time when… forget it.");
        swordMap.put(4, "\"Your mom does it better\" \n- Everyone");
        textsMap.put(3, swordMap);

        handbellMap.put(0, "First day of school isn’t what it used to be");
        handbellMap.put(1, "Does this remind you of the time you went to church?");
        handbellMap.put(2, "\"Shame. Shame. Shame.\" \n- Some TV show probably");
        textsMap.put(4, handbellMap);
    }

    public static String getRandomString(int runeIndex) {

        Map<Integer, String> map = textsMap.get(runeIndex);

        Random rand = new Random();
        String value = map.get(rand.nextInt(map.size()));
        return value;
    }
}
