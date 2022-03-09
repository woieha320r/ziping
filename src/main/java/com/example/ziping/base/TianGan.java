package com.example.ziping.base;

import com.example.ziping.utils.CycleIndexUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 天干：五行各自阴阳（生旺死绝十二种状态的简称）状态的代名词
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public enum TianGan {
    JIA("甲", Status.Simple.YANG, TheFivePhases.MU),
    YI("乙", Status.Simple.YIN, TheFivePhases.MU),
    BING("丙", Status.Simple.YANG, TheFivePhases.HUO),
    DING("丁", Status.Simple.YIN, TheFivePhases.HUO),
    WU("戊", Status.Simple.YANG, TheFivePhases.TU),
    JI("己", Status.Simple.YIN, TheFivePhases.TU),
    GENG("庚", Status.Simple.YANG, TheFivePhases.JIN),
    XIN("辛", Status.Simple.YIN, TheFivePhases.JIN),
    REN("壬", Status.Simple.YANG, TheFivePhases.SHUI),
    GUI("癸", Status.Simple.YIN, TheFivePhases.SHUI);

    private final String name;
    private final Status.Simple simpleStatus;
    private final TheFivePhases theFivePhases;

    /**
     * 声明时的顺序
     */
    @Getter(AccessLevel.NONE)
    private static final List<TianGan> TIAN_GAN_INDEX = Arrays.asList(TianGan.values());

    /**
     * 下一个
     */
    public TianGan next() {
        return nextN(1);
    }

    /**
     * 下n个，负数时为前n个
     */
    public TianGan nextN(long n) {
        return TIAN_GAN_INDEX.get(CycleIndexUtil.convertDistanceInBorder(TIAN_GAN_INDEX.size(), TIAN_GAN_INDEX.indexOf(this), n));
    }

    /**
     * 根据名称获得对象
     */
    public static TianGan getByName(String name) {
        return TIAN_GAN_INDEX.parallelStream()
                .filter(tianGan -> Objects.equals(tianGan.getName(), name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("无此天干：" + name));
    }
}
