package com.bbw.common;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtilTest {

    @Test
    public void partition() {
        List<Integer> collect = Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).collect(Collectors.toList());
        collect.remove(2);
        collect.add(2, 100);
        System.out.println(collect);
    }
}