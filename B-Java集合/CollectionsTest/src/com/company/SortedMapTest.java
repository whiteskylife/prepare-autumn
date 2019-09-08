package com.company;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author thisxzj
 * @date 2019 2019-08-07 00:20
 */


public class SortedMapTest {
    public static void main(String[] args) {
        SortedMap<Integer, String> map = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, "No." + (10 - i));
        }

    }
}
