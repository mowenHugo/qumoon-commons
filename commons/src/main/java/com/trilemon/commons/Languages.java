package com.trilemon.commons;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author kevin
 */
public class Languages {
    public static String getHanYuPinyin(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = chinese.trim().toCharArray();
        StringBuffer output = new StringBuffer("");

        for (int i = 0; i < input.length; i++) {
            if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                output.append(temp[0]);
                output.append(" ");
            } else
                output.append(Character.toString(input[i]));
        }
        return output.toString();
    }
}
