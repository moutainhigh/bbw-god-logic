package com.bbw.java8;

import org.junit.Test;

/**
 * 数值测试
 *
 * @author: suhq
 * @date: 2021/12/10 4:29 下午
 */
public class NumberTest {


    @Test
    public void test() {
        int baseCopper = 1223344000;
        baseCopper *= (1 + 100000000.00000000f);
        System.out.println(baseCopper);
    }

}
