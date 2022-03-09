package com.example.ziping.base;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 六十甲子：天干地支的六十种组合方式。
 * 甲子，乙丑...癸酉，甲戌，乙亥，丙子，丁丑...癸亥
 * <p>
 * 甲子历的年月日时都使用轮转的六十甲子来记录。
 * 每两小时，换时辰。每逢子时，换日。每逢交节，欢悦。每逢立春，换年。
 * TODO: 比较和公式换算的优缺点，是否改用公式换算（baike.baidu.com/item/干支历/9386578#7）
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@SuppressWarnings("unused")
public class JiaZi {

    private TianGan gan;
    private DiZhi zhi;

    /**
     * 五虎遁、五鼠遁：方便得知起始地支位对应天干的口诀
     * 以日干起子时干，五鼠遁
     * 以年干起寅月干，五虎遁
     */
    @Getter(AccessLevel.NONE)
    private static final Map<TianGan, TianGan> WU_SHU_DUN = MapUtil.builder(new HashMap<TianGan, TianGan>())
            .put(TianGan.JIA, TianGan.JIA)
            .put(TianGan.JI, TianGan.JIA)
            .put(TianGan.YI, TianGan.BING)
            .put(TianGan.GENG, TianGan.BING)
            .put(TianGan.BING, TianGan.WU)
            .put(TianGan.XIN, TianGan.WU)
            .put(TianGan.DING, TianGan.GENG)
            .put(TianGan.REN, TianGan.GENG)
            .put(TianGan.WU, TianGan.REN)
            .put(TianGan.GUI, TianGan.REN)
            .build();
    @Getter(AccessLevel.NONE)
    private static final Map<TianGan, TianGan> WU_HU_DUN = MapUtil.builder(new HashMap<TianGan, TianGan>())
            .put(TianGan.JIA, TianGan.BING)
            .put(TianGan.JI, TianGan.BING)
            .put(TianGan.YI, TianGan.WU)
            .put(TianGan.GENG, TianGan.WU)
            .put(TianGan.BING, TianGan.GENG)
            .put(TianGan.XIN, TianGan.GENG)
            .put(TianGan.DING, TianGan.REN)
            .put(TianGan.REN, TianGan.REN)
            .put(TianGan.WU, TianGan.JIA)
            .put(TianGan.GUI, TianGan.JIA)
            .build();

    public static JiaZi next(JiaZi flag) {
        return nextN(flag, 1L);
    }

    public static JiaZi nextN(JiaZi flag, long n) {
        return new JiaZi(flag.getGan().nextN(n), flag.getZhi().nextN(n));
    }

    /**
     * 立春为界，公历元年为辛酉年
     */
    public static JiaZi getYear(LocalDateTime glennTime) {
        JiaZi flagYear = new JiaZi(TianGan.XIN, DiZhi.YOU);
        int distance = glennTime.getYear() - 1;
        if (JieQi.LI_CHUN.isAfterInGlennYear(glennTime)) {
            distance--;
        }
        return nextN(flagYear, distance);
    }

    /**
     * 节气节为界
     */
    public static JiaZi getMonth(LocalDateTime glennTime, TianGan yearGan) {
        JieQi jieQi = JieQi.getJieQi(glennTime);
        DiZhi diZhi = DiZhi.getByJieQi(jieQi);
        return getMonthByYear(diZhi, yearGan);
    }

    /**
     * 子时为届
     */
    public static JiaZi getDay(LocalDateTime glennTime) {
        // 暂定以公历1900年1月1日为基准推算，甲子历为甲戌日
        JiaZi flagDayJiaZi = new JiaZi(TianGan.JIA, DiZhi.XU);
        LocalDate flagDayGlenn = LocalDate.of(1900, 1, 1);
        // 向后以日始开始满24小时算一天，向前以日末开始满24小时算一天
        LocalDateTime flagDayGlennStart = flagDayGlenn.minusDays(1L).atTime(DiZhi.ZI.getGlennTime());
        LocalDateTime flagDayGlennEnd = flagDayGlenn.atTime(DiZhi.ZI.getGlennTime());
        long distance;
        if (glennTime.isAfter(flagDayGlennStart)) {
            distance = LocalDateTimeUtil.between(flagDayGlennStart, glennTime, ChronoUnit.DAYS);
        } else {
            distance = -(LocalDateTimeUtil.between(glennTime, flagDayGlennEnd, ChronoUnit.DAYS));
            if (Objects.equals(glennTime.getHour(), DiZhi.ZI.getGlennTime().getHour())) {
                distance++;
            }
        }
        return nextN(flagDayJiaZi, distance);
    }

    /**
     * 公历每日23:00:00起，每2小时为届
     */
    public static JiaZi getHour(LocalDateTime glennTime, TianGan dayGan) {
        DiZhi shiZhi = DiZhi.getByShiChen(glennTime.toLocalTime());
        return getHourByDay(shiZhi, dayGan);
    }

    /**
     * 以年起月，月始于寅
     */
    private static JiaZi getMonthByYear(DiZhi monthZhi, TianGan yearGan) {
        JiaZi startJiaZi = new JiaZi(WU_HU_DUN.get(yearGan), DiZhi.YIN);
        return nextN(startJiaZi, monthZhi.distance(DiZhi.YIN));
    }

    /**
     * 以月起时，时始于子
     */
    private static JiaZi getHourByDay(DiZhi hourZhi, TianGan dayGan) {
        JiaZi startJiaZi = new JiaZi(WU_SHU_DUN.get(dayGan), DiZhi.ZI);
        return nextN(startJiaZi, hourZhi.distance(DiZhi.ZI));
    }

}
