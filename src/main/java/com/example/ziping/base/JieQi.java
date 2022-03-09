package com.example.ziping.base;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.ziping.utils.CycleIndexUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 十天干
 * 五行各自简称生旺死绝状态（阴阳）的代名词
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public enum JieQi {
    LI_CHUN("立春"),
    YU_SHUI("雨水"),
    JING_ZHE("惊蛰"),
    CHUN_FEN("春分"),
    QING_MING("清明"),
    GU_YU("谷雨"),
    LI_XIA("立夏"),
    XIAO_MAN("小满"),
    MANG_ZHONG("芒种"),
    XIA_ZHI("夏至"),
    XIAO_SHU("小暑"),
    DA_SHU("大暑"),
    LI_QIU("立秋"),
    CHU_SHU("处暑"),
    BAI_LU("白露"),
    QIU_FEN("秋分"),
    HAN_LU("寒露"),
    SHUANG_JIANG("霜降"),
    LI_DONG("立冬"),
    XIAO_XUE("小雪"),
    DA_XUE("大雪"),
    DONG_ZHI("冬至"),
    XIAO_HAN("小寒"),
    DA_HAN("大寒");

    private final String name;

    /**
     * 声明时的顺序
     */
    @Getter(AccessLevel.NONE)
    private static final List<JieQi> JIE_QI_INDEX = Arrays.asList(JieQi.values());

    /**
     * 所有节
     */
    @Getter(AccessLevel.NONE)
    private static final List<JieQi> JIE_INDEX = JIE_QI_INDEX.stream()
            .filter(jieQiName -> JIE_QI_INDEX.indexOf(jieQiName) % 2 == 0)
            .sorted(Comparator.comparingInt(JIE_QI_INDEX::indexOf))
            .collect(Collectors.toList());

    /**
     * 历年交节时间（公历）
     */
    @Getter(AccessLevel.NONE)
    private static final JSONArray JIE_QI_TIME = JSONUtil.parseArray(FileUtil.readString("../classes/jieQi.json", StandardCharsets.UTF_8));
    @Getter(AccessLevel.NONE)
    private static final String CACHE_CURR_YEAR_KEY = "年份";

    /**
     * 下一个
     */
    public JieQi next() {
        return nextN(1);
    }

    /**
     * 下n个，负数时为前n个
     */
    public JieQi nextN(long n) {
        int nextIndex = CycleIndexUtil.convertDistanceInBorder(JIE_QI_INDEX.size(), JIE_QI_INDEX.indexOf(this), n);
        return JIE_QI_INDEX.get(nextIndex);
    }

    /**
     * 根据名称获得对象
     */
    public static JieQi getByName(String name) {
        return JIE_QI_INDEX.parallelStream()
                .filter(jieQi -> Objects.equals(jieQi.getName(), name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("无此节气：" + name));
    }

    /**
     * 获取某公历年内的节气信息
     */
    private static JSONObject getJieQiInfoInGlennYear(int year) {
        return (JSONObject) JIE_QI_TIME.parallelStream()
                .filter(obj -> Objects.equals(((JSONObject) obj).getInt(CACHE_CURR_YEAR_KEY), year))
                .findAny()
                .orElseThrow(() -> new RuntimeException("节气记录中无此年份：" + year));
    }

    /**
     * 是否在某公历年中早于某个公历时间
     */
    public boolean isBeforeOrEqualInGlennYear(LocalDateTime glennTime) {
        return !isAfterInGlennYear(glennTime);
    }

    /**
     * 是否在某公历年中晚于某个公历时间
     */
    public boolean isAfterInGlennYear(LocalDateTime glennTime) {
        JSONObject jieQiInGlennYear = getJieQiInfoInGlennYear(glennTime.getYear());
        return jieQiInGlennYear.getLocalDateTime(getName(), null).isAfter(glennTime);
    }

    /**
     * 返回<0：入参时间晚于交节时间；返回>0：入参时间早于交节时间；返回=0：入参时间等于交节时间
     */
    private static long compare(JieQi jieQi, JSONObject jieQiTimeInfo, LocalDateTime time) {
        return LocalDateTimeUtil.parse(jieQiTimeInfo.getStr(jieQi.getName()), "yyyy-MM-dd HH:mm:ss").toEpochSecond(ZoneOffset.of("+8")) - time.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 获取入参时间所属的节气
     */
    public static JieQi getJieQi(LocalDateTime glennTime) {
        // 立春前是上一年，节气信息需使用上一年的
        JSONObject jieQiInfoVariable = getJieQiInfoInGlennYear(glennTime.getYear());
        if (JieQi.LI_CHUN.isAfterInGlennYear(glennTime)) {
            jieQiInfoVariable = getJieQiInfoInGlennYear(glennTime.getYear() - 1);
        }
        final JSONObject jieQiInfo = jieQiInfoVariable;
        // 记录位于公历时间之后的各节各自距离公历的时间
        Map<Long, JieQi> jieAfterDays = new HashMap<>();
        jieQiInfo.keySet().forEach(key -> {
            if (Objects.equals(key, CACHE_CURR_YEAR_KEY)) {
                return;
            }
            JieQi jieQi = getByName(key);
            if (!JIE_INDEX.contains(jieQi)) {
                jieAfterDays.put(-1L, jieQi);
                return;
            }
            long compareTime = compare(jieQi, jieQiInfo, glennTime);
            if (compareTime < 0) {
                jieAfterDays.put(-1L, jieQi);
                return;
            }
            jieAfterDays.put(compareTime, jieQi);
        });
        // 被记录的时间中，数值最小者为距离公历时间最近的下一个节。
        // 若没有，表示其在本年最后一个节之后出生，下一个节为立春
        // 依据下一个节向前推一个节为公历时间所在节
        Optional<Long> nextJieKey = jieAfterDays.keySet().stream().filter(val -> val >= 0).min(Long::compare);
        return (nextJieKey.isPresent() ? jieAfterDays.get(nextJieKey.get()) : LI_CHUN).nextN(-2);
    }
}
