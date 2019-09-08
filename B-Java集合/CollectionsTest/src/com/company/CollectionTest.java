package com.company;

import java.util.*;

/**
 * @author thisxzj
 * @date 2019 2019-08-06 23:28
 */


public class CollectionTest {
    public static void main(String[] args) {
        Collection<Object> collection = new LinkedList<>();
        collection.iterator();
        Map map = new HashMap(16);
        Queue<Object> queue = new PriorityQueue<>();
        queue.iterator();


        List<String> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            list.add("No." + i);
        }

        for (int i = 0; i < 10; i++) {
            String s = list.get(i);
            list.remove(s);
        }
        Iterator iterator = list.iterator();

    }
}
