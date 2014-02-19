package com.qumoon.commons.algorithm.ac;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * 此类被匹配到的模式的一些信息。
 *
 * @author kevin
 */
public class PatternMatcher {
    private final Multimap<String, PatternMatchResult> patternMatchResults = HashMultimap
            .create();

    /**
     * @param pattern 模式字串
     * @param upper   模式字串在文本中的起始位置
     * @param lower   模式字串在文本中的结束位置
     * @return
     */
    public PatternMatcher addMatchPatterns(String pattern, int upper, int lower) {
        final PatternMatchResult patternMatchResult = new PatternMatchResult(
                pattern, upper, lower);
        patternMatchResults.put(pattern, patternMatchResult);
        return this;
    }

    /**
     * 统计某一模式在文本中出现的次数。
     *
     * @param pattern 模式字串
     * @return
     */
    public int occurrence(String pattern) {
        return patternMatchResults.get(pattern).size();
    }

    /**
     * 得到被匹配到的模式数量。
     *
     * @return 被匹配到的模式数量
     */
    public int size() {
        return patternMatchResults.size();
    }

    /**
     * 得到被匹配到的模式去重后的数量。
     *
     * @return 被匹配到的模式数量
     */
    public int deduplicateSize() {
        return patternMatchResults.keySet().size();
    }

    /**
     * 得到所有匹配到的模式字串。
     *
     * @return 模式字串
     */
    public Collection<String> getPatterns() {
        return patternMatchResults.keySet();
    }

    /**
     * 得到某一模式的所有匹配结果。
     *
     * @param pattern 模式字串
     * @return
     */
    public Collection<PatternMatchResult> getPatternMatchResult(String pattern) {
        return patternMatchResults.get(pattern);
    }

    /**
     * 得到所有模式的所有匹配结果。
     *
     * @return
     */
    public Collection<PatternMatchResult> getPatternMatchResults() {
        return patternMatchResults.values();
    }
}
