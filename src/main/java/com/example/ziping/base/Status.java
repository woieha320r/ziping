package com.example.ziping.base;

import com.example.ziping.utils.CycleIndexUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 为 从无到有，日益生旺，盛极而衰，衰而至无 这一过程定出十二种状态。
 * 只有处于渐盛渐衰过程中的状态，被表达者才是存在的。
 * 其中渐盛过程的状态谓之阳，渐衰过程的状态谓之阴。
 * 只有 "长生", "临官", "帝旺", "衰", "墓" 这五种状态被称为“有用”。
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public enum Status {
    TAI("胎", null, false, "自无而有的酝酿"),
    YANG("养", null, false, "自无而有的酝酿"),
    CHANG_SHENG("长生", Simple.YANG, true, "萌芽初长"),
    MU_YU("沐浴", Simple.YANG, false, "初生幼稚，气渐生长，犹如登场前的穿衣准备"),
    GUAN_DAI("冠带", Simple.YANG, false, "初生幼稚，气渐生长，犹如登场前的穿衣准备"),
    LIN_GUAN("临官", Simple.YANG, true, "人值壮年，锐气正盛"),
    DI_WANG("帝旺", Simple.YANG, true, "极旺之时，由盛转衰的临界，但仍处于旺时，盛气已竭，此后开始转衰"),
    SHUAI("衰", Simple.YIN, true, "影响力开始衰退"),
    BING("病", Simple.YIN, false, "年迈，体弱多病"),
    SI("死", Simple.YIN, false, "死"),
    MU("墓", Simple.YIN, true, "入土"),
    JUE("绝", null, false, "完全消失");

    @Getter
    @AllArgsConstructor
    public enum Simple {
        YIN("阴"),
        YANG("阳");

        private final String name;
    }

    private final String name;
    private final Simple simple;
    private final boolean isUseful;
    private final String desc;

    /**
     * 声明时的顺序
     */
    @Getter(AccessLevel.NONE)
    private static final List<Status> STATUS_INDEX = Arrays.asList(Status.values());

    /**
     * 下一个
     */
    public Status next() {
        return nextN(1);
    }

    /**
     * 下n个，负数时为前n个
     */
    public Status nextN(long n) {
        return STATUS_INDEX.get(CycleIndexUtil.convertDistanceInBorder(STATUS_INDEX.size(), STATUS_INDEX.indexOf(this), n));
    }

    /**
     * 根据名称获得对象
     */
    public static Status getByName(String name) {
        return STATUS_INDEX.parallelStream()
                .filter(status -> Objects.equals(status.getName(), name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("无此状态：" + name));
    }
}
