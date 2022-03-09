package com.example.ziping.base;

import com.example.ziping.utils.CycleIndexUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 五行，气的五个阶段表现特征
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public enum TheFivePhases {
    MU("木", "春", "东", "曲直"),
    HUO("火", "夏", "南", "炎上"),
    TU("土", "长夏", "中", "稼穑"),
    JIN("金", "秋", "西", "从革"),
    SHUI("水", "东", "北", "润下");

    private final String name;
    private final String season;
    private final String direction;
    private final String desc;

    /**
     * 声明时的顺序
     */
    @Getter(AccessLevel.NONE)
    private static final List<TheFivePhases> ELEMENTS_SUPPRESS_INDEX = Arrays.asList(TheFivePhases.values());

    /**
     * 下n个，负数时为前n个
     */
    public TheFivePhases nextN(long n) {
        return ELEMENTS_SUPPRESS_INDEX.get(CycleIndexUtil.convertDistanceInBorder(ELEMENTS_SUPPRESS_INDEX.size(), ELEMENTS_SUPPRESS_INDEX.indexOf(this), n));
    }

    /**
     * 克我者
     */
    public TheFivePhases suppressMe() {
        return nextN(-2);
    }

    /**
     * 我克者
     */
    public TheFivePhases iSuppress() {
        return nextN(2);
    }

    /**
     * 生我者
     */
    public TheFivePhases encourageMe() {
        return nextN(-1);
    }

    /**
     * 我生者
     */
    public TheFivePhases iEncourage() {
        return nextN(1);
    }

    /**
     * 同我者
     */
    public TheFivePhases equalMe() {
        return this;
    }

    /**
     * 根据名称获得对象
     */
    public static TheFivePhases getByName(String name) {
        return ELEMENTS_SUPPRESS_INDEX.parallelStream()
                .filter(phases -> Objects.equals(phases.getName(), name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("无此五行：" + name));
    }
}
