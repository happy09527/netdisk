package com.exam.easypan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ZhangX
 * @createDate: 2023/5/12
 * @description:
 */

@SpringBootTest
public class TestController {
    @Test
    public void test1(){
        System.out.println("Ass");
        List<Integer> accountIdListOne = new ArrayList<>();
        accountIdListOne.add(1);
        accountIdListOne.add(2);
        accountIdListOne.add(3);

        List<Integer> accountIdListTwo = new ArrayList<>();
        accountIdListTwo.add(3);
        accountIdListTwo.add(4);
        accountIdListTwo.add(5);
        accountIdListTwo.add(6);

        List<Integer> accountIdList = accountIdListOne.stream().filter(accountIdListTwo::contains).collect(Collectors.toList());

        System.out.println(accountIdList.toString());
    }
}
