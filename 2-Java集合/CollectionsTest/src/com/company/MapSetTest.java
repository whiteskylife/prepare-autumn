package com.company;

import java.util.*;

/**
 * @author thisxzj
 * @date 2019 2019-08-07 00:09
 */


public class MapSetTest {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>(16);
        final int LENGTH = 6;
        for (int i = 0; i < LENGTH; i++) {
            map.put(i, "No." + (LENGTH - i));
        }
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        Collection<String> values = map.values();

        Set<Integer> keys = map.keySet();
        for (Map.Entry entry : entrySet) {
            System.out.print(entry.toString() + " ");
        }

        System.out.print("\nkeys:\t");
        for (Integer k : keys) {
            System.out.print(k + "   \t");
        }

        System.out.print("\nvalues:\t");
        for (String v : values) {
            System.out.print(v + "\t");
        }

    }
}
