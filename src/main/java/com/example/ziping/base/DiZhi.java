package com.example.ziping.base;

import cn.hutool.core.date.TemporalUtil;
import com.example.ziping.utils.CycleIndexUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 十二地支
 * 时序的代名词，用于月建始于寅，用于其他始于子。
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public enum DiZhi {
    ZI("子", JieQi.DA_XUE, LocalTime.of(23, 0)),
    CHOU("丑", JieQi.XIAO_HAN, LocalTime.of(1, 0)),
    YIN("寅", JieQi.LI_CHUN, LocalTime.of(3, 0)),
    MAO("卯", JieQi.JING_ZHE, LocalTime.of(5, 0)),
    CHEN("辰", JieQi.QING_MING, LocalTime.of(7, 0)),
    SI("巳", JieQi.LI_XIA, LocalTime.of(9, 0)),
    WU("午", JieQi.MANG_ZHONG, LocalTime.of(11, 0)),
    WEI("未", JieQi.XIAO_SHU, LocalTime.of(13, 0)),
    SHEN("申", JieQi.LI_QIU, LocalTime.of(15, 0)),
    YOU("酉", JieQi.BAI_LU, LocalTime.of(17, 0)),
    XU("戌", JieQi.HAN_LU, LocalTime.of(19, 0)),
    HAI("亥", JieQi.LI_DONG, LocalTime.of(21, 0));

    private final String name;
    private final JieQi jieQi;
    private final LocalTime glennTime;

    /**
     * 声明时的顺序
     */
    @Getter(AccessLevel.NONE)
    private static final List<DiZhi> DI_ZHI_INDEX = Arrays.asList(DiZhi.values());

    /**
     * 下一个
     */
    public DiZhi next() {
        return nextN(1);
    }

    /**
     * 下n个，负数时为前n个
     */
    public DiZhi nextN(long n) {
        return DI_ZHI_INDEX.get(CycleIndexUtil.convertDistanceInBorder(DI_ZHI_INDEX.size(), DI_ZHI_INDEX.indexOf(this), n));
    }

    /**
     * 根据名称获得对象
     */
    public static DiZhi getByName(String name) {
        return DI_ZHI_INDEX.parallelStream()
                .filter(diZhi -> Objects.equals(diZhi.getName(), name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("依据名称无此地支：" + name));
    }

    /**
     * 根据节气获得对象
     */
    public static DiZhi getByJieQi(JieQi jieQi) {
        return DI_ZHI_INDEX.parallelStream()
                .filter(diZhi -> Objects.equals(diZhi.getJieQi(), jieQi))
                .findAny()
                .orElseThrow(() -> new RuntimeException("依据节气无此地支：" + jieQi.getName()));
    }

    /**
     * 距离某个地支的距离，永远返回正数
     */
    public int distance(DiZhi diZhi) {
        return CycleIndexUtil.convertDistanceToPositive(DI_ZHI_INDEX.size(), DI_ZHI_INDEX.indexOf(this) - DI_ZHI_INDEX.indexOf(diZhi));
    }

    /**
     * 依据入参时间获取时支
     */
    public static DiZhi getByShiChen(LocalTime glennTime) {
        return Arrays.stream(DiZhi.values()).parallel().filter(dizhi -> {
            long diffMinutes = TemporalUtil.between(dizhi.getGlennTime(), glennTime, ChronoUnit.MINUTES);
            return diffMinutes >= 0 && diffMinutes < 120;
        }).findAny().orElseThrow(() -> new RuntimeException("无此时辰：" + glennTime.toString()));
    }
}
