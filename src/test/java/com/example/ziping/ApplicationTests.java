package com.example.ziping;

import com.example.ziping.reckon.MingZao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * 测试
 */
@SpringBootTest
class ApplicationTests {

    /**
     * 公历 -> 甲子历
     */
    @Test
    void calendar() {
        System.out.println(MingZao.of(LocalDateTime.of(1800, 1, 1, 7, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1800, 1, 1, 23, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1800, 6, 12, 9, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1899, 12, 31, 7, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1899, 12, 31, 23, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1900, 1, 1, 7, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1900, 1, 1, 23, 0, 0)).jiaZi());
        System.out.println(MingZao.of(LocalDateTime.of(1900, 9, 30, 23, 0, 0)).jiaZi());
        /*
         * 结果应为
         * 己未年丙子月庚寅日庚辰时
         * 己未年丙子月辛卯日戊子时
         * 庚申年壬午月壬申日乙巳时
         * 己亥年丙子月癸酉日丙辰时
         * 己亥年丙子月甲戌日甲子时
         * 己亥年丙子月甲戌日戊辰时
         * 己亥年丙子月乙亥日丙子时
         * 庚子年乙酉月丁未日庚子时
         * */
    }

}
