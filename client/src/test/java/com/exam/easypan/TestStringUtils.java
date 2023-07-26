package com.exam.easypan;

import org.junit.jupiter.api.Test;

/**
 * @author: ZhangX
 * @createDate: 2023/7/25
 * @description:
 */
public class TestStringUtils {
    @Test
    public void test1(){
        String fileName = "aaa.txt";
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            System.out.println("aa");
        }
        String suffix = fileName.substring(index);
        System.out.println(suffix);
    }

}
