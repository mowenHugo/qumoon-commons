package com.trilemon.commons.algorithm.ac;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author kevin
 */
public class PatternMatcherTest {
    @Test
    public void testOccurrence() {
        PatternMatcher matcher = new PatternMatcher();
        matcher.addMatchPatterns("test", 0, 3).addMatchPatterns("est", 1, 3)
                .addMatchPatterns("test", 4, 7).addMatchPatterns("est", 5, 7);
        assertEquals(2, matcher.occurrence("test"));
        assertEquals(2, matcher.occurrence("est"));
    }
}
