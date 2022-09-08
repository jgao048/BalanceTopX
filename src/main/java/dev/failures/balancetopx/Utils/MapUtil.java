package dev.failures.balancetopx.Utils;

import java.util.*;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((Comparator)Map.Entry.comparingByValue(Comparator.reverseOrder()));
        HashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }
}
