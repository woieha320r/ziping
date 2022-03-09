package com.example.ziping.utils;

/**
 * 循环下标工具
 */
public class CycleIndexUtil {

    /**
     * 将大于个数的下标循环进个数内
     */
    public static int convertDistanceInBorder(int cycleBorder, int startIndex, long distance) {
        distance = distance % cycleBorder;
        if (distance < 0) {
            distance = convertDistanceToPositive(cycleBorder, distance);
        }
        return (((int) distance) + startIndex) % cycleBorder;
    }

    /**
     * 将负的距离转为顺行的正数距离
     */
    public static int convertDistanceToPositive(int cycleBorder, long distance) {
        return (int) (distance > 0 ? distance : (-distance) * (cycleBorder - 1) % cycleBorder);
    }

}
