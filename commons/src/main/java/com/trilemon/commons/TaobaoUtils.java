/*
 * Copyright (c) 2013 Raycloud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trilemon.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.Files;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开放平台工具类
 *
 * @author kevin
 */
public class TaobaoUtils {
    public final static ObjectMapper jsonMapper = new ObjectMapper();
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
    private static Logger logger = LoggerFactory.getLogger(TaobaoUtils.class);
    private static Pattern ITEM_NUM_IID_PATTERN = Pattern.compile("(http://)*item.taobao.com/item\\.htm\\?.*id=" +
            "(\\d+).*");

    public static int getTbRate(String imgSrc) {
        String fileNameWithoutExt = Files.getNameWithoutExtension(imgSrc);
        return TAOBAO_RATES.get(fileNameWithoutExt.split("_", 2)[1]);
    }

    public static String getTbRateImgUrl(int rate) {
        String tag = TAOBAO_RATES.inverse().get(rate);
        return "http://a.tbcdn.cn/sys/common/icon/rank/b_" + tag + ".gif";
    }

    /**
     * 根据商品 url 获取 num iid
     *
     * @param url
     * @return
     */
    public static Long getItemUrlNumIid(String url) {
        Matcher matcher = ITEM_NUM_IID_PATTERN.matcher(url);
        if (matcher.find() && matcher.groupCount() > 1) {
            String numIidStr = matcher.group(2);
            if (NumberUtils.isNumber(numIidStr)) {
                return Long.valueOf(numIidStr);
            } else {
                return null;
            }
        }
        return null;
    }
}
