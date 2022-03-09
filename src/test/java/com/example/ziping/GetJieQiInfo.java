package com.example.ziping;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 获取历年二十四节气交节时间
 */
// @SpringBootTest
class GetJieQiInfo {

    private static final String RESOURCE_PATH = "./";
    private static final String HTML_DIR_PATH = RESOURCE_PATH + "/jieQiFiles";
    private static final String HTML_DIR_PATH_CANNOT_HANDLE = RESOURCE_PATH + "/jieQiFilesCanNotHandle";
    public static final String RESULT_FILE_PATH = RESOURCE_PATH + "/jieQi.json";
    private final String fileExt = ".txt";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 从911网获取历年节气网页，每年一个网页。支持获取公历1～5000年的数据
     */
    // @Test
    public void getJieQiHtml() {
        Arrays.stream(ArrayUtil.range(1, 5001, 1)).parallel().boxed().collect(Collectors.toList()).forEach(index -> {
            //这网站搜404年会直接返回404提示网页！！？，那就没法儿知道404年的喽
            if (index == 404) {
                return;
            }
            String waitWriteFilePath = HTML_DIR_PATH + "/" + index + fileExt;
            if (!FileUtil.exist(waitWriteFilePath) || getFail(waitWriteFilePath)) {
                System.out.println(index);
                FileUtil.del(waitWriteFilePath);
                HttpUtil.createGet("https://jieqi.911cha.com/" + index + ".html").addHeaders(
                        MapUtil.builder(new HashMap<String, String>())
                                .put("Cookie", "t=01f9fef87631c4286e74d31b952e9eed; r=7104; Hm_lvt_2e69b379c7dbfdda15f852ee2e7139dc=1643182867; Hm_lpvt_2e69b379c7dbfdda15f852ee2e7139dc=1643182867")
                                .put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                                .put("Referer", "https://jieqi.911cha.com/5.html")
                                .put("Cache-Control", "max-age=0")
                                .put("Host", "jieqi.911cha.com")
                                .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
                                .put("Accept-Language", "zh-cn")
                                .put("Accept-Encoding", "br, gzip, deflate")
                                .put("Connection", "keep-alive")
                                .build()
                ).execute().writeBody(waitWriteFilePath);
                if (getFail(waitWriteFilePath)) {
                    String msg = "失败了！截止到" + index;
                    RuntimeUtil.execForStr("say " + msg);
                    System.exit(1);
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean getFail(String filePath) {
        String fileContent = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        return fileContent.contains("403 Forbidden") || !fileContent.contains("节气");
    }

    /**
     * 解析每个网页文件，提取出该年的节气日期，转为json数组并以文件存储
     */
    // @Test
    public void parseInfoFormHtml() {
        FileUtil.del(RESULT_FILE_PATH);
        int rangeStart = 1;
        int rangeEnd = 5001;
        String jsonStr = JSONUtil.toJsonStr(
                Arrays.stream(ArrayUtil.range(rangeStart, rangeEnd))
                        .mapToObj(i -> getJieQiJsonObjFromFile(HTML_DIR_PATH + "/" + i + fileExt))
                        .parallel()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        System.out.println("失败个数：" + (rangeEnd - rangeStart - JSONUtil.parseArray(jsonStr).size()));
        FileUtil.writeUtf8String(jsonStr, RESULT_FILE_PATH);
    }

    private JSONObject getJieQiJsonObjFromFile(String htmlFilePath) {
        if (!FileUtil.exist(htmlFilePath)) {
            System.out.println("文件不存在：" + htmlFilePath);
            return null;
        }
        final String divStartTag = "<div class=\"gclear jq mb\">";
        final String divEndTag = "</a></div>\t\t\t<div class=\"mcon bt\"><div class=\"gray\">二十四节气为您提供";
        // 试图匹配出<div class="gclear jq mb">内容</a></div>\t\t\t<div class="mcon bt"><div class="gray">二十四节气为您提供
        List<String> divContent = ReUtil.findAll(divStartTag + ".*" + divEndTag, FileUtil.readString(htmlFilePath, StandardCharsets.UTF_8), 0);
        if (divContent.size() != 1) {
            System.out.println("文件内容不合规：" + htmlFilePath);
            FileUtil.move(new File(htmlFilePath), new File(htmlFilePath.replaceAll(HTML_DIR_PATH, HTML_DIR_PATH_CANNOT_HANDLE)), true);
            FileUtil.del(htmlFilePath);
            return null;
        }
        //只留取内容部分：<a href="./jieqi1.html"><img src="https://ii.911cha.com/jieqi/s1.jpg" alt="立春" width="170" height="106" /><br /><span class="f14 green">1年立春时间</span><br />1年2月5日 15:18:39</a><a href="./jieqi2.html"><img src="https://ii.911cha.com/jieqi/s2.jpg" alt="雨水" width="170" height="106" /><br /><span class="f14 green">1年雨水时间</span><br />1年2月20日 16:46:32</a><a href="./jieqi3.html"><img src="https://ii.911cha.com/jieqi/s3.jpg" alt="惊蛰" width="170" height="106" /><br /><span class="f14 green">1年惊蛰时间</span><br />1年3月7日 21:40:21</a><a href="./jieqi4.html"><img src="https://ii.911cha.com/jieqi/s4.jpg" alt="春分" width="170" height="106" /><br /><span class="f14 green">1年春分时间</span><br />1年3月23日 05:43:49</a><a href="./jieqi5.html"><img src="https://ii.911cha.com/jieqi/s5.jpg" alt="清明" width="170" height="106" /><br /><span class="f14 green">1年清明时间</span><br />1年4月7日 17:06:21</a><a href="./jieqi6.html"><img src="https://ii.911cha.com/jieqi/s6.jpg" alt="谷雨" width="170" height="106" /><br /><span class="f14 green">1年谷雨时间</span><br />1年4月23日 07:05:54</a><a href="./jieqi7.html"><img src="https://ii.911cha.com/jieqi/s7.jpg" alt="立夏" width="170" height="106" /><br /><span class="f14 green">1年立夏时间</span><br />1年5月8日 23:22:51</a><a href="./jieqi8.html"><img src="https://ii.911cha.com/jieqi/s8.jpg" alt="小满" width="170" height="106" /><br /><span class="f14 green">1年小满时间</span><br />1年5月24日 16:57:43</a><a href="./jieqi9.html"><img src="https://ii.911cha.com/jieqi/s9.jpg" alt="芒种" width="170" height="106" /><br /><span class="f14 green">1年芒种时间</span><br />1年6月9日 11:08:08</a><a href="./jieqi10.html"><img src="https://ii.911cha.com/jieqi/s10.jpg" alt="夏至" width="170" height="106" /><br /><span class="f14 green">1年夏至时间</span><br />1年6月25日 04:53:32</a><a href="./jieqi11.html"><img src="https://ii.911cha.com/jieqi/s11.jpg" alt="小暑" width="170" height="106" /><br /><span class="f14 green">1年小暑时间</span><br />1年7月10日 21:22:17</a><a href="./jieqi12.html"><img src="https://ii.911cha.com/jieqi/s12.jpg" alt="大暑" width="170" height="106" /><br /><span class="f14 green">1年大暑时间</span><br />1年7月26日 11:50:36</a><a href="./jieqi13.html"><img src="https://ii.911cha.com/jieqi/s13.jpg" alt="立秋" width="170" height="106" /><br /><span class="f14 green">1年立秋时间</span><br />1年8月10日 23:33:34</a><a href="./jieqi14.html"><img src="https://ii.911cha.com/jieqi/s14.jpg" alt="处暑" width="170" height="106" /><br /><span class="f14 green">1年处暑时间</span><br />1年8月26日 08:16:02</a><a href="./jieqi15.html"><img src="https://ii.911cha.com/jieqi/s15.jpg" alt="白露" width="170" height="106" /><br /><span class="f14 green">1年白露时间</span><br />1年9月10日 13:32:38</a><a href="./jieqi16.html"><img src="https://ii.911cha.com/jieqi/s16.jpg" alt="秋分" width="170" height="106" /><br /><span class="f14 green">1年秋分时间</span><br />1年9月25日 15:39:49</a><a href="./jieqi17.html"><img src="https://ii.911cha.com/jieqi/s17.jpg" alt="寒露" width="170" height="106" /><br /><span class="f14 green">1年寒露时间</span><br />1年10月10日 14:36:17</a><a href="./jieqi18.html"><img src="https://ii.911cha.com/jieqi/s18.jpg" alt="霜降" width="170" height="106" /><br /><span class="f14 green">1年霜降时间</span><br />1年10月25日 11:05:16</a><a href="./jieqi19.html"><img src="https://ii.911cha.com/jieqi/s19.jpg" alt="立冬" width="170" height="106" /><br /><span class="f14 green">1年立冬时间</span><br />1年11月9日 05:25:56</a><a href="./jieqi20.html"><img src="https://ii.911cha.com/jieqi/s20.jpg" alt="小雪" width="170" height="106" /><br /><span class="f14 green">1年小雪时间</span><br />1年11月23日 22:38:36</a><a href="./jieqi21.html"><img src="https://ii.911cha.com/jieqi/s21.jpg" alt="大雪" width="170" height="106" /><br /><span class="f14 green">1年大雪时间</span><br />1年12月8日 15:14:30</a><a href="./jieqi22.html"><img src="https://ii.911cha.com/jieqi/s22.jpg" alt="冬至" width="170" height="106" /><br /><span class="f14 green">1年冬至时间</span><br />1年12月23日 08:19:28</a><a href="./jieqi23.html"><img src="https://ii.911cha.com/jieqi/s23.jpg" alt="小寒" width="170" height="106" /><br /><span class="f14 green">1年小寒时间</span><br />1年1月6日 20:41:46</a><a href="./jieqi24.html"><img src="https://ii.911cha.com/jieqi/s24.jpg" alt="大寒" width="170" height="106" /><br /><span class="f14 green">1年大寒时间</span><br />1年1月21日 16:39:41
        String infoStr = divContent.get(0).replaceAll(divStartTag, "").replaceAll(divEndTag, "");
        final JSONObject jsonObject = new JSONObject();
        //提取年份
        jsonObject.putOnce("年份", Integer.parseInt(infoStr.split("<span class=\"f14 green\">")[1].split("年")[0]));
        //以节气为单位分别处理：<a href="./jieqi1.html"><img src="https://ii.911cha.com/jieqi/s1.jpg" alt="立春" width="170" height="106" /><br /><span class="f14 green">1年立春时间</span><br />1年2月5日 15:18:39
        try {
            Arrays.stream(infoStr.split("</a>")).forEach(infoEleStr -> {
                //1年立春时间</span><br />1年2月5日 15:18:39
                String[] yearJieQiAndTime = infoEleStr.split("<span class=\"f14 green\">")[1].split(infoEleStr.contains("时间</span><br />") ? "时间</span><br />" : "时间</span><br>");
                String jieQi = yearJieQiAndTime[0].split("年")[1];
                //1年2月5日 15:18:39
                String timeStrWaitHandle = yearJieQiAndTime[1];
                //1 2月5日 15:18:39
                String[] yearSplit = timeStrWaitHandle.split("年");
                int year = Integer.parseInt(yearSplit[0]);
                //2 5日 15:18:39
                String[] monthSplit = yearSplit[1].split("月");
                int month = Integer.parseInt(monthSplit[0]);
                //5 15:18:39
                String[] daySplit = monthSplit[1].split("日 ");
                int day = Integer.parseInt(daySplit[0]);
                //15 18 39
                String[] timeSplit = daySplit[1].split(":");
                if (timeSplit[2].contains("<br")) {
                    timeSplit[2] = timeSplit[2].split("<br")[0];
                }
                jsonObject.putOnce(jieQi, LocalDateTime.of(year, month, day, Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]), Integer.parseInt(timeSplit[2])).format(dateTimeFormatter));
            });
        } catch (Exception e) {
            System.out.println("解析异常：" + htmlFilePath + "，" + e.getMessage());
            FileUtil.move(new File(htmlFilePath), new File(htmlFilePath.replaceAll(HTML_DIR_PATH, HTML_DIR_PATH_CANNOT_HANDLE)), true);
            FileUtil.del(htmlFilePath);
            return null;
        }
        return jsonObject;
    }

    /**
     * 日期不对，所有1月1日到立春的年份都得加一年
     */
    // @Test
    public void fixYear() {
        FileUtil.writeUtf8String(
                JSONUtil.parseArray(
                        JSONUtil.parseArray(FileUtil.readString(RESULT_FILE_PATH, StandardCharsets.UTF_8)).stream().peek(obj -> {
                            JSONObject jsonObject = (JSONObject) obj;
                            jsonObject.keySet().stream().filter(key ->
                                    !Objects.equals(key, "年份") && LocalDateTimeUtil.parse(jsonObject.getStr(key), dateTimeFormatter).isBefore(
                                            LocalDateTimeUtil.parse(jsonObject.getStr("立春"), dateTimeFormatter)
                                    )
                            ).forEach(key -> jsonObject.set(key, LocalDateTimeUtil.format(LocalDateTimeUtil.parse(jsonObject.getStr(key), dateTimeFormatter).plusYears(1L), dateTimeFormatter)));
                        }).collect(Collectors.toList())
                ).toString(),
                RESULT_FILE_PATH
        );
    }

}
