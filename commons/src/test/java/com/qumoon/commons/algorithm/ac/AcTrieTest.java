package com.qumoon.commons.algorithm.ac;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author kevin
 */
public class AcTrieTest {

  @Test
  public void testMatch() {
    AcTrie acTrie = new AcTrie();
    AcTrieNode ab_a = new AcTrieNode('a');
    AcTrieNode ab_b = new AcTrieNode('b');
    ab_b.addOutputWord("ab");
    AcTrieNode b = new AcTrieNode('b');
    b.addOutputWord("b");
    acTrie.getRootNode().addChild(ab_a).addChild(ab_b);
    acTrie.getRootNode().addChild(b);

    ab_b.addOutputWord("b");
    acTrie.getRootNode().setSuffixNode(acTrie.getRootNode());
    ab_a.setSuffixNode(acTrie.getRootNode());
    b.setSuffixNode(acTrie.getRootNode());
    ab_b.setSuffixNode(b);

    String abab = "abab";
    PatternMatcher matcher = acTrie.match(abab);
    assertEquals(4, matcher.size());
    assertEquals(2, matcher.deduplicateSize());
    assertEquals(2, matcher.occurrence("b"));
    assertEquals(2, matcher.occurrence("ab"));

  }

  @Test
  public void testMatch_blankTrie() {
    AcTrie acTrie = new AcTrie();
    acTrie.getRootNode().setSuffixNode(acTrie.getRootNode());

    String ab = "abab";
    PatternMatcher matcher = acTrie.match(ab);
    assertEquals(0, matcher.size());
    assertEquals(0, matcher.deduplicateSize());

  }
}
