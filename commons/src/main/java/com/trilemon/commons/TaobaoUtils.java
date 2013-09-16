package com.trilemon.commons;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.Files;

/**
 * @author kevin
 */
public class TaobaoUtils {
    private final static BiMap<String, Integer> TAOBAO_RATES = ImmutableBiMap.<String, Integer>builder()
            .put("无等级", 0)
            .put("1_1", 1)
            .put("1_2", 2)
            .put("1_3", 3)
            .put("1_4", 4)
            .put("1_5", 5)
            .put("2_1", 6)
            .put("2_2", 7)
            .put("2_3", 8)
            .put("2_4", 9)
            .put("2_5", 10)
            .put("3_1", 11)
            .put("3_2", 12)
            .put("3_3", 13)
            .put("3_4", 14)
            .put("3_5", 15)
            .put("4_1", 16)
            .put("4_2", 17)
            .put("4_3", 18)
            .put("4_4", 19)
            .put("4_5", 20)
            .build();

    public static int getTbRate(String imgSrc) {
        String fileNameWithoutExt = Files.getNameWithoutExtension(imgSrc);
        return TAOBAO_RATES.get(fileNameWithoutExt.split("_",2)[1]);
    }

    public static String getTbRateImgUrl(int rate) {
        String tag = TAOBAO_RATES.inverse().get(rate);
        return "http://a.tbcdn.cn/sys/common/icon/rank/b_" + tag + ".gif";
    }
}
