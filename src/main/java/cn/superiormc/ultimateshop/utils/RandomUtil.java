package cn.superiormc.ultimateshop.utils;

import java.util.*;

public class RandomUtil {

    public static <T> T getRandomElement(Set<T> set) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Set is empty");
        }

        int randomIndex = new Random().nextInt(set.size());
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < randomIndex; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public static <K, V> K getRandomKey(Map<K, V> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Map is empty");
        }

        List<K> keyList = new ArrayList<>(map.keySet());
        int randomIndex = new Random().nextInt(keyList.size());
        return keyList.get(randomIndex);
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }

        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

}
