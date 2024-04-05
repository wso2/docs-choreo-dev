package com.wso2.choreo.integration.common.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NameGenerator {

    private static final List<Integer> range = IntStream.range(1, 10).boxed().collect(Collectors.toList());
    private static Iterator<Integer> iterator = range.iterator();

    public static String generateUniqueName(String baseName) {
        String timestamp = String.valueOf(new Date().getTime());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return baseName.concat("_").concat(timestamp).concat("_").concat(uuid);
    }

    public static String generateThreadUniqueName() {
        String timestamp = String.valueOf(new Date().getTime());
        long id = Thread.currentThread().getId();
        return String.valueOf(id).concat("autotest").concat(timestamp);
    }

    public static String generateThreadUniqueNameWithPrefix(String prefix) {
        String timestamp = String.valueOf(new Date().getTime());
        long id = Thread.currentThread().getId();
        return prefix.concat(String.valueOf(getNextInt())).concat(String.valueOf(id)).concat("T").concat(timestamp);
    }

    private static synchronized int getNextInt() {
        if (!iterator.hasNext()) {
            iterator = range.iterator();
        }
        return iterator.next();
    }
}
