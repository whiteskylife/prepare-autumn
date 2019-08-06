package com.company;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author thisxzj
 * @date 2019 2019-08-07 00:50
 */


public class HashTableTest {
    public static void main(String[] args) {
        Hashtable<Integer, String> table = new Hashtable<>();
        HashMap<Integer, String> map = new HashMap<>();
        Map<Integer, String> synchronizedMap = Collections.synchronizedMap(map);
    }
}
