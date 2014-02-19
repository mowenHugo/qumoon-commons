package com.qumoon.commons.algorithm.ac;

/**
 * 封装了由AC75算法构造的trie树。提供了模式匹配操作。
 * @author kevin
 */
public class AcTrie {
    private final AcTrieNode root = new AcTrieNode('#');

    /**
     * 实现了AC75算法中的匹配部分。
     *
     * @param target
     *            　被匹配的目标文本
     * @return 一个{@link PatternMatcher}对象
     */
    public PatternMatcher match(String target) {
        final PatternMatcher matchPatterns = new PatternMatcher();
        final char[] targetCharArray = target.toCharArray();
        AcTrieNode current = root;
        for (int i = 0; i < targetCharArray.length; i++) {
            char currentChar = targetCharArray[i];
            AcTrieNode child = current.getChild(currentChar);
            while (null == child) {
                current = current.getSuffixNode();
                child = current.getChild(currentChar);
                if (current.isRoot())
                    break;
            }
            if (null == child)
                current = root;
            else
                current = child;
            if (current.isOutputNode())
                for (String word : current.getOutputWords())
                    matchPatterns.addMatchPatterns(word, i - word.length() + 1, i);
        }
        return matchPatterns;
    }

    /**
     * 得到trie树的根节点。
     *
     * @return trie树的根节点
     */
    public AcTrieNode getRootNode() {
        return root;
    }
}
