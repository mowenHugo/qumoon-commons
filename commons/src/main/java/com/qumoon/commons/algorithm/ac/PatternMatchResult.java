package com.qumoon.commons.algorithm.ac;

/**
 * 封装了被匹配到的模式。
 * <p/>
 * 可以得到的信息有模式字串和模式在文本中的位置（文本的下标从0开始）。
 *
 * @author kevin
 */
public class PatternMatchResult {

    private final String pattern;
    private final int[] bound = new int[2];

    public PatternMatchResult(String pattern, int upper, int lower) {
        this.pattern = pattern;
        bound[0] = upper;
        bound[1] = lower;
    }

    public String getPattern() {
        return pattern;
    }

    public int[] getBound() {
        return bound;
    }

}
