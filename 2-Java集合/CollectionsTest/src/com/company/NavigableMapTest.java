package com.company;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author thisxzj
 * @date 2019 2019-08-06 23:54
 */

public class NavigableMapTest {
    public static void main(String[] args) {
        NavigableMap<Integer, String> map = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, "No." + (10 - i));
        }
        Map.Entry<Integer, String> entry = map.higherEntry(5);
        System.out.println(entry.toString());
    }
}
