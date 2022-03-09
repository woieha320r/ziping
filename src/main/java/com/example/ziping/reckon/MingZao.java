package com.example.ziping.reckon;

import com.example.ziping.base.JiaZi;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 命造：生辰的甲子历表示+性别，起运的依据
 * TODO: 未完成，差性别，差设计其与运势的关系
 */
@Data
@Accessors(chain = true)
public class MingZao {

    private JiaZi year;
    private JiaZi month;
    private JiaZi day;
    private JiaZi hour;

    public static MingZao of(LocalDateTime glennBirthday) {
        JiaZi year = JiaZi.getYear(glennBirthday);
        JiaZi month = JiaZi.getMonth(glennBirthday, year.getGan());
        JiaZi day = JiaZi.getDay(glennBirthday);
        JiaZi hour = JiaZi.getHour(glennBirthday, day.getGan());
        return new MingZao()
                .setYear(year)
                .setMonth(month)
                .setDay(day)
                .setHour(hour);
    }

    public String jiaZi() {
        return year.getGan().getName() + year.getZhi().getName() + "年"
                + month.getGan().getName() + month.getZhi().getName() + "月"
                + day.getGan().getName() + day.getZhi().getName() + "日"
                + hour.getGan().getName() + hour.getZhi().getName() + "时";
    }

}
