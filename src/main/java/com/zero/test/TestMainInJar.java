package com.zero.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMainInJar {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMainInJar.class);

    public static void main(String[] args) throws InterruptedException {
        int count = 0;
        while (true) {
            Thread.sleep(1000);
            int number = 3;
            test(number);
            new TestMainInJar().test2();
            new TestMainInJar().test3(3, "zero");
        }
    }

    public static void test(int a) throws InterruptedException {
        Thread.sleep(1000);
        LOGGER.info("test a");
    }

    public void test2() throws InterruptedException {
        System.out.println("test2");
    }

    public void test3(long a, String b) throws InterruptedException {
        System.out.println("test3: --- " + a + "," + b);
    }
}
