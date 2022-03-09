package com.example.ziping.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 生成代表相生五行的数字
 */
public class GenerateTheFiveElements {

    private static final int SIZE = 5;
    private static final int MAX = 10;
    private static final int MIN = 1;

    /**
     * 生成可以代表相生五行的五个数字。
     * 算法是：预设第一个数为a，第二个数为b，新b = (1 + 旧b) / a。第四次计算的新b == a。
     * 如：[1, 4, 5, 1.5, 0.5]
     */
    private static void generate() {
        List<Double> result = new ArrayList<>();
        for (double first = MIN; first <= MAX; first++) {
            for (double second = first + 1; second <= MAX; second++) {
                int index = 0;
                boolean jump = false;
                result.add(index++, first);
                result.add(index++, second);
                do {
                    double nextElement = getTheNextElement(result.get(index - 2), result.get(index - 1));
                    if (canCalculate(nextElement) && noRepeat(nextElement, result)) {
                        result.add(index, nextElement);
                    } else {
                        jump = true;
                    }
                } while (!jump && index++ < SIZE);
                if (jump || !isCycle(result)) {
                    result.clear();
                    continue;
                }
                result.remove(--index);
                System.out.println(result);
                result.clear();
            }
        }
    }

    /**
     * 五行数字列表是否循环
     */
    private static boolean isCycle(List<Double> nums) {
        return Objects.equals(nums.get(0), getTheNextElement(nums.get(SIZE - 2), nums.get(SIZE - 1)));
    }

    /**
     * 元素是否在列表中不存在
     */
    private static boolean noRepeat(double num, List<Double> nums) {
        return nums.indexOf(num) <= 0;
    }

    /**
     * 小数位数不超过2
     */
    private static boolean canCalculate(double num) {
        return String.valueOf(num).split("\\.")[1].length() <= 2;
    }

    /**
     * 获取下一个相生五行数字
     */
    private static double getTheNextElement(double perv, double cur) {
        return (1 + cur) / perv;
    }

    public static void main(String[] args) {
        generate();
    }

}
